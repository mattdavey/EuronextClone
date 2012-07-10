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
    DefaultObservable<Trade> notifier = new DefaultObservable<Trade>();

    public OrderBook(Order.OrderSide buy)
    {
        bookSide = buy;
        bestLimit = new OrderPrice(new Limit(), buy != Order.OrderSide.Buy ? Double.MAX_VALUE: Double.MIN_VALUE);
    }

    public OrderPrice getBestLimit()
    {
        return bestLimit;
    }

    public boolean match(Order newOrder)
    {
        final ArrayList<Order> rebalance = new ArrayList<Order>();

        for (Order order : orders)
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
                    newOrder.getPrice().update(bestLimit.value());
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

    private void generateTrade(Order newOrder, Order order, int tradeQuantity)
    {
        notifier.next(new Trade(bookSide != Order.OrderSide.Sell ? order.getBroker() : newOrder.getBroker(), bookSide != Order.OrderSide.Sell ? newOrder.getBroker() : order.getBroker(), tradeQuantity, newOrder.getPrice().value()));
//        System.out.println(String.format("Trade: Buy Broker %s, Sell Broker %s, Quantity %d, Price %f", new Object[] {
//            bookSide != Order.OrderSide.Sell ? order.getBroker() : newOrder.getBroker(), bookSide != Order.OrderSide.Sell ? newOrder.getBroker() : order.getBroker(), Integer.valueOf(tradeQuantity), Double.valueOf(newOrder.getPrice().value())
//        }));
    }

    public void addOrder(Order order)
    {
        add(order);
        if(bookSide == Order.OrderSide.Buy && order.getPrice().value() > bestLimit.value())
        {
            bestLimit = order.getPrice();
            updatePegOrders();
        } else
        if(bookSide == Order.OrderSide.Sell && order.getPrice().value() < bestLimit.value())
        {
            bestLimit = order.getPrice();
            updatePegOrders();
        }
    }

    private void add(Order newOrder)
    {
        int count = 0;
        for (Order order : orders)
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
        setBestLimit();

        final ArrayList<Order> rebalance = new ArrayList<Order>();

        for (Order order : orders)
        {
            if(order.getPrice().updateBestLimit(bestLimit))
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
                bestLimit.update(order.getPrice().value());
                break;
            }
        }
    }

    private void checkForTopOfBookPeg()
    {
        if(!(orders.get(0)).getPrice().getOrderType().canBeTopOfBook())
        {
            for (Order order : orders)
            {
                if(order.getPrice().getOrderType().canBeTopOfBook())
                {
                    bestLimit.update(order.getPrice().value());
                    orders.remove(order);
                    orders.add(0, order);
                    return;
                }
            }
            orders.clear();
        }
    }

    private void add(ArrayList<Order> rebalance)
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
    private OrderPrice bestLimit;
    private final Order.OrderSide bookSide;

    public Closeable register(Observer<? super Trade> observer) {
        return notifier.register(observer);
    }
}
