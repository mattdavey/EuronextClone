package com.euronextclone;

public class OrderPrice implements Comparable {
    public OrderPrice(final OrderType orderType) {
        this.orderType = orderType;
    }

    public OrderPrice(final OrderType orderType, final double price) {
        this(orderType);
        update(price);
    }

    public OrderPrice(final OrderType orderType, final double price, final double limit) {
        this(orderType, price);
        this.limit = limit;
    }

    public void convertToLimit(final double value) {
        this.orderType = OrderType.Limit;
        this.price = value;
    }

    public void updateToLimitOrder(final double value) {
        price = value;
        orderType = OrderType.Limit;
    }

    public void update(final double value) {
        price = value;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public double getLimit() {
        return limit;
    }

    public boolean hasPrice() {
        return price != Double.MAX_VALUE;
    }

    public boolean hasLimit() {
        return limit != Double.MAX_VALUE;
    }

    public double value() {
        return price;
    }

    public String toString() {
        return orderType.format(price, limit);
    }

    public boolean updateBestLimit(final OrderPrice bestLimit) {
        return orderType.markToBestLimit(this, bestLimit);
    }

    public int compareTo(final OrderPrice other) {
        int orderTypeCompare = compareOrderTypes(orderType, other.getOrderType());
        if (orderTypeCompare == 0) {
            if (this.hasPrice() && other.hasPrice()) {
                if (this.value() < other.value())
                    return -1;
                return this.value() <= other.value() ? 0 : 1;
            }

            if (!this.hasPrice() && !other.hasPrice()) {
                return orderTypeCompare;
            }
            return !this.hasPrice() ? 1 : -1;
        } else {
            return orderTypeCompare;
        }
    }

    public int compareTo(Object x0) {
        return compareTo((OrderPrice) x0);
    }

    private static int compareOrderTypes(OrderType orderType, OrderType orderTypePassed) {
        int BEFORE = -1;
        int EQUAL = 0;
        int AFTER = 1;
        if (orderType == orderTypePassed)
            return 0;
        if (orderType.canBeTopOfBook() && !orderTypePassed.canBeTopOfBook())
            return 1;
        return !orderTypePassed.canBeTopOfBook() || orderType.canBeTopOfBook() ? 0 : -1;
    }

    private double price = Double.MAX_VALUE;
    private double limit = Double.MAX_VALUE;
    private OrderType orderType;
}
