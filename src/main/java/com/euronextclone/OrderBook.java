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

    public OrderPrice getBestLimit()
    {
        return bestLimit.getOrderPrice();
    }

    public boolean match(final Order newOrder, final MatchingUnit.ContinuousTradingProcess mode)
    {
        final ArrayList<Order> rebalance = new ArrayList<Order>();

        for (final Order order : orders)
        {
            if (order.getPrice().hasPrice() && newOrder.getPrice().hasPrice() &&
                    order.getPrice().value() != newOrder.getPrice().value())
                continue;

            if (order.getQuantity() == newOrder.getQuantity())
            {
                orders.remove(order);
                generateTrade(newOrder, order, order.getQuantity(), newOrder.getPrice().value());
                newOrder.decrementQuantity(newOrder.getQuantity());
                break;
            }

            if (order.getQuantity() > newOrder.getQuantity())
            {
                if (!order.getPrice().hasPrice()) {
                    order.getPrice().convertToLimit(newOrder.getPrice().value());
                }

                order.decrementQuantity(newOrder.getQuantity());
                if (!newOrder.getPrice().hasPrice() && !order.getPrice().hasPrice()) {
                    newOrder.getPrice().update(bestLimit.getOrderPrice().value());
                    generateTrade(newOrder, order, newOrder.getQuantity(), newOrder.getPrice().value());
                } else {
                    generateTrade(newOrder, order, newOrder.getQuantity(), order.getPrice().value());
                }
                newOrder.decrementQuantity(newOrder.getQuantity());
                break;
            }

            if (order.getQuantity() < newOrder.getQuantity())
            {
                rebalance.add(order);
                if (!newOrder.getPrice().hasPrice() && newOrder.getPrice().getOrderType() == OrderType.MarketToLimit)
                    newOrder.getPrice().updateToLimitOrder(bestLimit.getOrderPrice().value());

                generateTrade(newOrder, order, order.getQuantity(), order.getPrice().value());


                newOrder.decrementQuantity(order.getQuantity());
            }
        }

        if(rebalance.size() > 0)
            orders.removeAll(rebalance);

        updatePegOrders();
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
        updatePegOrders();
    }

    public void add(final Order order)
    {
        placeOrderInBook(order);

        // First order in book
//        if (order.getPrice().getOrderType() instanceof MarketToLimit) {
//            if (!bestLimit.getOrderPrice().hasPrice() && order.getPrice().hasPrice()) {
//                bestLimit.getOrderPrice().update(order.getPrice().value());
//                return;
//            }
//        }

        updatePegOrders();
    }

    private void placeOrderInBook(final Order newOrder)
    {
        int count = 0;
        for (final Order order : orders)
        {
            int comparePrice = order.getPrice().compareTo(newOrder.getPrice());
            if(bookSide == Order.OrderSide.Buy && comparePrice < 0 || bookSide == Order.OrderSide.Sell && comparePrice > 0)
                break;
            count++;
        }

        orders.add(count, newOrder);
    }

    private void updatePegOrders()
    {
        if(orders.size() == 0)
            return;

        validateBook();

        final ArrayList<Order> rebalance = new ArrayList<Order>();

        for (Order order : orders)
        {
            if(order.getPrice().updateBestLimit(bestLimit.getOrderPrice()))
                rebalance.add(order);
        }

        if(rebalance.size() > 0)
        {
            orders.removeAll(rebalance);
            add(rebalance);
        }
    }

    private void validateBook()
    {
        if (orders.size() == 0)
            return;

        bestLimit.reset();

        if (!(orders.get(0)).getPrice().getOrderType().canBeTopOfBook())
        {
            for (final Order order : orders)
            {
                bestLimit.addQuantity(order.getQuantity());
                if(order.getPrice().getOrderType().canBeTopOfBook())
                {
                    bestLimit.getOrderPrice().update(order.getPrice().value());
                    orders.remove(order);
                    orders.add(0, order);
                    break;
                }
            }

            orders.clear();
        } else {
            // Order book should be good, just reset best
            for (final Order order : orders)
            {
                if (order.getPrice().hasPrice())
                {
                    bestLimit.getOrderPrice().update(order.getPrice().value());
                    bestLimit.addQuantity(order.getQuantity());
                    break;
                }
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
