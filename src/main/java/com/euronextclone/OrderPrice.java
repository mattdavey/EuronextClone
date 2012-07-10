package com.euronextclone;

import com.euronextclone.ordertypes.OrderType;

public class OrderPrice
    implements Comparable
{

    public OrderPrice(OrderType orderType)
    {
        price = 0.0D;
        hasPrice = false;
        limit = 0.0D;
        hasLimit = false;
        this.orderType = orderType;
    }

    public OrderPrice(OrderType orderType, double price)
    {
        this.price = 0.0D;
        hasPrice = false;
        limit = 0.0D;
        hasLimit = false;
        this.orderType = orderType;
        update(price);
    }

    public OrderPrice(OrderType orderType, double price, double limit)
    {
        this.price = 0.0D;
        hasPrice = false;
        this.limit = 0.0D;
        hasLimit = false;
        this.limit = limit;
        hasLimit = true;
        this.orderType = orderType;
        update(price);
    }

    public void update(double value)
    {
        price = value;
        hasPrice = true;
    }

    public OrderType getOrderType()
    {
        return orderType;
    }

    public double getLimit()
    {
        return limit;
    }

    public boolean hasPrice()
    {
        return hasPrice;
    }

    public boolean hasLimit()
    {
        return hasLimit;
    }

    public double value()
    {
        return price;
    }

    public String toString()
    {
        return orderType.format(price, limit);
    }

    public boolean updateBestLimit(OrderPrice bestLimit)
    {
        return orderType.markToBestLimit(this, bestLimit);
    }

    public int compareTo(OrderPrice passedOrderPrice)
    {
        int orderTypeCompare = orderType.compareTo(passedOrderPrice.getOrderType());
        if(orderTypeCompare == 0)
        {
            if(hasPrice() && passedOrderPrice.hasPrice())
            {
                if(value() < passedOrderPrice.value())
                    return -1;
                return value() <= passedOrderPrice.value() ? 0 : 1;
            }
            return !hasPrice() ? 1 : -1;
        } else
        {
            return orderTypeCompare;
        }
    }

    public int compareTo(Object x0)
    {
        return compareTo((OrderPrice)x0);
    }

    private double price;
    private boolean hasPrice;
    private double limit;
    private boolean hasLimit;
    private final OrderType orderType;
}
