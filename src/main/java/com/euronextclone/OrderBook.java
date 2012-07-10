package com.euronextclone;

import com.euronextclone.ordertypes.Limit;
import sun.beans.editors.DoubleEditor;

import java.util.*;

public class OrderBook
{
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
        final ArrayList rebalance = new ArrayList();

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
        System.out.println(String.format("Trade: Buy Broker %s, Sell Broker %s, Quantity %d, Price %f", new Object[] {
            bookSide != Order.OrderSide.Sell ? order.getBroker() : newOrder.getBroker(), bookSide != Order.OrderSide.Sell ? newOrder.getBroker() : order.getBroker(), Integer.valueOf(tradeQuantity), Double.valueOf(newOrder.getPrice().value())
        }));
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
        else
            bestLimit.update(orders.get(0).getPrice().value());
    }

    private void add(ArrayList<Order> rebalance)
    {
        for (Order order : rebalance) {
            add(order);
        }
    }

    public int orderBookDepth()
    {
        return orders.size();
    }

    public void dump()
    {
        for (Order order : orders)
            order.dump();
    }

    private final LinkedList<Order> orders = new LinkedList<Order>();
    private OrderPrice bestLimit;
    private final Order.OrderSide bookSide;
}
