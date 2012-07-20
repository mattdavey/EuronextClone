package com.euronextclone.ordertypes;

import com.euronextclone.OrderType;
import com.euronextclone.OrderTypeLimit;

/**
 * Created with IntelliJ IDEA.
 * User: eprystupa
 * Date: 7/20/12
 * Time: 2:08 PM
 */
public class Market extends OrderTypeLimit {

    public Market() {
        super(OrderType.MarketOrder);
    }
}
