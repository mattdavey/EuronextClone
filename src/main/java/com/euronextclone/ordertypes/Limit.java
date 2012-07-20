package com.euronextclone.ordertypes;

import com.euronextclone.OrderType;
import com.euronextclone.OrderTypeLimit;

/**
 * Created with IntelliJ IDEA.
 * User: eprystupa
 * Date: 7/20/12
 * Time: 2:32 PM
 */
public class Limit extends OrderTypeLimit {

    private final double limit;

    public Limit(final double limit) {
        super(OrderType.Limit, limit);
        this.limit = limit;
    }

    @Override
    public double value(double bestLimit) {
        return limit;
    }
}
