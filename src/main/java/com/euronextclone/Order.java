package com.euronextclone;

import java.util.concurrent.atomic.AtomicInteger;

public class Order
{
    public enum OrderSide {Buy, Sell};

    public Order(final String broker, final int quantity, final OrderPrice price, final OrderSide side)
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

    public void decrementQuantity(final int quantity)
    {
        this.quantity -= quantity;
    }

    public OrderPrice getPrice()
    {
        return price;
    }

    public void dump()
    {
        System.out.println(String.format("%s %d %d %s", broker, id, quantity, price));
    }

    private static AtomicInteger c = new AtomicInteger(0);
    private final OrderPrice price;
    private final OrderSide side;
    private final String broker;
    private int quantity;
    private final int id;
}
