package com.euronextclone;

import com.euronextclone.ordertypes.Limit;
import com.euronextclone.ordertypes.Peg;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

public class PegOrderTest
{
    private void buyOrders(MatchingUnit matchingUnit)
    {
        matchingUnit.newOrder(Order.OrderSide.Buy, "A", 200, new OrderPrice(new Limit(), 10.5D));
        matchingUnit.newOrder(Order.OrderSide.Buy, "B", 150, new OrderPrice(new Peg(), 10.5D));
        matchingUnit.newOrder(Order.OrderSide.Buy, "B", 70, new OrderPrice(new Peg(), 10.5D));
        matchingUnit.newOrder(Order.OrderSide.Buy, "B", 125, new OrderPrice(new Limit(), 10.5D));
    }

    private void sellOrders(MatchingUnit matchingUnit)
    {
        matchingUnit.newOrder(Order.OrderSide.Sell, "C", 130, new OrderPrice(new Limit(), 10.9D));
        matchingUnit.newOrder(Order.OrderSide.Sell, "C", 350, new OrderPrice(new Limit(), 10.9D));
        matchingUnit.newOrder(Order.OrderSide.Sell, "D", 275, new OrderPrice(new Limit(), 11D));
    }

    @Test
    public void newBuyAndSellOrderTest()
    {
        final MatchingUnit matchingUnit = new MatchingUnit();
        buyOrders(matchingUnit);
        sellOrders(matchingUnit);
        MatcherAssert.assertThat("Buy Order Depth", Integer.valueOf(matchingUnit.orderBookDepth(Order.OrderSide.Buy)), Matchers.is(Integer.valueOf(4)));
        MatcherAssert.assertThat("Sell Order Depth", Integer.valueOf(matchingUnit.orderBookDepth(Order.OrderSide.Sell)), Matchers.is(Integer.valueOf(3)));
    }

    @Test
    public void addLimitOrderWithoutMatchTest()
    {
        final MatchingUnit matchingUnit = new MatchingUnit();
        buyOrders(matchingUnit);
        sellOrders(matchingUnit);

        matchingUnit.newOrder(Order.OrderSide.Buy, "E", 200, new OrderPrice(new Limit(), 10.800000000000001D));

        MatcherAssert.assertThat("Buy Order Depth", Integer.valueOf(matchingUnit.orderBookDepth(Order.OrderSide.Buy)), Matchers.is(Integer.valueOf(5)));
        MatcherAssert.assertThat("Best Buy Limit", matchingUnit.getBestLimit(Order.OrderSide.Buy), Matchers.is("10,8"));
        MatcherAssert.assertThat("Best Sell Limit", matchingUnit.getBestLimit(Order.OrderSide.Sell), Matchers.is("10,9"));
    }

    @Test
    public void addBuyLimitOrderWithMatchTest()
    {
        MatchingUnit matchingUnit = new MatchingUnit();
        buyOrders(matchingUnit);
        sellOrders(matchingUnit);

        matchingUnit.newOrder(Order.OrderSide.Buy, "E", 200, new OrderPrice(new Limit(), 10.800000000000001D));
        matchingUnit.newOrder(Order.OrderSide.Buy, "G", 100, new OrderPrice(new Limit(), 10.9D));

        matchingUnit.dump();

        MatcherAssert.assertThat("Buy Order Depth", Integer.valueOf(matchingUnit.orderBookDepth(Order.OrderSide.Buy)), Matchers.is(Integer.valueOf(5)));
        MatcherAssert.assertThat("Sell Order Depth", Integer.valueOf(matchingUnit.orderBookDepth(Order.OrderSide.Sell)), Matchers.is(Integer.valueOf(3)));
        MatcherAssert.assertThat("Best Limit", matchingUnit.getBestLimit(Order.OrderSide.Buy), Matchers.is("10,8"));
        MatcherAssert.assertThat("Best Sell Limit", matchingUnit.getBestLimit(Order.OrderSide.Sell), Matchers.is("10,9"));
    }

    @Test
    public void addSellLimitOrderWithMatchTest()
    {
        final MatchingUnit matchingUnit = new MatchingUnit();
        buyOrders(matchingUnit);
        sellOrders(matchingUnit);

        matchingUnit.newOrder(Order.OrderSide.Buy, "E", 200, new OrderPrice(new Limit(), 10.800000000000001D));
        matchingUnit.newOrder(Order.OrderSide.Buy, "G", 100, new OrderPrice(new Limit(), 10.9D));
        matchingUnit.newOrder(Order.OrderSide.Sell, "G", 250, new OrderPrice(new Limit(), 10.800000000000001D));
        matchingUnit.dump();

        MatcherAssert.assertThat("Buy Order Depth", Integer.valueOf(matchingUnit.orderBookDepth(Order.OrderSide.Buy)), Matchers.is(Integer.valueOf(4)));
        MatcherAssert.assertThat("Sell Order Depth", Integer.valueOf(matchingUnit.orderBookDepth(Order.OrderSide.Sell)), Matchers.is(Integer.valueOf(3)));
        MatcherAssert.assertThat("Best Limit", matchingUnit.getBestLimit(Order.OrderSide.Buy), Matchers.is("10,5"));
        MatcherAssert.assertThat("Best Sell Limit", matchingUnit.getBestLimit(Order.OrderSide.Sell), Matchers.is("10,9"));
    }
}
