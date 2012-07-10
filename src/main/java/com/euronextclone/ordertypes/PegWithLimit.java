package com.euronextclone.ordertypes;

import com.euronextclone.OrderPrice;

public class PegWithLimit extends BaseOrderType
{

    public PegWithLimit()
    {
    }

    public String format(double price, double limit)
    {
        return String.format("PEG (%s)[%s]", new Object[] {
            Double.toString(price).replace('.', ','), Double.toString(limit).replace('.', ',')
        });
    }

    public boolean markToBestLimit(OrderPrice orderPrice, OrderPrice bestLimit)
    {
        double newVal = bestLimit.value() <= orderPrice.getLimit() ? bestLimit.value() : orderPrice.getLimit();
        orderPrice.update(newVal);
        return true;
    }

    public boolean canBeTopOfBook()
    {
        return false;
    }
}
