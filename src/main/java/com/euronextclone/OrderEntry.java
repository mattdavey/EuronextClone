package com.euronextclone;

import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: eprystupa
 * Date: 7/26/12
 * Time: 5:19 PM
 */
public class OrderEntry {
    private final String orderId;
    private final OrderSide side;
    private final String broker;
    private final int quantity;
    private final OrderType orderType;


    public OrderEntry(OrderSide side, String broker, int quantity, OrderType orderType) {
        this.orderId = UUID.randomUUID().toString();
        this.side = side;
        this.broker = broker;
        this.quantity = quantity;
        this.orderType = orderType;
    }

    public String getOrderId() {
        return orderId;
    }

    public OrderSide getSide() {
        return side;
    }

    public String getBroker() {
        return broker;
    }

    public int getQuantity() {
        return quantity;
    }

    public OrderType getOrderType() {
        return orderType;
    }
}
