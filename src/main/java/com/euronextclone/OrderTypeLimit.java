package com.euronextclone;

public abstract class OrderTypeLimit {

    private OrderType orderType;

    public OrderTypeLimit(final OrderType orderType) {
        this.orderType = orderType;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public abstract boolean providesLimit();

    public abstract double getLimit();

    public abstract boolean canPegLimit();

    public abstract Double price(Order.OrderSide side, final Double bestLimit);
}
