package com.euronextclone;

public class Trade {
    private final String sellBroker;
    private final String buyBroker;
    private final int quantity;
    private final double price;

    public Trade(final String sellBroker, final String buyBroker, final int quantity, final double price) {
        this.buyBroker = buyBroker;
        this.sellBroker = sellBroker;
        this.price = price;
        this.quantity = quantity;
    }

    public String getSellBroker() {
        return sellBroker;
    }

    public String getBuyBroker() {
        return buyBroker;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }
}
