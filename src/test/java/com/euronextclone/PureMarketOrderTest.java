package com.euronextclone;

import com.euronextclone.ordertypes.Limit;
import com.euronextclone.ordertypes.MarketOrder;
import com.euronextclone.ordertypes.MarketToLimit;
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
    public void marketOrderTotallyFilled() throws IOException {
        final MatchingUnit matchingUnit = new MatchingUnit();
        buyOrders(matchingUnit);
        sellOrders(matchingUnit);

        final Closeable close = matchingUnit.register(Reactive.toObserver(new Action1<Trade>() {
            @Override
            public void invoke(Trade value) {
                final int count = incTradeCount();

                switch (count){
                    case 0:
                        assert value.getBuyBroker() == "A";
                        assert value.getSellBroker() == "D";
                        assert value.getQuantity() == 40;
                        assert value.getPrice() == 10.1;
                        break;
                    case 1:
                        assert value.getBuyBroker() == "A";
                        assert value.getSellBroker() == "E";
                        assert value.getQuantity() == 10;
                        assert value.getPrice() == 10.1;
                        break;
                    case 2:
                        assert value.getBuyBroker() == "G";
                        assert value.getSellBroker() == "E";
                        assert value.getQuantity() == 20;
                        assert value.getPrice() == 10.1;
                        break;
                    case 3:
                        assert value.getBuyBroker() == "B";
                        assert value.getSellBroker() == "E";
                        assert value.getQuantity() == 70;
                        assert value.getPrice() == 10.1;
                        break;
                }
            }
        }));

        matchingUnit.newOrder(Order.OrderSide.Buy, "G", 20, new OrderPrice(new MarketOrder()));

        matchingUnit.dump();
        close.close();

        MatcherAssert.assertThat("Received Trade", getReceivedTradeCount(), is(4));
        MatcherAssert.assertThat("Buy Order Depth", matchingUnit.orderBookDepth(Order.OrderSide.Buy), Matchers.is(4));
        MatcherAssert.assertThat("Sell Order Depth", matchingUnit.orderBookDepth(Order.OrderSide.Sell), Matchers.is(3));
    }
}
