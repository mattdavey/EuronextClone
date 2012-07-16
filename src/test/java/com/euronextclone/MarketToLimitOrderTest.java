package com.euronextclone;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

public class MarketToLimitOrderTest {

    @Test
    public void continuousPhaseLimitOrdersTest()
    {
        final MatchingUnit matchingUnit = new MatchingUnit();
        matchingUnit.newOrder(Order.OrderSide.Buy, "A", 10, new OrderTypeLimit(OrderType.Limit, 15));
        matchingUnit.newOrder(Order.OrderSide.Buy, "B", 10, new OrderTypeLimit(OrderType.Limit, 12));
        matchingUnit.newOrder(Order.OrderSide.Buy, "C", 10, new OrderTypeLimit(OrderType.Limit, 10));
        matchingUnit.newOrder(Order.OrderSide.Sell, "D", 25, new OrderTypeLimit(OrderType.MarketToLimit));

        MatcherAssert.assertThat("Buy Order Depth", matchingUnit.orderBookDepth(Order.OrderSide.Buy), Matchers.is(2));
        MatcherAssert.assertThat("Sell Order Depth", matchingUnit.orderBookDepth(Order.OrderSide.Sell), Matchers.is(1));
    }

    @Test
    public void continuousPhaseLimitOrdersAndPureMarketOrderTest()
    {
        final MatchingUnit matchingUnit = new MatchingUnit();
        matchingUnit.newOrder(Order.OrderSide.Buy, "A", 10, new OrderTypeLimit(OrderType.MarketOrder));
        matchingUnit.newOrder(Order.OrderSide.Buy, "B", 10, new OrderTypeLimit(OrderType.Limit, 12));
        matchingUnit.newOrder(Order.OrderSide.Buy, "C", 10, new OrderTypeLimit(OrderType.Limit, 9));
        matchingUnit.newOrder(Order.OrderSide.Sell, "D", 25, new OrderTypeLimit(OrderType.MarketToLimit));

        matchingUnit.dump();

        MatcherAssert.assertThat("Buy Order Depth", matchingUnit.orderBookDepth(Order.OrderSide.Buy), Matchers.is(1));
        MatcherAssert.assertThat("Sell Order Depth", matchingUnit.orderBookDepth(Order.OrderSide.Sell), Matchers.is(1));
    }
}
