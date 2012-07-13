package com.euronextclone;

import hu.akarnokd.reactive4java.base.Action1;
import hu.akarnokd.reactive4java.reactive.Reactive;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.io.Closeable;
import java.io.IOException;

import static org.hamcrest.core.Is.is;

public class PureMarketOrderTest extends BaseReactiveTest
{
    @Test
    public void tradingSessionExample1() throws IOException {
        final MatchingUnit matchingUnit = new MatchingUnit();
        matchingUnit.newOrder(Order.OrderSide.Sell, "A", 100, new OrderPrice(OrderType.Limit, 10.2));
        matchingUnit.newOrder(Order.OrderSide.Sell, "B", 60, new OrderPrice(OrderType.Limit, 10.3));

        final Closeable close = matchingUnit.register(Reactive.toObserver(new Action1<Trade>() {
            @Override
            public void invoke(Trade value) {
                final int count = incTradeCount();

                switch (count){
                    case 0:
                        assert value.getBuyBroker() == "C";
                        assert value.getSellBroker() == "A";
                        assert value.getQuantity() == 100;
                        assert value.getPrice() == 10.2;
                        break;
                    case 1:
                        assert value.getBuyBroker() == "C";
                        assert value.getSellBroker() == "B";
                        assert value.getQuantity() == 10;
                        assert value.getPrice() == 10.3;
                        break;
                }
            }
        }));

        matchingUnit.newOrder(Order.OrderSide.Buy, "C", 110, new OrderPrice(OrderType.MarketOrder));
        close.close();

        MatcherAssert.assertThat("Received Trade", getReceivedTradeCount(), is(2));
        MatcherAssert.assertThat("Buy Order Depth", matchingUnit.orderBookDepth(Order.OrderSide.Buy), Matchers.is(0));
        MatcherAssert.assertThat("Sell Order Depth", matchingUnit.orderBookDepth(Order.OrderSide.Sell), Matchers.is(1));
    }

    @Test
    public void tradingSessionExample2() throws IOException {
        final MatchingUnit matchingUnit = new MatchingUnit();
        matchingUnit.newOrder(Order.OrderSide.Sell, "A", 100, new OrderPrice(OrderType.Limit, 10.2));
        matchingUnit.newOrder(Order.OrderSide.Sell, "B", 60, new OrderPrice(OrderType.Limit, 10.3));

        final Closeable close = matchingUnit.register(Reactive.toObserver(new Action1<Trade>() {
            @Override
            public void invoke(Trade value) {
                final int count = incTradeCount();

                switch (count){
                    case 0:
                        assert value.getBuyBroker() == "C";
                        assert value.getSellBroker() == "A";
                        assert value.getQuantity() == 100;
                        assert value.getPrice() == 10.2;
                        break;
                    case 1:
                        assert value.getBuyBroker() == "C";
                        assert value.getSellBroker() == "B";
                        assert value.getQuantity() == 60;
                        assert value.getPrice() == 10.3;
                        break;
                }
            }
        }));

        matchingUnit.newOrder(Order.OrderSide.Buy, "C", 200, new OrderPrice(OrderType.MarketOrder));
        close.close();

        MatcherAssert.assertThat("Received Trade", getReceivedTradeCount(), is(2));
        MatcherAssert.assertThat("Buy Order Depth", matchingUnit.orderBookDepth(Order.OrderSide.Buy), Matchers.is(1));
        MatcherAssert.assertThat("Sell Order Depth", matchingUnit.orderBookDepth(Order.OrderSide.Sell), Matchers.is(0));
    }
}
