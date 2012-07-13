package com.euronextclone.ordertypes;

import com.euronextclone.OrderPrice;

public interface OrderType
    extends Comparable
{

    public abstract String format(double d, double d1);

    public abstract boolean markToBestLimit(OrderPrice orderPrice, OrderPrice bestLimit);

    public abstract boolean canBeTopOfBook();
}
