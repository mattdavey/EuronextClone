package com.euronextclone.ordertypes;

import com.euronextclone.OrderPrice;

public class MarketToLimit extends BaseOrderType
{
    public static final MarketToLimit INSTANCE = new MarketToLimit();

    public String format(double price, double limit)
    {
        return "MTL";
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
