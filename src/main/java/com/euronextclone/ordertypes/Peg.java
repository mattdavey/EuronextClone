package com.euronextclone.ordertypes;

import com.euronextclone.Order;
import com.euronextclone.OrderType;
import com.euronextclone.OrderTypeLimit;

import java.text.DecimalFormat;

/**
 * Created with IntelliJ IDEA.
 * User: eprystupa
 * Date: 7/20/12
 * Time: 2:35 PM
 */
public class Peg extends OrderTypeLimit {

    private final static DecimalFormat priceFormat = new DecimalFormat("#.##");

    public Peg() {
        super(OrderType.Peg);
    }

    @Override
    public boolean providesLimit() {
        return false;
    }

    @Override
    public double getLimit() {
        throw new RuntimeException("Peg orders do not provide limit");
    }

    @Override
    public boolean canPegLimit() {
        return true;
    }

    @Override
    public boolean convertsToLimit() {
        return false;
    }

    @Override
    public Double price(Order.OrderSide side, Double bestLimit) {
        return bestLimit;
    }

    @Override
    public String displayPrice(Double price) {
        return String.format("Peg(%s)", priceFormat.format(price));
    }
}
