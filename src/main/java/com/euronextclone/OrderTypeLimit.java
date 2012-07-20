package com.euronextclone;

public abstract class OrderTypeLimit {
    public OrderTypeLimit(final OrderType orderType) {
        this.orderType = orderType;
    }

    public OrderTypeLimit(final OrderType orderType, final double limit) {
        this(orderType);
        this.limit = limit;
    }

    public void convertToLimit(final double limit) {
        this.orderType = OrderType.Limit;
        this.limit = limit;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void setLimit(final double limit) {
        this.limit = limit;
    }

    public double getLimit() {
        return limit;
    }

    public boolean hasLimit() {
        return limit != Double.MAX_VALUE;
    }

    public String toString() {
        return orderType.format(limit);
    }

    private double limit = Double.MAX_VALUE;
    private OrderType orderType;

    public abstract double value(final double bestLimit);

    public boolean canTrade(final Double price, final Order.OrderSide side) {

        if (!hasLimit()) {
            return true;
        }

        if (side == Order.OrderSide.Buy) {
            return price <= getLimit();
        }

        return price >= getLimit();
    }
}
