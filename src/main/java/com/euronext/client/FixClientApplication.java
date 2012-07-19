package com.euronext.client;

import quickfix.*;
import quickfix.field.*;

import javax.swing.*;
import java.util.HashMap;
import java.util.HashSet;

public class FixClientApplication implements quickfix.Application {
    private DefaultMessageFactory messageFactory = new DefaultMessageFactory();
    private boolean isAvailable = true;
    private boolean isMissingField;

    static private HashMap<SessionID, HashSet<ExecID>> execIDs = new HashMap<SessionID, HashSet<ExecID>>();

    public FixClientApplication() {
    }

    public void onCreate(SessionID sessionID) {
    }

    public void onLogon(SessionID sessionID) {
    }

    public void onLogout(SessionID sessionID) {
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
                    } else {
                        sendBusinessReject(message, BusinessRejectReason.UNSUPPORTED_MESSAGE_TYPE,
                                "Unsupported Message Type");
                    }
                } else {
                    sendBusinessReject(message, BusinessRejectReason.APPLICATION_NOT_AVAILABLE,
                            "Application not available");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
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

    public boolean isMissingField() {
        return isMissingField;
    }

    public void setMissingField(boolean isMissingField) {
        this.isMissingField = isMissingField;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }
}
