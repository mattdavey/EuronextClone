package com.euronextclone.engine;

import com.euronextclone.OrderEntry;
import com.lmax.disruptor.EventFactory;

public final class ValueEvent
{
    private OrderEntry value;

    public OrderEntry getValue()
    {
        return value;
    }

    public void setValue(final OrderEntry value)
    {
        this.value = value;
    }

    public final static EventFactory<ValueEvent> EVENT_FACTORY = new EventFactory<ValueEvent>()
    {
        public ValueEvent newInstance()
        {
            return new ValueEvent();
        }
    };
}