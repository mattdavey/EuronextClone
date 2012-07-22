package com.euronextclone.ordertypes;

import com.euronextclone.OrderSide;
import com.euronextclone.OrderType;
import com.euronextclone.OrderTypeLimit;

import java.text.DecimalFormat;

/**
 * Created with IntelliJ IDEA.
 * User: eprystupa
 * Date: 7/20/12
 * Time: 2:36 PM
 */
public class PegWithLimit extends OrderTypeLimit {

    private final static DecimalFormat priceFormat = new DecimalFormat("#.##");
    private final double limit;

    public PegWithLimit(final double limit) {
        super(OrderType.Peg);
        this.limit = limit;
    }

    @Override
    public boolean providesLimit() {
        return false;
    }

    @Override
    public double getLimit() {
        return limit;
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
    public Double price(OrderSide side, Double bestLimit) {
        if (side == OrderSide.Buy && bestLimit <= limit) {
            return bestLimit;
        } else if (side == OrderSide.Sell && bestLimit >= limit) {
            return bestLimit;
        } else {
            return limit;
        }
    }

    @Override
    public String displayPrice(Double price) {
        return String.format("Peg(%s)[%s]", priceFormat.format(price), priceFormat.format(limit));
    }
}
