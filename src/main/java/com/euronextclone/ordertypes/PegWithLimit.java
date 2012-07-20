package com.euronextclone.ordertypes;

import com.euronextclone.OrderType;
import com.euronextclone.OrderTypeLimit;

/**
 * Created with IntelliJ IDEA.
 * User: eprystupa
 * Date: 7/20/12
 * Time: 2:36 PM
 */
public class PegWithLimit extends OrderTypeLimit {

    public PegWithLimit(final double limit) {
        super(OrderType.Peg, limit);
    }
}
