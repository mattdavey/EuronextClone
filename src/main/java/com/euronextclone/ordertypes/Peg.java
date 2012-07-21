package com.euronextclone.ordertypes;

import com.euronextclone.Order;
import com.euronextclone.OrderType;
import com.euronextclone.OrderTypeLimit;

/**
 * Created with IntelliJ IDEA.
 * User: eprystupa
 * Date: 7/20/12
 * Time: 2:35 PM
 */
public class Peg extends OrderTypeLimit {

    public Peg() {
        super(OrderType.Peg);
    }

    @Override
    public boolean providesLimit() {
        return false;
    }

    @Override
    public Double price(Order.OrderSide side, double bestLimit) {
        return bestLimit;
    }

    @Override
    public double value(double bestLimit) {
        return bestLimit;
    }
}
