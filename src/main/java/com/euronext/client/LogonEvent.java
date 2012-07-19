package com.euronext.client;

import quickfix.SessionID;

public class LogonEvent {
    private SessionID sessionID;
    private boolean loggedOn;

    public LogonEvent(SessionID sessionID, boolean loggedOn) {
        this.sessionID = sessionID;
        this.loggedOn = loggedOn;
    }

    public SessionID getSessionID() {
        return sessionID;
    }
    public boolean isLoggedOn() {
        return loggedOn;
    }
}
