package com.euronext.fix.server;

import com.euronext.fix.FixAdapter;
import com.euronextclone.MatchingUnit;
import com.euronextclone.OrderEntry;
import com.euronextclone.Trade;
import hu.akarnokd.reactive4java.reactive.Observer;
import quickfix.*;
import quickfix.field.ExecID;
import quickfix.field.OrderID;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.fix42.ExecutionReport;
import quickfix.fix42.NewOrderSingle;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
    // TODO: this is temporary until symbol is added to OrderEntry
    private final Symbol symbol = new Symbol("MSFT");


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
        try {
            sendExecutionReport(trade, new Side(Side.BUY));
            sendExecutionReport(trade, new Side(Side.SELL));
        } catch (SessionNotFound sessionNotFound) {
            sessionNotFound.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
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

    private void sendExecutionReport(Trade trade, Side side) throws SessionNotFound {
        final boolean buy = side.getValue() == Side.BUY;
        final String orderId = buy ? trade.getBuyOrderId() : trade.getSellOrderId();
        final String broker = buy ? trade.getBuyBroker() : trade.getSellBroker();
        final SessionID sessionID = sessionByBroker.get(broker);
        if (sessionID != null) {
            ExecutionReport executionReport = new ExecutionReport(new OrderID(orderId), generateExecId(), null, null, null, symbol, side, null, null, null);
            Session.sendToTarget(executionReport, sessionID);
        }
    }

    private ExecID generateExecId() {
        return new ExecID(UUID.randomUUID().toString());
    }
}
