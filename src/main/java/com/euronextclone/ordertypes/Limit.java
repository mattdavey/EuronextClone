package com.euronextclone.ordertypes;

import com.euronextclone.OrderSide;
import com.euronextclone.OrderType;
import com.euronextclone.OrderTypeLimit;

import java.text.DecimalFormat;

/**
 * Created with IntelliJ IDEA.
 * User: eprystupa
 * Date: 7/20/12
 * Time: 2:32 PM
 */
public class Limit extends OrderTypeLimit {

    private final double limit;

    public Limit(final double limit) {
        super(OrderType.Limit);
        this.limit = limit;
    }

    @Override
    public boolean providesLimit() {
        return true;
    }

    @Override
    public double getLimit() {
        return limit;
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
        return limit;
    }

    @Override
    public String displayPrice(Double price) {
        return new DecimalFormat("#.##").format(price);
    }
}
