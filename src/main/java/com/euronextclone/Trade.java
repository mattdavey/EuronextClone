package com.euronextclone;

public class Trade {
    private final String buyOrderId;
    private final String buyBroker;
    private final String sellOrderId;
    private final String sellBroker;
    private final int quantity;
    private final double price;



    public Trade(String buyOrderId, final String buyBroker, String sellOrderId, final String sellBroker, final int quantity, final double price) {
        this.buyOrderId = buyOrderId;
        this.buyBroker = buyBroker;
        this.sellOrderId = sellOrderId;
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

    public String getBuyOrderId() {
        return buyOrderId;
    }

    public String getSellOrderId() {
        return sellOrderId;
    }
}
