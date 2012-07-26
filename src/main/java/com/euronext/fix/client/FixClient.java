package com.euronext.fix.client;

import com.euronext.fix.FixAdapter;
import quickfix.*;
import quickfix.fix42.NewOrderSingle;


/**
 * Created with IntelliJ IDEA.
 * User: eprystupa
 * Date: 7/25/12
 * Time: 8:28 PM
 */
public class FixClient extends FixAdapter {

    private final SocketInitiator socketInitiator;

    public FixClient(final SessionSettings settings) throws ConfigError {

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

    public void submitOrder(NewOrderSingle order) throws SessionNotFound {
        send(order);
    }
}
