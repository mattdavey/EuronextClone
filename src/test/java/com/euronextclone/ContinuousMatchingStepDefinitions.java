package com.euronextclone;

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

    @Then("^best limits are:$")
    public void best_limits_are(DataTable limitsTable) throws Throwable {
        final List<BestLimitRow> bestLimitRows = limitsTable.asList(BestLimitRow.class);
        for (BestLimitRow bestLimitRow : bestLimitRows) {
            assertThat(matchingUnit.getBestLimit(bestLimitRow.side), is(bestLimitRow.limit));
        }
    }

    @Then("^remaining buy order book depth is (\\d+)$")
    public void remaining_buy_order_book_depth_is(int expectedDepth) throws Throwable {

        assertThat(matchingUnit.orderBookDepth(Order.OrderSide.Buy), is(expectedDepth));
    }

    @Then("^remaining sell order book depth is (\\d+)$")
    public void remaining_sell_order_book_depth_is(int expectedDepth) throws Throwable {

        assertThat(matchingUnit.orderBookDepth(Order.OrderSide.Sell), is(expectedDepth));
    }

    private static class BestLimitRow {
        private Order.OrderSide side;
        private Double limit;
    }
}
