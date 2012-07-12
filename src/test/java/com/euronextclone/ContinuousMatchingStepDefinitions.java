package com.euronextclone;

import com.euronextclone.ordertypes.Limit;
import com.euronextclone.ordertypes.MarketOrder;
import com.euronextclone.ordertypes.MarketToLimit;
import cucumber.annotation.en.Given;
import cucumber.annotation.en.Then;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ContinuousMatchingStepDefinitions {
    private final MatchingUnit matchingUnit = new MatchingUnit();

    @Given("^the MTL buy order from broker \"([^\"]*)\" for (\\d+) shares$")
    public void the_MTL_buy_order_from_broker_for_shares(String broker, int quantity) throws Throwable {

        matchingUnit.newOrder(Order.OrderSide.Buy, broker, quantity, new OrderPrice(MarketToLimit.INSTANCE));
    }

    @Given("^the MTL sell order from broker \"([^\"]*)\" for (\\d+) shares$")
    public void the_MTL_sell_order_from_broker_for_shares(String broker, int quantity) throws Throwable {

        matchingUnit.newOrder(Order.OrderSide.Sell, broker, quantity, new OrderPrice(MarketToLimit.INSTANCE));
    }

    @Given("^the MO buy order from broker \"([^\"]*)\" for (\\d+) shares$")
    public void the_MO_buy_order_from_broker_for_shares(String broker, int quantity) throws Throwable {
        matchingUnit.newOrder(Order.OrderSide.Buy, broker, quantity, new OrderPrice(MarketOrder.INSTANCE));
    }

    @Given("^the Limit sell order from broker \"([^\"]*)\" for (\\d+) shares at ([0-9]*\\.?[0-9]+) price$")
    public void the_Limit_sell_order_from_broker_for_shares(String broker, int quantity, double price) throws Throwable {
        matchingUnit.newOrder(Order.OrderSide.Sell, broker, quantity, new OrderPrice(Limit.INSTANCE, price));
    }

    @Given("^the Limit buy order from broker \"([^\"]*)\" for (\\d+) shares at ([0-9]*\\.?[0-9]+) price$")
    public void the_Limit_buy_order_from_broker_for_shares(String broker, int quantity, double price) throws Throwable {
        matchingUnit.newOrder(Order.OrderSide.Sell, broker, quantity, new OrderPrice(Limit.INSTANCE, price));
    }

    @Then("^remaining buy order book depth is (\\d+)$")
    public void remaining_buy_order_book_depth_is(int expectedDepth) throws Throwable {

        assertThat(matchingUnit.orderBookDepth(Order.OrderSide.Buy), is(expectedDepth));
    }

    @Then("^remaining sell order book depth is (\\d+)$")
    public void remaining_sell_order_book_depth_is(int expectedDepth) throws Throwable {

        assertThat(matchingUnit.orderBookDepth(Order.OrderSide.Sell), is(expectedDepth));
    }
}
