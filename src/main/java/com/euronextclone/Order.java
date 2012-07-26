package com.euronextclone;

import java.util.concurrent.atomic.AtomicInteger;

public class Order {
    public int compareTo(final Order anOrder, final Double bestLimit) {
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;

        if (getId() == anOrder.getId())
            return EQUAL;

        final int compareOrderType = compareOrderType(anOrder);
        if (compareOrderType != EQUAL) {
            return compareOrderType;
        }

        final int comparePrice = comparePrice(anOrder, bestLimit);
        if (comparePrice == EQUAL) {
            // Time after orderType compare
            if (getId() > anOrder.getId()) {
                return BEFORE;
            } else if (getId() < anOrder.getId()) {
                return AFTER;
            }
        }

        return comparePrice;
    }

    private int compareOrderType(Order other) {
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;

        if (this.orderType.acceptsMarketPrice() && !other.orderType.acceptsMarketPrice()) {
            return AFTER;
        }
        if (!this.orderType.acceptsMarketPrice() && other.orderType.acceptsMarketPrice()) {
            return BEFORE;
        }

        return EQUAL;
    }

    private int comparePrice(final Order anOrder, final Double bestLimit) {
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;

        final Double thisOrderLimit = getOrderType().price(side, bestLimit);
        final Double anOrderLimit = anOrder.getOrderType().price(side, bestLimit);

        if (thisOrderLimit == null && anOrderLimit == null) {
            return EQUAL;
        }

        switch (side) {
            case Buy:
                if (thisOrderLimit == null || thisOrderLimit > anOrderLimit) {
                    return AFTER;
                } else if (thisOrderLimit < anOrderLimit) {
                    return BEFORE;
                }
                break;
            case Sell:
                if (thisOrderLimit == null || thisOrderLimit < anOrderLimit) {
                    return AFTER;
                } else if (thisOrderLimit > anOrderLimit) {
                    return BEFORE;
                }
                break;
        }

        return EQUAL;
    }

    public Order(final String broker, final int quantity, final OrderType orderType, final OrderSide side) {
        id = c.incrementAndGet();
        this.orderType = orderType;
        this.side = side;
        this.broker = broker;
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getBroker() {
        return broker;
    }

    public OrderSide getSide() {
        return side;
    }

    public int getId() {
        return id;
    }

    public void decrementQuantity(final int quantity) {
        this.quantity -= quantity;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public Order convertTo(OrderType orderType) {
        return new Order(broker, quantity, orderType, side);
    }

    private static AtomicInteger c = new AtomicInteger(0);
    private final OrderType orderType;
    private final OrderSide side;
    private final String broker;
    private int quantity;
    private final int id;
}
