package com.euronext.client;

import quickfix.*;
import quickfix.field.*;

import javax.swing.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;

public class FixClientApplication implements quickfix.Application {
    private ObservableLogon observableLogon = new ObservableLogon();
    private DefaultMessageFactory messageFactory = new DefaultMessageFactory();
    private boolean isAvailable = true;
    private boolean isMissingField;

    static private HashMap<SessionID, HashSet<ExecID>> execIDs = new HashMap<SessionID, HashSet<ExecID>>();

    public FixClientApplication() {
    }

    public void onCreate(SessionID sessionID) {
    }

    public void onLogon(SessionID sessionID) {
        observableLogon.logon(sessionID);
    }

    public void onLogout(SessionID sessionID) {
        observableLogon.logoff(sessionID);
    }

    public void toAdmin(quickfix.Message message, SessionID sessionID) {
    }

    public void toApp(quickfix.Message message, SessionID sessionID) throws DoNotSend {
    }

    public void fromAdmin(quickfix.Message message, SessionID sessionID) throws FieldNotFound,
            IncorrectDataFormat, IncorrectTagValue, RejectLogon {
    }

    public void fromApp(quickfix.Message message, SessionID sessionID) throws FieldNotFound,
            IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
        try {
            SwingUtilities.invokeLater(new MessageProcessor(message, sessionID));
        } catch (Exception e) {
        }
    }

    public class MessageProcessor implements Runnable {
        private quickfix.Message message;
        private SessionID sessionID;

        public MessageProcessor(quickfix.Message message, SessionID sessionID) {
            this.message = message;
            this.sessionID = sessionID;
        }

        public void run() {
            try {
                MsgType msgType = new MsgType();
                if (isAvailable) {
                    if (isMissingField) {
                        // For OpenFIX certification testing
                        sendBusinessReject(message, BusinessRejectReason.CONDITIONALLY_REQUIRED_FIELD_MISSING, "Conditionally required field missing");
                    }
                    else if (message.getHeader().isSetField(DeliverToCompID.FIELD)) {
                        // This is here to support OpenFIX certification
                        sendSessionReject(message, SessionRejectReason.COMPID_PROBLEM);
                    } else if (message.getHeader().getField(msgType).valueEquals("8")) {
                        executionReport(message, sessionID);
                    } else {
                        sendBusinessReject(message, BusinessRejectReason.UNSUPPORTED_MESSAGE_TYPE, "Unsupported Message Type");
                    }
                } else {
                    sendBusinessReject(message, BusinessRejectReason.APPLICATION_NOT_AVAILABLE,
                            "FixGatewayApplication not available");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void executionReport(Message message, SessionID sessionID) throws FieldNotFound {
        ExecID execID = (ExecID) message.getField(new ExecID());
        if (alreadyProcessed(execID, sessionID))
            return;
    }

    private void sendSessionReject(Message message, int rejectReason) throws FieldNotFound,
            SessionNotFound {
        Message reply = createMessage(message, MsgType.REJECT);
        reverseRoute(message, reply);
        String refSeqNum = message.getHeader().getString(MsgSeqNum.FIELD);
        reply.setString(RefSeqNum.FIELD, refSeqNum);
        reply.setString(RefMsgType.FIELD, message.getHeader().getString(MsgType.FIELD));
        reply.setInt(SessionRejectReason.FIELD, rejectReason);
        Session.sendToTarget(reply);
    }

    private void sendBusinessReject(Message message, int rejectReason, String rejectText)
            throws FieldNotFound, SessionNotFound {
        Message reply = createMessage(message, MsgType.BUSINESS_MESSAGE_REJECT);
        reverseRoute(message, reply);
        String refSeqNum = message.getHeader().getString(MsgSeqNum.FIELD);
        reply.setString(RefSeqNum.FIELD, refSeqNum);
        reply.setString(RefMsgType.FIELD, message.getHeader().getString(MsgType.FIELD));
        reply.setInt(BusinessRejectReason.FIELD, rejectReason);
        reply.setString(Text.FIELD, rejectText);
        Session.sendToTarget(reply);
    }

    private Message createMessage(Message message, String msgType) throws FieldNotFound {
        return messageFactory.create(message.getHeader().getString(BeginString.FIELD), msgType);
    }

    private void reverseRoute(Message message, Message reply) throws FieldNotFound {
        reply.getHeader().setString(SenderCompID.FIELD,
                message.getHeader().getString(TargetCompID.FIELD));
        reply.getHeader().setString(TargetCompID.FIELD,
                message.getHeader().getString(SenderCompID.FIELD));
    }

    private boolean alreadyProcessed(ExecID execID, SessionID sessionID) {
        HashSet<ExecID> set = execIDs.get(sessionID);
        if (set == null) {
            set = new HashSet<ExecID>();
            set.add(execID);
            execIDs.put(sessionID, set);
            return false;
        } else {
            if (set.contains(execID))
                return true;
            set.add(execID);
            return false;
        }
    }

    private void send(quickfix.Message message, SessionID sessionID) {
        try {
            Session.sendToTarget(message, sessionID);
        } catch (SessionNotFound e) {
            System.out.println(e);
        }
    }

    public void send42(SessionID sessionID) {
        quickfix.fix42.NewOrderSingle newOrderSingle = new quickfix.fix42.NewOrderSingle(
                new ClOrdID("1"), new HandlInst('1'), new Symbol("VOD"),
                new Side(Side.BUY), new TransactTime(), new OrdType(OrdType.LIMIT));
        newOrderSingle.set(new OrderQty(-12));
        newOrderSingle.setField(new Price(22.22));

        send(newOrderSingle, sessionID);
    }

    private static class ObservableLogon extends Observable {
        private HashSet<SessionID> set = new HashSet<SessionID>();

        public void logon(SessionID sessionID) {
            set.add(sessionID);
            setChanged();
            notifyObservers(new LogonEvent(sessionID, true));
            clearChanged();
        }

        public void logoff(SessionID sessionID) {
            set.remove(sessionID);
            setChanged();
            notifyObservers(new LogonEvent(sessionID, false));
            clearChanged();
        }
    }

    public void addLogonObserver(Observer observer) {
        observableLogon.addObserver(observer);
    }

    public void deleteLogonObserver(Observer observer) {
        observableLogon.deleteObserver(observer);
    }
}
