package com.euronextclone;

import com.euronextclone.ordertypes.Limit;
import com.euronextclone.ordertypes.MarketOrder;
import com.euronextclone.ordertypes.MarketToLimit;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

public class PureMarketOrderTest
{
    private void buyOrders(MatchingUnit matchingUnit)
    {
        matchingUnit.newOrder(Order.OrderSide.Buy, "A", 50, new OrderPrice(new MarketToLimit()));
        matchingUnit.newOrder(Order.OrderSide.Buy, "B", 90, new OrderPrice(new Limit(), 10.1D));
        matchingUnit.newOrder(Order.OrderSide.Buy, "C", 10, new OrderPrice(new Limit(), 9.9000000000000004D));
    }

    private void sellOrders(MatchingUnit matchingUnit)
    {
        matchingUnit.newOrder(Order.OrderSide.Sell, "D", 40, new OrderPrice(new MarketToLimit()));
        matchingUnit.newOrder(Order.OrderSide.Sell, "E", 100, new OrderPrice(new Limit(), 10.08D));
        matchingUnit.newOrder(Order.OrderSide.Sell, "F", 60, new OrderPrice(new Limit(), 10.15D));
    }

    @Test
    public void marketOrderTotallyFilled()
    {
        MatchingUnit matchingUnit = new MatchingUnit();
        buyOrders(matchingUnit);
        sellOrders(matchingUnit);

        matchingUnit.newOrder(Order.OrderSide.Buy, "G", 20, new OrderPrice(new MarketOrder()));

        matchingUnit.dump();
        MatcherAssert.assertThat("Buy Order Depth", Integer.valueOf(matchingUnit.orderBookDepth(Order.OrderSide.Buy)), Matchers.is(Integer.valueOf(4)));
        MatcherAssert.assertThat("Sell Order Depth", Integer.valueOf(matchingUnit.orderBookDepth(Order.OrderSide.Sell)), Matchers.is(Integer.valueOf(3)));
    }
}
