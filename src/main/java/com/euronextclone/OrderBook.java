package com.euronextclone;

import hu.akarnokd.reactive4java.reactive.DefaultObservable;
import hu.akarnokd.reactive4java.reactive.Observable;
import hu.akarnokd.reactive4java.reactive.Observer;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class OrderBook implements Observable<Trade>
{
    private final LinkedList<Order> orders = new LinkedList<Order>();
    private IndicativeMatchPrice bestLimit;
    private final Order.OrderSide bookSide;

    /** The observable helper. */
    final private DefaultObservable<Trade> notifier = new DefaultObservable<Trade>();

    public OrderBook(final Order.OrderSide side)
    {
        bookSide = side;
        bestLimit = new IndicativeMatchPrice(side);
    }

    public OrderTypeLimit getBestLimit()
    {
        return bestLimit.getOrderPrice();
    }

    public boolean match(final Order newOrder, final MatchingUnit.ContinuousTradingProcess mode)
    {
        final ArrayList<Order> rebalance = new ArrayList<Order>();

        for (final Order order : orders)
        {
            if (order.getOrderTypeLimit().value(bestLimit) != newOrder.getOrderTypeLimit().value(bestLimit)) {
                if (newOrder.getOrderTypeLimit().getOrderType() == OrderType.MarketOrder &&
                        newOrder.getPartlyFilled() &&
                        order.getOrderTypeLimit().getOrderType() == OrderType.Limit) {
                    // Rule 1 of Pure Market Order continuous trading
                } else
                    continue;
            }

            if (order.getQuantity() == newOrder.getQuantity())
            {
                orders.remove(order);
                generateTrade(newOrder, order, order.getQuantity(), newOrder.getOrderTypeLimit().getLimit());
                newOrder.decrementQuantity(newOrder.getQuantity());
                break;
            }

            if (order.getQuantity() > newOrder.getQuantity())
            {
                if (!order.getOrderTypeLimit().hasLimit()) {
                    order.getOrderTypeLimit().convertToLimit(newOrder.getOrderTypeLimit().getLimit());
                }

                order.decrementQuantity(newOrder.getQuantity());
                if (!newOrder.getOrderTypeLimit().hasLimit() && !order.getOrderTypeLimit().hasLimit()) {
                    generateTrade(newOrder, order, newOrder.getQuantity(), newOrder.getOrderTypeLimit().getLimit());
                } else {
                    generateTrade(newOrder, order, newOrder.getQuantity(), order.getOrderTypeLimit().getLimit());
                }
                newOrder.decrementQuantity(newOrder.getQuantity());
                break;
            }

            if (order.getQuantity() < newOrder.getQuantity())
            {
                rebalance.add(order);
                if (!newOrder.getOrderTypeLimit().hasLimit() && newOrder.getOrderTypeLimit().getOrderType() == OrderType.MarketToLimit)
                    newOrder.getOrderTypeLimit().updateToLimitOrder(bestLimit.getOrderPrice().getLimit());

                generateTrade(newOrder, order, order.getQuantity(), order.getOrderTypeLimit().getLimit());
                newOrder.decrementQuantity(order.getQuantity());
                order.decrementQuantity(order.getQuantity());
            }
        }

        if(rebalance.size() > 0)
            orders.removeAll(rebalance);

        validatePegOrderPositions();
        return newOrder.getQuantity() != 0;
    }

    private void generateTrade(final Order newOrder, final Order order, final int tradeQuantity, final double price)
    {
        notifier.next(new Trade(newOrder.getSide() == Order.OrderSide.Buy ? newOrder.getBroker() : order.getBroker(),
                newOrder.getSide() == Order.OrderSide.Sell ? newOrder.getBroker() : order.getBroker(),
                tradeQuantity, price));
    }


    public void remove(Order order) {
        orders.remove(order);
        validatePegOrderPositions();
    }

    public void add(final Order order)
    {
        placeOrderInBook(order);
        validatePegOrderPositions();
    }

    private void placeOrderInBook(final Order newOrder)
    {
        int count = 0;
        for (final Order order : orders)
        {
            if (newOrder.getOrderTypeLimit().getOrderType() == OrderType.Peg && 0 == count) {
                count++;
                continue;
            }

            final int compare = order.compareTo(newOrder, bestLimit);
            if (bookSide == Order.OrderSide.Buy && compare < 0 || bookSide == Order.OrderSide.Sell && compare < 0) {
                break;
            }

            count++;
        }

        orders.add(count, newOrder);
    }

    private void validatePegOrderPositions()
    {
        if(orders.size() == 0)
            return;

        calculateBestLimit();

        boolean rerun = true;
        while (!rerun) {
            rerun = false;
            Order last = null;
            for (Order order : orders)
            {
                if (last == null)
                {
                    last = order;
                    continue;
                }

                if (last.compareTo(order, this.getIMP()) < 0) {
                    orders.remove(order);
                    add(order);
                    rerun = true;
                    break;
                }

                last = order;
            }
        }

        // Cheat if the order book only has a PEG left
        if (orders.size() == 1 && orders.get(0).getOrderTypeLimit().getOrderType() == OrderType.Peg)
            orders.clear();

    }

    private void calculateBestLimit()
    {
        if (orders.size() == 0)
            return;

        bestLimit.reset();

        // Order book should be good, just reset best
        for (final Order order : orders)
        {
            if (order.getOrderTypeLimit().hasLimit() && order.getQuantity() != 0)
            {
                bestLimit.getOrderPrice().updateToLimitOrder(order.getOrderTypeLimit().getLimit());
                bestLimit.addQuantity(order.getQuantity());
                break;
            }
        }
    }

    private void add(final ArrayList<Order> rebalance)
    {
        for (final Order order : rebalance) {
            placeOrderInBook(order);
        }
    }

    public int orderBookDepth()
    {
        return orders.size();
    }

    public void dump()
    {
        for (final Order order : orders)
            order.dump();
    }

    public Closeable register(Observer<? super Trade> observer) {
        return notifier.register(observer);
    }

    public IndicativeMatchPrice getIMP() {
        return bestLimit;
    }

    public List<Order> getOrders() {
        return orders;
    }
}
