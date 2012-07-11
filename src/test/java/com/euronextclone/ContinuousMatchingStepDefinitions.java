package com.euronextclone;

import com.euronextclone.ordertypes.MarketToLimit;
import cucumber.annotation.en.Given;
import cucumber.annotation.en.Then;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created with IntelliJ IDEA.
 * User: eprystupa
 * Date: 7/11/12
 * Time: 9:54 AM
 */
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

    @Then("^remaining buy order book depth is (\\d+)$")
    public void remaining_buy_order_book_depth_is(int expectedDepth) throws Throwable {

        assertThat(matchingUnit.orderBookDepth(Order.OrderSide.Buy), is(expectedDepth));
    }

    @Then("^remaining sell order book depth is (\\d+)$")
    public void remaining_sell_order_book_depth_is(int expectedDepth) throws Throwable {

        assertThat(matchingUnit.orderBookDepth(Order.OrderSide.Sell), is(expectedDepth));
    }
}
