package com.euronextclone.ordertypes;

import com.euronextclone.OrderSide;
import com.euronextclone.OrderType;

/**
 * Created with IntelliJ IDEA.
 * User: eprystupa
 * Date: 7/20/12
 * Time: 2:08 PM
 */
public class Market implements OrderType {

    @Override
    public boolean acceptsMarketPrice() {
        return true;
    }

    @Override
    public boolean providesLimit() {
        return false;
    }

    @Override
    public double getLimit() {
        throw new RuntimeException("Market orders do not provide limit");
    }

    @Override
    public boolean canPegLimit() {
        return false;
    }

    @Override
    public boolean convertsToLimit() {
        return false;
    }

    @Override
    public Double price(OrderSide side, Double bestLimit) {
        return null;
    }

    @Override
    public String displayPrice(Double price) {
        return "MO";
    }
}
