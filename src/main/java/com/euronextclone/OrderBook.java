package com.euronextclone;

import com.euronextclone.ordertypes.Limit;
import hu.akarnokd.reactive4java.reactive.DefaultObservable;
import hu.akarnokd.reactive4java.reactive.Observable;
import hu.akarnokd.reactive4java.reactive.Observer;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.LinkedList;

public class OrderBook implements Observable<Trade>
{
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

    public boolean match(final Order newOrder)
    {
        final ArrayList<Order> rebalance = new ArrayList<Order>();

        for (final Order order : orders)
        {
            if(order.getPrice().value() != newOrder.getPrice().value())
                continue;

            if(order.getQuantity() == newOrder.getQuantity())
            {
                orders.remove(order);
                generateTrade(newOrder, order, order.getQuantity());
                newOrder.decrementQuantity(newOrder.getQuantity());
                break;
            }
            if(order.getQuantity() > newOrder.getQuantity())
            {
                order.decrementQuantity(newOrder.getQuantity());
                if(!newOrder.getPrice().hasPrice())
                    newOrder.getPrice().update(bestLimit.getOrderPrice().value());

                generateTrade(newOrder, order, newOrder.getQuantity());
                newOrder.decrementQuantity(newOrder.getQuantity());
                break;
            }
            if(order.getQuantity() < newOrder.getQuantity())
            {
                rebalance.add(order);
                generateTrade(newOrder, order, order.getQuantity());
                newOrder.decrementQuantity(order.getQuantity());
            }
        }

        if(rebalance.size() > 0)
            orders.removeAll(rebalance);

        updatePegOrders();
        return newOrder.getQuantity() != 0;
    }

    private void generateTrade(final Order newOrder, final Order order, final int tradeQuantity)
    {
        notifier.next(new Trade(bookSide != Order.OrderSide.Sell ? order.getBroker() : newOrder.getBroker(), bookSide != Order.OrderSide.Sell ? newOrder.getBroker() : order.getBroker(), tradeQuantity, newOrder.getPrice().value()));
//        System.out.println(String.format("Trade: Buy Broker %s, Sell Broker %s, Quantity %d, Price %f", new Object[] {
//            bookSide != Order.OrderSide.Sell ? order.getBroker() : newOrder.getBroker(), bookSide != Order.OrderSide.Sell ? newOrder.getBroker() : order.getBroker(), Integer.valueOf(tradeQuantity), Double.valueOf(newOrder.getPrice().value())
//        }));
    }

    public void addOrder(final Order order)
    {
        add(order);
        if ((bookSide == Order.OrderSide.Buy && order.getPrice().value() > bestLimit.getOrderPrice().value()) ||
            (bookSide == Order.OrderSide.Sell && order.getPrice().value() < bestLimit.getOrderPrice().value()))
        {
            bestLimit.getOrderPrice().update(order.getPrice().value());
            updatePegOrders();
        }
    }

    private void add(final Order newOrder)
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

        checkForTopOfBookPeg();

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

    private void setBestLimit() {
        for (final Order order : orders) {
            if (order.getPrice().hasPrice()) {
                bestLimit.getOrderPrice().update(order.getPrice().value());
                break;
            }
        }
    }

    private void checkForTopOfBookPeg()
    {
//        setBestLimit();

        bestLimit.resetQuantity();

        if(!(orders.get(0)).getPrice().getOrderType().canBeTopOfBook())
        {
            for (final Order order : orders)
            {
                bestLimit.addQuantity(order.getQuantity());
                if(order.getPrice().getOrderType().canBeTopOfBook())
                {
                    bestLimit.getOrderPrice().update(order.getPrice().value());
                    orders.remove(order);
                    orders.add(0, order);
                    return;
                }
            }
            orders.clear();
        } else {
            bestLimit.getOrderPrice().update(orders.get(0).getPrice().value());
            bestLimit.addQuantity(orders.get(0).getQuantity());
        }
    }

    private void add(final ArrayList<Order> rebalance)
    {
        for (final Order order : rebalance) {
            add(order);
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

    private final LinkedList<Order> orders = new LinkedList<Order>();
    private IndicativeMatchPrice bestLimit;
    private final Order.OrderSide bookSide;

    public Closeable register(Observer<? super Trade> observer) {
        return notifier.register(observer);
    }

    public IndicativeMatchPrice getIMP() {
        return bestLimit;
    }
}
