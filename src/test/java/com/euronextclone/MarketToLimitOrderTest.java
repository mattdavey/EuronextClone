package com.euronextclone;

import com.euronextclone.ordertypes.Limit;
import com.euronextclone.ordertypes.Market;
import com.euronextclone.ordertypes.MarketToLimit;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

public class MarketToLimitOrderTest {

    @Test
    public void continuousPhaseLimitOrdersTest() {
        final MatchingUnit matchingUnit = new MatchingUnit();
        matchingUnit.addOrder(Order.OrderSide.Buy, "A", 10, new Limit(15));
        matchingUnit.addOrder(Order.OrderSide.Buy, "B", 10, new Limit(12));
        matchingUnit.addOrder(Order.OrderSide.Buy, "C", 10, new Limit(10));
        matchingUnit.addOrder(Order.OrderSide.Sell, "D", 25, new MarketToLimit());

        MatcherAssert.assertThat("Buy Order Depth", matchingUnit.orderBookDepth(Order.OrderSide.Buy), Matchers.is(2));
        MatcherAssert.assertThat("Sell Order Depth", matchingUnit.orderBookDepth(Order.OrderSide.Sell), Matchers.is(1));
    }

    @Test
    public void continuousPhaseLimitOrdersAndPureMarketOrderTest() {
        final MatchingUnit matchingUnit = new MatchingUnit();
        matchingUnit.addOrder(Order.OrderSide.Buy, "A", 10, new Market());
        matchingUnit.addOrder(Order.OrderSide.Buy, "B", 10, new Limit(12));
        matchingUnit.addOrder(Order.OrderSide.Buy, "C", 10, new Limit(9));
        matchingUnit.addOrder(Order.OrderSide.Sell, "D", 25, new MarketToLimit());

        MatcherAssert.assertThat("Buy Order Depth", matchingUnit.orderBookDepth(Order.OrderSide.Buy), Matchers.is(1));
        MatcherAssert.assertThat("Sell Order Depth", matchingUnit.orderBookDepth(Order.OrderSide.Sell), Matchers.is(1));
    }
}
