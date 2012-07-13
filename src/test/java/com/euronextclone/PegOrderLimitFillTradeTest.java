package com.euronextclone;

import com.euronextclone.ordertypes.OrderType;
import hu.akarnokd.reactive4java.base.Action1;
import hu.akarnokd.reactive4java.reactive.Reactive;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.io.Closeable;
import java.io.IOException;

import static org.hamcrest.core.Is.is;

public class PegOrderLimitFillTradeTest extends BaseReactiveTest
{
    private void buyOrders(MatchingUnit matchingUnit)
    {
        matchingUnit.newOrder(Order.OrderSide.Buy, "A", 200, new OrderPrice(OrderType.Limit, 11.5D));
        matchingUnit.newOrder(Order.OrderSide.Buy, "B", 150, new OrderPrice(OrderType.PegWithLimit, 11.5D, 11.6D));
    }

    @Test
    public void newPEGLimitOrderTest() throws IOException {
        final MatchingUnit matchingUnit = new MatchingUnit();
        buyOrders(matchingUnit);

        final Closeable close = matchingUnit.register(Reactive.toObserver(new Action1<Trade>() {
            @Override
            public void invoke(Trade value) {
                assert value.getPrice() == 11.5;
                assert value.getSellBroker() == "C";
                assert value.getBuyBroker() == "A";
                assert value.getQuantity() == 200;
                incTradeCount();
            }
        }));

        matchingUnit.newOrder(Order.OrderSide.Sell, "C", 200, new OrderPrice(OrderType.Limit, 11.5D));
        close.close();

        MatcherAssert.assertThat("Received Trade", getReceivedTradeCount(), is(1));
        MatcherAssert.assertThat("Buy Order Depth", matchingUnit.orderBookDepth(Order.OrderSide.Buy), Matchers.is(0));
        MatcherAssert.assertThat("Sell Order Depth", matchingUnit.orderBookDepth(Order.OrderSide.Sell), Matchers.is(0));
    }
}
