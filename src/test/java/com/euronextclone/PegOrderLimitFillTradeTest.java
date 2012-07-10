package com.euronextclone;

import com.euronextclone.ordertypes.Limit;
import com.euronextclone.ordertypes.PegWithLimit;
import hu.akarnokd.reactive4java.base.Action1;
import hu.akarnokd.reactive4java.reactive.Reactive;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.io.Closeable;
import java.io.IOException;

import static org.hamcrest.core.Is.is;

public class PegOrderLimitFillTradeTest
{
    private void buyOrders(MatchingUnit matchingUnit)
    {
        matchingUnit.newOrder(Order.OrderSide.Buy, "A", 200, new OrderPrice(new Limit(), 11.5D));
        matchingUnit.newOrder(Order.OrderSide.Buy, "B", 150, new OrderPrice(new PegWithLimit(), 11.5D, 11.6D));
    }

    @Test
    public void newPEGLimitOrderTest() throws IOException {
        final MatchingUnit matchingUnit = new MatchingUnit();
        buyOrders(matchingUnit);

        Closeable close = matchingUnit.register(Reactive.toObserver(new Action1<Trade>() {
            @Override
            public void invoke(Trade value) {
                assert value.getPrice() == 11.5;
                assert value.getSellBroker() == "C";
                assert value.getBuyBroker() == "A";
                assert value.getQuantity() == 200;
                setReceivedTrade();
            }
        }));

        matchingUnit.newOrder(Order.OrderSide.Sell, "C", 200, new OrderPrice(new Limit(), 11.5D));
        close.close();

        MatcherAssert.assertThat("Received Trade", receivedTrade, is(true));
        MatcherAssert.assertThat("Buy Order Depth", Integer.valueOf(matchingUnit.orderBookDepth(Order.OrderSide.Buy)), Matchers.is(Integer.valueOf(0)));
        MatcherAssert.assertThat("Sell Order Depth", Integer.valueOf(matchingUnit.orderBookDepth(Order.OrderSide.Sell)), Matchers.is(Integer.valueOf(0)));
    }

    private boolean receivedTrade = false;
    public void setReceivedTrade() {
        this.receivedTrade = true;
    }
}
