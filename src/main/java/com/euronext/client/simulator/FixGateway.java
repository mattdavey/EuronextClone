package com.euronext.client.simulator;

import quickfix.*;


/**
 * Created with IntelliJ IDEA.
 * User: eprystupa
 * Date: 7/25/12
 * Time: 8:28 PM
 */
public class FixGateway extends FixAdapter {

    private final SocketInitiator socketInitiator;

    public FixGateway(final SessionSettings settings) throws ConfigError {

        MessageStoreFactory messageStoreFactory = new FileStoreFactory(settings);
        SLF4JLogFactory logFactory = new SLF4JLogFactory(settings);
        MessageFactory messageFactory = new DefaultMessageFactory();
        socketInitiator = new SocketInitiator(this, messageStoreFactory, settings, logFactory, messageFactory);
    }

    public void start() throws ConfigError {
        socketInitiator.start();

    }

    public void stop() {
        socketInitiator.stop();
    }
}
