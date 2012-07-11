package com.euronextclone;

import com.euronextclone.ordertypes.Limit;

public class IndicativeMatchPrice {
    private OrderPrice price;
    private int quantity;

    public IndicativeMatchPrice(final Order.OrderSide side) {
        price = new OrderPrice(new Limit(), side != Order.OrderSide.Buy ? Double.MAX_VALUE: Double.MIN_VALUE);
    }

    public void resetQuantity() {
        quantity=0;
    }

    public void addQuantity(final int quantity) {
        this.quantity += quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public OrderPrice getOrderPrice() {
        return price;
    }
}
