package com.euronext.fix;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.*;
import quickfix.fix42.MessageCracker;

/**
 * Created with IntelliJ IDEA.
 * User: eprystupa
 * Date: 7/24/12
 * Time: 10:30 PM
 */
public class FixAdapter extends MessageCracker implements Application {

    private static Logger logger = LoggerFactory.getLogger(FixAdapter.class);

    private SessionID theOnlySessionId;

    @Override
    public void onCreate(SessionID sessionId) {
        logger.info("Session created: {}", sessionId);
        theOnlySessionId = sessionId;

        Session.lookupSession(theOnlySessionId).logon();
    }

    @Override
    public void onLogon(SessionID sessionId) {
        logger.debug("Logon for session: {}", sessionId);
    }

    @Override
    public void onLogout(SessionID sessionId) {
        logger.debug("Logout for session: {}", sessionId);
    }

    @Override
    public void toAdmin(Message message, SessionID sessionId) {
        logger.debug("toAdmin for session: {}, message: {}", sessionId, message);
    }

    @Override
    public void fromAdmin(Message message, SessionID sessionId) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
        logger.debug("fromAdmin for session: {}, message: {}", sessionId, message);
    }

    @Override
    public void toApp(Message message, SessionID sessionId) throws DoNotSend {
        logger.debug("toApp for session: {}, message: {}", sessionId, message);
    }

    @Override
    public void fromApp(Message message, SessionID sessionId) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
        logger.debug("fromApp for session: {}, message: {}", sessionId, message);
        crack(message, sessionId);
    }

    public boolean send(Message message) throws SessionNotFound {
        return Session.sendToTarget(message, theOnlySessionId);
    }
}
