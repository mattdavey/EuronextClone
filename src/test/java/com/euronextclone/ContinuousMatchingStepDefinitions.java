package com.euronextclone;

import cucumber.annotation.en.Given;
import cucumber.annotation.en.Then;
import cucumber.table.DataTable;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ContinuousMatchingStepDefinitions {

    private final MatchingUnit matchingUnit;

    public ContinuousMatchingStepDefinitions(World world) {
        this.matchingUnit = world.getMatchingUnit();
    }

    @Given("^the following orders are submitted in this order:$")
    public void the_following_orders_are_submitted_in_this_order(DataTable orderTable) throws Throwable {

        final List<OrderRow> orderRows = orderTable.asList(OrderRow.class);
        for (OrderRow orderRow : orderRows) {

            if (orderRow.price == null) {
                matchingUnit.addOrder(orderRow.side, orderRow.broker, orderRow.quantity, new OrderTypeLimit(orderRow.orderType));
            } else {
                matchingUnit.addOrder(orderRow.side, orderRow.broker, orderRow.quantity, new OrderTypeLimit(orderRow.orderType, orderRow.price));
            }
        }
    }


    @Then("^best limits are:$")
    public void best_limits_are(DataTable limitsTable) throws Throwable {
        final List<BestLimitRow> bestLimitRows = limitsTable.asList(BestLimitRow.class);
        for (BestLimitRow bestLimitRow : bestLimitRows) {
            assertThat(matchingUnit.getBestLimit(bestLimitRow.side), is(bestLimitRow.limit));
        }
    }

    @Given("^the MTL buy order from broker \"([^\"]*)\" for (\\d+) shares$")
    public void the_MTL_buy_order_from_broker_for_shares(String broker, int quantity) throws Throwable {

        matchingUnit.addOrder(Order.OrderSide.Buy, broker, quantity, new OrderTypeLimit(OrderType.MarketToLimit));
    }

    @Given("^the MTL sell order from broker \"([^\"]*)\" for (\\d+) shares$")
    public void the_MTL_sell_order_from_broker_for_shares(String broker, int quantity) throws Throwable {

        matchingUnit.addOrder(Order.OrderSide.Sell, broker, quantity, new OrderTypeLimit(OrderType.MarketOrder));
    }

    @Given("^the MO buy order from broker \"([^\"]*)\" for (\\d+) shares$")
    public void the_MO_buy_order_from_broker_for_shares(String broker, int quantity) throws Throwable {
        matchingUnit.addOrder(Order.OrderSide.Buy, broker, quantity, new OrderTypeLimit(OrderType.MarketOrder));
    }

    @Given("^the Limit sell order from broker \"([^\"]*)\" for (\\d+) shares at ([0-9]*\\.?[0-9]+) price$")
    public void the_Limit_sell_order_from_broker_for_shares(String broker, int quantity, double price) throws Throwable {
        matchingUnit.addOrder(Order.OrderSide.Sell, broker, quantity, new OrderTypeLimit(OrderType.Limit, price));
    }

    @Given("^the Limit buy order from broker \"([^\"]*)\" for (\\d+) shares at ([0-9]*\\.?[0-9]+) price$")
    public void the_Limit_buy_order_from_broker_for_shares(String broker, int quantity, double price) throws Throwable {
        matchingUnit.addOrder(Order.OrderSide.Sell, broker, quantity, new OrderTypeLimit(OrderType.Limit, price));
    }

    @Then("^remaining buy order book depth is (\\d+)$")
    public void remaining_buy_order_book_depth_is(int expectedDepth) throws Throwable {

        assertThat(matchingUnit.orderBookDepth(Order.OrderSide.Buy), is(expectedDepth));
    }

    @Then("^remaining sell order book depth is (\\d+)$")
    public void remaining_sell_order_book_depth_is(int expectedDepth) throws Throwable {

        assertThat(matchingUnit.orderBookDepth(Order.OrderSide.Sell), is(expectedDepth));
    }

    private static class OrderRow {
        private String broker;
        private Order.OrderSide side;
        private int quantity;
        private OrderType orderType;
        private Double price;
    }

    private static class BestLimitRow {
        private Order.OrderSide side;
        private String limit;
    }
}
