package com.euronextclone;

public abstract class OrderTypeLimit {

    private double limit = Double.MAX_VALUE;
    private OrderType orderType;

    public OrderTypeLimit(final OrderType orderType) {
        this.orderType = orderType;
    }

    public OrderTypeLimit(final OrderType orderType, final double limit) {
        this(orderType);
        this.limit = limit;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public double getLimit() {
        return limit;
    }

    public String toString() {
        return orderType.format(limit);
    }

    public abstract boolean providesLimit();

    public abstract boolean canPegLimit();

    public abstract Double price(Order.OrderSide side, final Double bestLimit);
}
