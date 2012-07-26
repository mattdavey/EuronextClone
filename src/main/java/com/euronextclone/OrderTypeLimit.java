package com.euronextclone;

public abstract class OrderTypeLimit {

    public abstract boolean acceptsMarketPrice();

    public abstract boolean providesLimit();

    public abstract double getLimit();

    public abstract boolean canPegLimit();

    public abstract boolean convertsToLimit();

    public abstract Double price(OrderSide side, final Double bestLimit);

    public abstract String displayPrice(Double price);
}
