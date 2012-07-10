package com.euronextclone.ordertypes;

import com.euronextclone.OrderPrice;

public class Limit extends BaseOrderType
{
    public String format(double price, double limit)
    {
        return Double.toString(price).replace('.', ',');
    }

    public boolean markToBestLimit(OrderPrice orderPrice, OrderPrice bestLimit)
    {
        return false;
    }

    public boolean canBeTopOfBook()
    {
        return true;
    }
}
