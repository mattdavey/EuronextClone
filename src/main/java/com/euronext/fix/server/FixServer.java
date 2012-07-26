package com.euronext.fix.server;

import com.euronext.fix.FixAdapter;
import quickfix.*;

/**
 * Created with IntelliJ IDEA.
 * User: eprystupa
 * Date: 7/25/12
 * Time: 10:38 PM
 */
public class FixServer extends FixAdapter {

    private final SocketAcceptor socketAcceptor;

    public FixServer(final SessionSettings settings) throws ConfigError {
        MessageStoreFactory messageStoreFactory = new FileStoreFactory(settings);
        SLF4JLogFactory logFactory = new SLF4JLogFactory(settings);
        MessageFactory messageFactory = new DefaultMessageFactory();

        socketAcceptor = new SocketAcceptor(this, messageStoreFactory, settings, logFactory, messageFactory);
    }

    public void start() throws ConfigError {
        socketAcceptor.start();
    }

    public void stop() {
        socketAcceptor.stop();
    }
}
