package com.euronextclone;

public class BestLimit {
    private OrderTypeLimit orderTypeLimit;

    public BestLimit(final Order.OrderSide side) {
        if (side == Order.OrderSide.Buy) {
            orderTypeLimit = new OrderTypeLimit(OrderType.Limit);
        } else {
            orderTypeLimit = new OrderTypeLimit(OrderType.Limit, 0);
        }
    }

    public OrderTypeLimit getOrderPrice() {
        return orderTypeLimit;
    }
}
