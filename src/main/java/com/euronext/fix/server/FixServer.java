package com.euronext.fix.server;

import com.euronext.fix.FixAdapter;
import com.euronextclone.MatchingUnit;
import quickfix.*;
import quickfix.fix42.NewOrderSingle;

/**
 * Created with IntelliJ IDEA.
 * User: eprystupa
 * Date: 7/25/12
 * Time: 10:38 PM
 */
public class FixServer extends FixAdapter {

    private final SocketAcceptor socketAcceptor;
    // TODO: this is temporary. Matching units will be outside of FIX server, likely in a different process
    private final MatchingUnit matchingUnit;

    public FixServer(final SessionSettings settings) throws ConfigError {
        MessageStoreFactory messageStoreFactory = new FileStoreFactory(settings);
        SLF4JLogFactory logFactory = new SLF4JLogFactory(settings);
        MessageFactory messageFactory = new DefaultMessageFactory();

        socketAcceptor = new SocketAcceptor(this, messageStoreFactory, settings, logFactory, messageFactory);

        matchingUnit = new MatchingUnit();
    }

    public void start() throws ConfigError {
        socketAcceptor.start();
    }

    public void stop() {
        socketAcceptor.stop();
    }

    @Override
    public void onMessage(NewOrderSingle message, SessionID sessionID) throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {

//        matchingUnit.addOrder();
    }
}
