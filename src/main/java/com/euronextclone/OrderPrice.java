package com.euronextclone;

import com.euronextclone.ordertypes.Limit;
import com.euronextclone.ordertypes.OrderType;

public class OrderPrice implements Comparable
{
    public OrderPrice(final OrderType orderType)
    {
        this.orderType = orderType;
    }

    public OrderPrice(final OrderType orderType, final double price)
    {
        this(orderType);
        update(price);
    }

    public OrderPrice(final OrderType orderType, final double price, final double limit)
    {
        this(orderType, price);
        this.limit = limit;
    }

    public void convertToLimit(final double value) {
        this.orderType = Limit.INSTANCE;
        this.price = value;
    }

    public void updateToLimitOrder(final double value)
    {
        price = value;
        orderType = Limit.INSTANCE;
    }

    public void update(final double value)
    {
        price = value;
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
        return price != Double.MAX_VALUE;
    }

    public boolean hasLimit()
    {
        return limit != Double.MAX_VALUE;
    }

    public double value()
    {
        return price;
    }

    public String toString()
    {
        return orderType.format(price, limit);
    }

    public boolean updateBestLimit(final OrderPrice bestLimit)
    {
        return orderType.markToBestLimit(this, bestLimit);
    }

    public int compareTo(final OrderPrice passedOrderPrice)
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

    private double price = Double.MAX_VALUE;
    private double limit = Double.MAX_VALUE;
    private OrderType orderType;
}
