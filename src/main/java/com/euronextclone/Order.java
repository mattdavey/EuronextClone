package com.euronextclone;

import java.util.concurrent.atomic.AtomicInteger;

public class Order
{
    enum OrderSide {Buy, Sell};

    public Order(String broker, int quantity, OrderPrice price, OrderSide side)
    {
        id = c.incrementAndGet();
        this.price = price;
        this.side = side;
        this.broker = broker;
        this.quantity = quantity;
    }

    public int getQuantity()
    {
        return quantity;
    }

    public String getBroker()
    {
        return broker;
    }

    public OrderSide getSide()
    {
        return side;
    }

    public int getId()
    {
        return id;
    }

    public void decrementQuantity(int quantity)
    {
        this.quantity -= quantity;
    }

    public OrderPrice getPrice()
    {
        return price;
    }

    public void dump()
    {
        System.out.println(String.format("%s %d %d %s", new Object[] {
            broker, Integer.valueOf(id), Integer.valueOf(quantity), price.toString()
        }));
    }

    private static AtomicInteger c = new AtomicInteger(0);
    private final OrderPrice price;
    private final OrderSide side;
    private final String broker;
    private int quantity;
    private final int id;
}
