package com.euronext.fix.server;

import com.euronext.fix.FixAdapter;
import com.euronextclone.*;
import com.euronextclone.ordertypes.Limit;
import hu.akarnokd.reactive4java.reactive.Observer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.*;
import quickfix.field.*;
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

    private static Logger logger = LoggerFactory.getLogger(FixServer.class);

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

        matchingUnit = new MatchingUnit("MSFT");
        matchingUnit.register(this);
        matchingUnit.setTradingMode(TradingMode.Continuous);
        matchingUnit.setTradingPhase(TradingPhase.CoreContinuous);
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
    public void onMessage(NewOrderSingle order, SessionID sessionID) throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {

        String broker = sessionID.getTargetCompID();
        OrderEntry orderEntry = convertToOrderEntry(order, broker);
        acceptOrder(orderEntry);
        matchingUnit.addOrder(orderEntry);
    }

    @Override
    public void next(Trade trade) {
        try {
            sendExecutionReport(trade, new Side(Side.BUY));
            sendExecutionReport(trade, new Side(Side.SELL));
        } catch (SessionNotFound sessionNotFound) {
            logger.error(sessionNotFound.getMessage(), sessionNotFound);
        }
    }

    @Override
    public void error(@Nonnull Throwable throwable) {
        throw new RuntimeError(throwable);
    }

    @Override
    public void finish() {
        // Nothing to do
    }

    private OrderEntry convertToOrderEntry(NewOrderSingle orderSingle, String broker) throws FieldNotFound {
        OrderSide side = orderSingle.getSide().getValue() == Side.BUY ? OrderSide.Buy : OrderSide.Sell;
        return new OrderEntry(
                side,
                broker,
                (int) orderSingle.getOrderQty().getValue(),
                new Limit(orderSingle.getPrice().getValue()));
    }

    private ExecutionReport buildExecutionReport(final String orderId,
                                                 final ExecTransType execTransType,
                                                 final ExecType execType,
                                                 final OrdStatus ordStatus,
                                                 final Side side) {

        return new ExecutionReport(
                new OrderID(orderId),
                generateExecId(),
                execTransType,
                execType,
                ordStatus,
                symbol,
                side,
                new LeavesQty(),
                new CumQty(),
                new AvgPx());
    }

    private void acceptOrder(final OrderEntry orderEntry) {
        final String broker = orderEntry.getBroker();
        final SessionID sessionID = sessionByBroker.get(broker);

        if (sessionID != null) {
            final Side side = new Side(orderEntry.getSide() == OrderSide.Buy ? Side.BUY : Side.SELL);
            final ExecutionReport report = buildExecutionReport(
                    orderEntry.getOrderId(),
                    new ExecTransType(ExecTransType.NEW),
                    new ExecType(ExecType.NEW),
                    new OrdStatus(OrdStatus.NEW),
                    side);
            sendToTarget(report, sessionID);
        }
    }

    private boolean sendToTarget(ExecutionReport report, SessionID sessionID) {
        try {
            return Session.sendToTarget(report, sessionID);
        } catch (SessionNotFound sessionNotFound) {
            throw new RuntimeError(sessionNotFound);
        }
    }

    private void sendExecutionReport(Trade trade, Side side) throws SessionNotFound {
        final boolean buy = side.getValue() == Side.BUY;
        final String orderId = buy ? trade.getBuyOrderId() : trade.getSellOrderId();
        final String broker = buy ? trade.getBuyBroker() : trade.getSellBroker();
        final SessionID sessionID = sessionByBroker.get(broker);
        if (sessionID != null) {
            ExecutionReport executionReport = new ExecutionReport(
                    new OrderID(orderId),
                    generateExecId(),
                    new ExecTransType(ExecTransType.STATUS),
                    new ExecType(ExecType.PARTIAL_FILL),
                    new OrdStatus(OrdStatus.PARTIALLY_FILLED),
                    symbol,
                    side,
                    new LeavesQty(),
                    new CumQty(),
                    new AvgPx());

            executionReport.set(new LastShares(trade.getQuantity()));
            executionReport.set(new LastPx(trade.getPrice()));
            sendToTarget(executionReport, sessionID);
        }
    }

    private ExecID generateExecId() {
        return new ExecID(UUID.randomUUID().toString());
    }
}
