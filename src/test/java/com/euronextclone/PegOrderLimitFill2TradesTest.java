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

public class PegOrderLimitFill2TradesTest extends BaseReactiveTest {
    private void buyOrders(MatchingUnit matchingUnit) {
        matchingUnit.newOrder(Order.OrderSide.Buy, "A", 200, new OrderPrice(OrderType.Limit, 11.5D));
        matchingUnit.newOrder(Order.OrderSide.Buy, "B", 150, new OrderPrice(OrderType.PegWithLimit, 11.5D, 11.6D));
        matchingUnit.newOrder(Order.OrderSide.Buy, "B", 70, new OrderPrice(OrderType.Peg, 11.5D));
        matchingUnit.newOrder(Order.OrderSide.Buy, "B", 125, new OrderPrice(OrderType.Limit, 10.5D));
    }

    private void sellOrders(MatchingUnit matchingUnit) {
        matchingUnit.newOrder(Order.OrderSide.Sell, "C", 130, new OrderPrice(OrderType.Limit, 11.800000000000001D));
        matchingUnit.newOrder(Order.OrderSide.Sell, "C", 350, new OrderPrice(OrderType.Limit, 11.9D));
        matchingUnit.newOrder(Order.OrderSide.Sell, "D", 275, new OrderPrice(OrderType.Limit, 12D));
    }

    @Test
    public void newBuyAndSellOrderTest() {
        final MatchingUnit matchingUnit = new MatchingUnit();
        buyOrders(matchingUnit);
        sellOrders(matchingUnit);

        matchingUnit.newOrder(Order.OrderSide.Buy, "E", 200, new OrderPrice(OrderType.Limit, 11.699999999999999D));

        MatcherAssert.assertThat("Buy Order Depth", matchingUnit.orderBookDepth(Order.OrderSide.Buy), Matchers.is(5));
        MatcherAssert.assertThat("Sell Order Depth", matchingUnit.orderBookDepth(Order.OrderSide.Sell), Matchers.is(3));
    }

    @Test
    public void newSellLimitOrderTest() throws IOException {
        final MatchingUnit matchingUnit = new MatchingUnit();
        buyOrders(matchingUnit);
        sellOrders(matchingUnit);

        matchingUnit.newOrder(Order.OrderSide.Buy, "E", 200, new OrderPrice(OrderType.Limit, 11.699999999999999D));

        final Closeable close = matchingUnit.register(Reactive.toObserver(new Action1<Trade>() {
            @Override
            public void invoke(Trade value) {
                int count = incTradeCount();

                switch (count) {
                    case 0:
                        assert value.getPrice() == 11.7;
                        assert value.getSellBroker() == "A";
                        assert value.getBuyBroker() == "E";
                        assert value.getQuantity() == 200;
                        break;
                    case 1:
                        assert value.getPrice() == 11.7;
                        assert value.getSellBroker() == "A";
                        assert value.getBuyBroker() == "B";
                        assert value.getQuantity() == 70;
                        break;
                }
            }
        }));
        matchingUnit.newOrder(Order.OrderSide.Sell, "A", 270, new OrderPrice(OrderType.Limit, 11.699999999999999D));
        close.close();

        MatcherAssert.assertThat("Received Trade", getReceivedTradeCount(), is(2));
        MatcherAssert.assertThat("Buy Order Depth", matchingUnit.orderBookDepth(Order.OrderSide.Buy), Matchers.is(3));
        MatcherAssert.assertThat("Sell Order Depth", matchingUnit.orderBookDepth(Order.OrderSide.Sell), Matchers.is(3));
    }
}
