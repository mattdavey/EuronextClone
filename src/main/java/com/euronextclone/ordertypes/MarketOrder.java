package com.euronextclone.ordertypes;


import com.euronextclone.OrderPrice;

public class MarketOrder extends BaseOrderType
{
    public MarketOrder()
    {
    }

    public String format(double price, double limit)
    {
        return "MO";
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
