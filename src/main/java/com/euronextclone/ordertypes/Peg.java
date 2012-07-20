package com.euronextclone.ordertypes;

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
}
