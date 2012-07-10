package com.euronextclone;

import com.euronextclone.ordertypes.Limit;
import com.euronextclone.ordertypes.PegWithLimit;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

public class PegOrderLimitFillTradeTest
{
    private void buyOrders(MatchingUnit matchingUnit)
    {
        matchingUnit.newOrder(Order.OrderSide.Buy, "A", 200, new OrderPrice(new Limit(), 11.5D));
        matchingUnit.newOrder(Order.OrderSide.Buy, "B", 150, new OrderPrice(new PegWithLimit(), 11.5D, 11.6D));
    }

    @Test
    public void newPEGLimitOrderTest()
    {
        MatchingUnit matchingUnit = new MatchingUnit();
        buyOrders(matchingUnit);

        matchingUnit.newOrder(Order.OrderSide.Sell, "C", 200, new OrderPrice(new Limit(), 11.5D));

        MatcherAssert.assertThat("Buy Order Depth", Integer.valueOf(matchingUnit.orderBookDepth(Order.OrderSide.Buy)), Matchers.is(Integer.valueOf(0)));
        MatcherAssert.assertThat("Sell Order Depth", Integer.valueOf(matchingUnit.orderBookDepth(Order.OrderSide.Sell)), Matchers.is(Integer.valueOf(0)));
    }
}
