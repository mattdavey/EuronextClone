package com.euronextclone;

public class MatchingUnit
{
    public MatchingUnit()
    {
        buyOrderBook = new OrderBook(Order.OrderSide.Buy);
        sellOrderBook = new OrderBook(Order.OrderSide.Sell);
    }

    public void newOrder(Order.OrderSide side, String broker, int quantity, OrderPrice price)
    {
        matchOrder(side, broker, quantity, price);
    }

    private void matchOrder(Order.OrderSide side, String broker, int quantity, OrderPrice price)
    {
        final OrderBook matchOrderBook = side != Order.OrderSide.Buy ? buyOrderBook : sellOrderBook;
        final Order order = new Order(broker, quantity, price, side);
        if(matchOrderBook.match(order))
        {
            final OrderBook orderBook = side != Order.OrderSide.Buy ? sellOrderBook : buyOrderBook;
            orderBook.addOrder(order);
        }
    }

    public int orderBookDepth(Order.OrderSide side)
    {
        final OrderBook orders = side != Order.OrderSide.Buy ? sellOrderBook : buyOrderBook;
        return orders.orderBookDepth();
    }

    public String getBestLimit(Order.OrderSide side)
    {
        return side != Order.OrderSide.Buy ? sellOrderBook.getBestLimit().toString() : buyOrderBook.getBestLimit().toString();
    }

    public void dump()
    {
        System.out.println();
        System.out.println("Buy Book:");
        buyOrderBook.dump();
        System.out.println("Sell Book:");
        sellOrderBook.dump();
        System.out.println();
    }

    private final OrderBook buyOrderBook;
    private final OrderBook sellOrderBook;
}
