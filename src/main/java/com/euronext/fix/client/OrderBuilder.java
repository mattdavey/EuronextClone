package com.euronext.fix.client;

import quickfix.field.*;
import quickfix.fix42.NewOrderSingle;

import java.util.Date;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: eprystupa
 * Date: 7/25/12
 * Time: 10:02 PM
 */
public class OrderBuilder {
    private String symbol;
    private char orderType;
    private double quantity;
    private char side;
    private double price;

    public OrderBuilder withSymbol(String symbol) {
        this.symbol = symbol;
        return this;
    }

    public OrderBuilder withOrderType(char orderType) {
        this.orderType = orderType;
        return this;
    }

    public OrderBuilder withQuantity(double quantity) {
        this.quantity = quantity;
        return this;
    }

    public OrderBuilder at(double price) {
        this.price = price;
        return this;
    }

    public NewOrderSingle buy() {
        this.side = Side.BUY;
        return build();
    }

    public NewOrderSingle sell() {
        this.side = Side.SELL;
        return build();
    }

    private NewOrderSingle build() {
        final NewOrderSingle order = new NewOrderSingle(
                new ClOrdID(UUID.randomUUID().toString()),
                new HandlInst(HandlInst.AUTOMATED_EXECUTION_ORDER_PUBLIC),
                new Symbol(symbol),
                new Side(side),
                new TransactTime(new Date()),
                new OrdType(orderType));

        order.set(new OrderQty(quantity));
        order.set(new Price(price));
        return order;
    }
}
