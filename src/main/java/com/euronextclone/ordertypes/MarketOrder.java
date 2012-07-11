package com.euronextclone.ordertypes;

import com.euronextclone.OrderPrice;

public class MarketOrder extends BaseOrderType
{
    public static final MarketOrder INSTANCE = new MarketOrder();

    public String format(double price, double limit)
    {
        return "MO";
    }

    public boolean markToBestLimit(final OrderPrice orderPrice, final OrderPrice bestLimit)
    {
        return false;
    }

    public boolean canBeTopOfBook()
    {
        return true;
    }
}
