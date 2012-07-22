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
            // Time after orderTypeLimit compare
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

        final OrderType thisOrderType = this.orderTypeLimit.getOrderType();
        final OrderType otherOrderType = other.orderTypeLimit.getOrderType();

        if (isMarket(thisOrderType) && !isMarket(otherOrderType)) {
            return AFTER;
        }
        if (!isMarket(thisOrderType) && isMarket(otherOrderType)) {
            return BEFORE;
        }

        return EQUAL;
    }

    private boolean isMarket(OrderType type) {
        return type == OrderType.MarketOrder || type == OrderType.MarketToLimit;
    }

    private int comparePrice(final Order anOrder, final Double bestLimit) {
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;

        final Double thisOrderLimit = getOrderTypeLimit().price(side, bestLimit);
        final Double anOrderLimit = anOrder.getOrderTypeLimit().price(side, bestLimit);

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

    public Order(final String broker, final int quantity, final OrderTypeLimit orderTypeLimit, final OrderSide side) {
        id = c.incrementAndGet();
        this.orderTypeLimit = orderTypeLimit;
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

    public OrderTypeLimit getOrderTypeLimit() {
        return orderTypeLimit;
    }

    public Order convertTo(OrderTypeLimit orderType) {
        return new Order(broker, quantity, orderType, side);
    }

    private static AtomicInteger c = new AtomicInteger(0);
    private final OrderTypeLimit orderTypeLimit;
    private final OrderSide side;
    private final String broker;
    private int quantity;
    private final int id;
}
