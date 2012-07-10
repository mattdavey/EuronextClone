package com.euronextclone.ordertypes;

public abstract class BaseOrderType
    implements OrderType
{

    public BaseOrderType()
    {
    }

    public int compareTo(OrderType orderTypePassed)
    {
        int BEFORE = -1;
        int EQUAL = 0;
        int AFTER = 1;
        if(this == orderTypePassed)
            return 0;
        if(orderTypePassed.getClass() == getClass())
            return 0;
        if(canBeTopOfBook() && !orderTypePassed.canBeTopOfBook())
            return 1;
        return !orderTypePassed.canBeTopOfBook() || canBeTopOfBook() ? 0 : -1;
    }

    public int compareTo(Object x0)
    {
        return compareTo((OrderType)x0);
    }
}
