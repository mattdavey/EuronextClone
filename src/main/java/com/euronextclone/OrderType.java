package com.euronextclone;

public interface OrderType {

    boolean acceptsMarketPrice();

    boolean providesLimit();

    double getLimit();

    boolean canPegLimit();

    boolean convertsToLimit();

    Double price(OrderSide side, final Double bestLimit);

    String displayPrice(Double price);
}
