package com.euronext.fix.server;

import com.euronext.fix.FixAdapter;
import com.euronextclone.MatchingUnit;
import com.euronextclone.OrderEntry;
import com.euronextclone.OrderSide;
import com.euronextclone.Trade;
import hu.akarnokd.reactive4java.reactive.Observer;
import quickfix.*;
import quickfix.fix42.NewOrderSingle;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: eprystupa
 * Date: 7/25/12
 * Time: 10:38 PM
 */
public class FixServer extends FixAdapter implements Observer<Trade> {

    private final SocketAcceptor socketAcceptor;
    private Map<String, SessionID> sessionByBroker;
    // TODO: this is temporary. Matching units will be outside of FIX server, likely in a different process
    private final MatchingUnit matchingUnit;

    // TODO: to correctly route execution reports to relevant party looks like I need:
    // a) a mapping from OrderId to Broker (TargetCompID)
    // b) a mapping from Broker (TargetCompId) to SessionID

    public FixServer(final SessionSettings settings) throws ConfigError {
        MessageStoreFactory messageStoreFactory = new FileStoreFactory(settings);
        SLF4JLogFactory logFactory = new SLF4JLogFactory(settings);
        MessageFactory messageFactory = new DefaultMessageFactory();

        socketAcceptor = new SocketAcceptor(this, messageStoreFactory, settings, logFactory, messageFactory);
        sessionByBroker = new HashMap<String, SessionID>();

        matchingUnit = new MatchingUnit();
        matchingUnit.register(this);
    }

    public void start() throws ConfigError {
        socketAcceptor.start();
    }

    public void stop() {
        socketAcceptor.stop();
    }

    @Override
    public void onCreate(SessionID sessionId) {
        sessionByBroker.put(sessionId.getTargetCompID(), sessionId);
    }

    @Override
    public void onMessage(NewOrderSingle message, SessionID sessionID) throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {

        String broker = sessionID.getTargetCompID();
        OrderEntry orderEntry = convertToOrderEntry(message, broker);
        matchingUnit.addOrder(orderEntry);
    }

    @Override
    public void next(Trade trade) {
        sendExecutionReport(trade, OrderSide.Buy);
        sendExecutionReport(trade, OrderSide.Sell);
    }

    @Override
    public void error(@Nonnull Throwable throwable) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void finish() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private OrderEntry convertToOrderEntry(NewOrderSingle orderSingle, String broker) {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }

    private void sendExecutionReport(Trade trade, OrderSide side) {
        final String broker = side == OrderSide.Buy ? trade.getBuyBroker() : trade.getSellBroker();
        sessionByBroker.get(broker);
    }
}
