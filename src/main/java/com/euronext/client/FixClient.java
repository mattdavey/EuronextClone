package com.euronext.client;

import org.quickfixj.jmx.JmxExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.*;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CountDownLatch;

public class FixClient implements Observer {
    private static final CountDownLatch shutdownLatch = new CountDownLatch(1);

    /** enable logging for this class */
    private static Logger log = LoggerFactory.getLogger(FixClient.class);
    private static FixClient fixClient;
    private boolean initiatorStarted = false;
    private Initiator initiator = null;
    private FixClientApplication application;

    public void init(String[] args) throws Exception {
        InputStream inputStream = null;
        if (args.length == 0) {
            inputStream = FixClient.class.getResourceAsStream("FixBrokerA.cfg");
        } else if (args.length == 1) {
            inputStream = new FileInputStream(args[0]);
        }

        if (inputStream == null) {
            System.out.println("usage: " + FixClient.class.getName() + " [configFile].");
            return;
        }
        SessionSettings settings = new SessionSettings(inputStream);
        inputStream.close();

        boolean logHeartbeats = Boolean.valueOf(System.getProperty("logHeartbeats", "true"));

        application = new FixClientApplication();
        MessageStoreFactory messageStoreFactory = new FileStoreFactory(settings);
        LogFactory logFactory = new ScreenLogFactory(true, true, true, logHeartbeats);
        MessageFactory messageFactory = new DefaultMessageFactory();

        initiator = new SocketInitiator(application, messageStoreFactory, settings, logFactory,
                messageFactory);

        JmxExporter exporter = new JmxExporter();
        exporter.register(initiator);

        application.addLogonObserver(this);
    }

    public synchronized void logon() {
        if (!initiatorStarted) {
            try {
                initiator.start();
                initiatorStarted = true;
            } catch (Exception e) {
                log.error("Logon failed", e);
            }
        } else {
            for (SessionID sessionId : initiator.getSessions()) {
                Session.lookupSession(sessionId).logon();
            }
        }
    }

    public void logout() {
        for (SessionID sessionId : initiator.getSessions()) {
            Session.lookupSession(sessionId).logout("user requested");
        }
    }

    public void stop() {
        shutdownLatch.countDown();
    }

    public void run(String args[]) throws Exception {
        fixClient = new FixClient();
        fixClient.init(args);
        if (!System.getProperties().containsKey("openfix")) {
            fixClient.logon();
        }
        shutdownLatch.await();
    }

    public static void main(String args[]) throws Exception {
        new FixClient().run(args);
    }

    @Override
    public void update(Observable observable, Object arg) {
        LogonEvent logonEvent = (LogonEvent)arg;
        if(logonEvent.isLoggedOn()){
            application.send42(logonEvent.getSessionID());
        }
    }
}
