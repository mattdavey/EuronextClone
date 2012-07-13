package com.euronextclone;

import hu.akarnokd.reactive4java.base.Action1;
import hu.akarnokd.reactive4java.reactive.Reactive;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.io.Closeable;
import java.io.IOException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class PegOrderTest extends BaseReactiveTest {
    private void buyOrders(MatchingUnit matchingUnit) {
        matchingUnit.newOrder(Order.OrderSide.Buy, "A", 200, new OrderPrice(OrderType.Limit, 10.5D));
        matchingUnit.newOrder(Order.OrderSide.Buy, "B", 150, new OrderPrice(OrderType.Peg, 10.5D));
        matchingUnit.newOrder(Order.OrderSide.Buy, "B", 70, new OrderPrice(OrderType.Peg, 10.5D));
        matchingUnit.newOrder(Order.OrderSide.Buy, "B", 125, new OrderPrice(OrderType.Limit, 10.5D));
    }

    private void sellOrders(MatchingUnit matchingUnit) {
        matchingUnit.newOrder(Order.OrderSide.Sell, "C", 130, new OrderPrice(OrderType.Limit, 10.9D));
        matchingUnit.newOrder(Order.OrderSide.Sell, "C", 350, new OrderPrice(OrderType.Limit, 10.9D));
        matchingUnit.newOrder(Order.OrderSide.Sell, "D", 275, new OrderPrice(OrderType.Limit, 11D));
    }

    @Test
    public void example1Step1Test() {
        final MatchingUnit matchingUnit = new MatchingUnit();
        buyOrders(matchingUnit);
        sellOrders(matchingUnit);
        matchingUnit.dump();

        MatcherAssert.assertThat("Buy Order Depth", matchingUnit.orderBookDepth(Order.OrderSide.Buy), Matchers.is(4));
        MatcherAssert.assertThat("Sell Order Depth", matchingUnit.orderBookDepth(Order.OrderSide.Sell), Matchers.is(3));
    }

    @Test
    public void example1Step2Test() {
        final MatchingUnit matchingUnit = new MatchingUnit();
        buyOrders(matchingUnit);
        sellOrders(matchingUnit);
        matchingUnit.dump();
        matchingUnit.newOrder(Order.OrderSide.Buy, "E", 200, new OrderPrice(OrderType.Limit, 10.800000000000001D));
        matchingUnit.dump();

        MatcherAssert.assertThat("Buy Order Depth", matchingUnit.orderBookDepth(Order.OrderSide.Buy), Matchers.is(5));
        MatcherAssert.assertThat("Best Buy Limit", matchingUnit.getBestLimit(Order.OrderSide.Buy), Matchers.is("10,8"));
        MatcherAssert.assertThat("Best Sell Limit", matchingUnit.getBestLimit(Order.OrderSide.Sell), Matchers.is("10,9"));
    }

    @Test
    public void example1Step3Test() throws IOException {
        MatchingUnit matchingUnit = new MatchingUnit();
        buyOrders(matchingUnit);
        sellOrders(matchingUnit);

        matchingUnit.newOrder(Order.OrderSide.Buy, "E", 200, new OrderPrice(OrderType.Limit, 10.800000000000001D));

        final Closeable close = matchingUnit.register(Reactive.toObserver(new Action1<Trade>() {
            @Override
            public void invoke(Trade value) {
                assertThat(value.getPrice(), is(10.9));
                assertThat(value.getSellBroker(), is("C"));
                assertThat(value.getBuyBroker(), is("G"));
                assertThat(value.getQuantity(), is(100));
                incTradeCount();
            }
        }));

        matchingUnit.newOrder(Order.OrderSide.Buy, "G", 100, new OrderPrice(OrderType.Limit, 10.9D));

        close.close();

        MatcherAssert.assertThat("Received Trade", getReceivedTradeCount(), is(1));
        MatcherAssert.assertThat("Buy Order Depth", matchingUnit.orderBookDepth(Order.OrderSide.Buy), Matchers.is(5));
        MatcherAssert.assertThat("Sell Order Depth", matchingUnit.orderBookDepth(Order.OrderSide.Sell), Matchers.is(3));
        MatcherAssert.assertThat("Best Limit", matchingUnit.getBestLimit(Order.OrderSide.Buy), Matchers.is("10,8"));
        MatcherAssert.assertThat("Best Sell Limit", matchingUnit.getBestLimit(Order.OrderSide.Sell), Matchers.is("10,9"));
    }

    @Test
    public void example1Step4Test() throws IOException {
        final MatchingUnit matchingUnit = new MatchingUnit();
        buyOrders(matchingUnit);
        sellOrders(matchingUnit);

        matchingUnit.newOrder(Order.OrderSide.Buy, "E", 200, new OrderPrice(OrderType.Limit, 10.800000000000001D));
        matchingUnit.newOrder(Order.OrderSide.Buy, "G", 100, new OrderPrice(OrderType.Limit, 10.9D));

        final Closeable close = matchingUnit.register(Reactive.toObserver(new Action1<Trade>() {
            @Override
            public void invoke(Trade value) {
                int count = incTradeCount();
                switch (count) {
                    case 0:
                        assert value.getSellBroker() == "E";
                        assert value.getBuyBroker() == "G";
                        assert value.getQuantity() == 200;
                        assert value.getPrice() == 10.8;
                        break;
                    case 1:
                        assert value.getSellBroker() == "B";
                        assert value.getBuyBroker() == "G";
                        assert value.getQuantity() == 50;
                        assert value.getPrice() == 10.8;
                        break;
                }
            }
        }));

        matchingUnit.dump();
        matchingUnit.newOrder(Order.OrderSide.Sell, "G", 250, new OrderPrice(OrderType.Limit, 10.800000000000001D));
        close.close();

        MatcherAssert.assertThat("Received Trade", getReceivedTradeCount(), is(2));
        MatcherAssert.assertThat("Buy Order Depth", matchingUnit.orderBookDepth(Order.OrderSide.Buy), Matchers.is(4));
        MatcherAssert.assertThat("Sell Order Depth", matchingUnit.orderBookDepth(Order.OrderSide.Sell), Matchers.is(3));
        MatcherAssert.assertThat("Best Limit", matchingUnit.getBestLimit(Order.OrderSide.Buy), Matchers.is("10,5"));
        MatcherAssert.assertThat("Best Sell Limit", matchingUnit.getBestLimit(Order.OrderSide.Sell), Matchers.is("10,9"));
    }
}
