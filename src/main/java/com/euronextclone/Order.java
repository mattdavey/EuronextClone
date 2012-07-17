package com.euronextclone;

import java.util.concurrent.atomic.AtomicInteger;

public class Order
{
    public int compareTo(final Order anOrder, final BestLimit bestLimit) {
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;

        if (getId() == anOrder.getId())
            return EQUAL;

        final int comparePrice = comparePrice(anOrder, bestLimit);
        if (comparePrice == EQUAL) {
            // Time after orderTypeLimit compare
            if (getId() > anOrder.getId()) {
               return BEFORE;
            } else if (getId() < anOrder.getId()) {
                return AFTER;
            }
        }

        return comparePrice;
    }

    private int comparePrice(final Order anOrder, final BestLimit bestLimit) {
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;

        final double thisOrderLimit = getOrderTypeLimit().value(bestLimit);
        final double anOrderLimit = anOrder.getOrderTypeLimit().value(bestLimit);

        switch (side) {
            case Buy:
                if (thisOrderLimit > anOrderLimit) {
                    return AFTER;
                } else if (thisOrderLimit < anOrderLimit) {
                    return BEFORE;
                }
                break;
            case Sell:
                if (thisOrderLimit < anOrderLimit) {
                    return AFTER;
                } else if (thisOrderLimit > anOrderLimit) {
                    return BEFORE;
                }
                break;
        }

        return EQUAL;
    }

    public boolean getPartlyFilled() {
        return quantity != originalQuantity;
    }

    public enum OrderSide {Buy, Sell};

    public Order(final String broker, final int quantity, final OrderTypeLimit orderTypeLimit, final OrderSide side)
    {
        id = c.incrementAndGet();
        this.orderTypeLimit = orderTypeLimit;
        this.side = side;
        this.broker = broker;
        this.quantity = quantity;
        this.originalQuantity = quantity;
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

    public OrderTypeLimit getOrderTypeLimit()
    {
        return orderTypeLimit;
    }

    public void dump()
    {
        System.out.println(String.format("%s %d %d %s", broker, id, quantity, orderTypeLimit));
    }

    private static AtomicInteger c = new AtomicInteger(0);
    private final OrderTypeLimit orderTypeLimit;
    private final OrderSide side;
    private final String broker;
    private int quantity;
    private final int originalQuantity;
    private final int id;
}
