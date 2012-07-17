package com.euronextclone;

public class IndicativeMatchPrice
{
    private OrderTypeLimit orderTypeLimit;
    private int quantity;

    public IndicativeMatchPrice(final Order.OrderSide side)
    {
        if (side == Order.OrderSide.Buy)
        {
            orderTypeLimit = new OrderTypeLimit(OrderType.Limit);
        }
        else
        {
            orderTypeLimit = new OrderTypeLimit(OrderType.Limit, 0);
        }
    }

    public void reset()
    {
        quantity = 0;
//        orderTypeLimit.update(0);
    }

    public void addQuantity(final int quantity)
    {
        this.quantity += quantity;
    }

    public int getQuantity()
    {
        return quantity;
    }

    public OrderTypeLimit getOrderPrice()
    {
        return orderTypeLimit;
    }
}
