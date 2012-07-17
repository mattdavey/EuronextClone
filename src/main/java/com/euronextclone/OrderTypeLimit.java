package com.euronextclone;

public class OrderTypeLimit {
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

    public void updateToLimitOrder(final double limit) {
        this.limit = limit;
        this.orderType = OrderType.Limit;
    }

    public OrderType getOrderType() {
        return orderType;
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

    @Override
    public boolean equals(Object aThat) {
        //check for self-comparison
        if ( this == aThat ) return true;

        //use instanceof instead of getClass here for two reasons
        //1. if need be, it can match any supertype, and not just one class;
        //2. it renders an explict check for "that == null" redundant, since
        //it does the check for null already - "null instanceof [type]" always
        //returns false. (See Effective Java by Joshua Bloch.)
        if ( !(aThat instanceof OrderTypeLimit) ) return false;

        return orderType == ((OrderTypeLimit) aThat).getOrderType() &&
            Double.doubleToLongBits(limit) == Double.doubleToLongBits(((OrderTypeLimit) aThat).getLimit());
    }

    private double limit = Double.MAX_VALUE;
    private OrderType orderType;

    public double value(final IndicativeMatchPrice bestLimit) {
        if (getOrderType() == OrderType.Peg) {
            if (hasLimit()) {
                if (limit < bestLimit.getOrderPrice().limit)
                    return limit;
            }
        }

        if (getOrderType() == OrderType.Limit) {
            return limit;
        }

        return bestLimit.getOrderPrice().limit;
    }

    public boolean canTrade(Double price, Order.OrderSide side) {
        return true;
    }
}
