package com.euronextclone.ordertypes;

import com.euronextclone.OrderPrice;

public class Peg extends BaseOrderType
{
    public String format(double price, double limit)
    {
        return String.format("PEG (%s)", Double.toString(price).replace('.', ','));
    }

    public boolean markToBestLimit(OrderPrice orderPrice, OrderPrice bestLimit)
    {
        orderPrice.update(bestLimit.value());
        return true;
    }

    public boolean canBeTopOfBook()
    {
        return false;
    }
}
