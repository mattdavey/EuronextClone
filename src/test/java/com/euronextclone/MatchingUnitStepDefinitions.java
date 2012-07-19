package com.euronextclone;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import cucumber.annotation.en.Given;
import cucumber.annotation.en.Then;
import cucumber.annotation.en.When;
import cucumber.table.DataTable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;

public class MatchingUnitStepDefinitions {

    private final MatchingUnit matchingUnit;
    private final List<Trade> generatedTrades;

    public MatchingUnitStepDefinitions(World world) {
        matchingUnit = world.getMatchingUnit();
        generatedTrades = world.getGeneratedTrades();
    }

    @Given("^that trading mode for security is \"([^\"]*)\"$")
    public void that_trading_mode_for_security_is(TradingMode tradingMode) throws Throwable {
        matchingUnit.setTradingMode(tradingMode);
    }

    @When("^class auction completes$")
    public void class_auction_completes() throws Throwable {
        matchingUnit.auction();
    }

    @Then("^\"([^\"]*)\" order book should look like:$")
    public void order_book_should_look_like(Order.OrderSide side, DataTable orderTable) throws Throwable {
        final List<OrderRow> expectedOrders = orderTable.asList(OrderRow.class);
        final List<OrderRow> actualOrders = FluentIterable.from(matchingUnit.getOrders(side)).transform(OrderRow.FROM_Order).toImmutableList();

        assertEquals(expectedOrders, actualOrders);
    }

    @Then("^\"([^\"]*)\" order book is empty$")
    public void order_book_is_empty(Order.OrderSide side) throws Throwable {
        final List<OrderRow> actualOrders = FluentIterable.from(matchingUnit.getOrders(side)).transform(OrderRow.FROM_Order).toImmutableList();

        assertThat(actualOrders, is(empty()));
    }

    @Then("^the calculated IMP is:$")
    public void the_calculated_IMP_is(List<Double> imp) {
        assertThat(matchingUnit.getIndicativeMatchingPrice(), is(imp.get(0)));
    }

    @Then("^the following trades are generated:$")
    public void the_following_trades_are_generated(DataTable expectedTradesTable) throws Throwable {
        final List<TradeRow> expectedTrades = expectedTradesTable.asList(TradeRow.class);
        final List<TradeRow> actualTrades = FluentIterable.from(generatedTrades).transform(TradeRow.FROM_TRADE).toImmutableList();
        assertEquals(expectedTrades, actualTrades);
    }

    @Then("^no trades are generated$")
    public void no_trades_are_generated() throws Throwable {
        List<TradeRow> actualTrades = FluentIterable.from(generatedTrades).transform(TradeRow.FROM_TRADE).toImmutableList();
        List<TradeRow> empty = new ArrayList<TradeRow>();
        assertEquals(empty, actualTrades);
    }

    private static class TradeRow {
        private String buyingBroker;
        private String sellingBroker;
        private int quantity;
        private double price;

        public static final Function<? super Trade, TradeRow> FROM_TRADE = new Function<Trade, TradeRow>() {
            @Override
            public TradeRow apply(Trade input) {
                TradeRow tradeRow = new TradeRow();
                tradeRow.setBuyingBroker(input.getBuyBroker());
                tradeRow.setPrice(input.getPrice());
                tradeRow.setQuantity(input.getQuantity());
                tradeRow.setSellingBroker(input.getSellBroker());
                return tradeRow;
            }
        };

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TradeRow tradeRow = (TradeRow) o;

            if (Double.compare(tradeRow.price, price) != 0) return false;
            if (quantity != tradeRow.quantity) return false;
            if (!buyingBroker.equals(tradeRow.buyingBroker)) return false;
            if (!sellingBroker.equals(tradeRow.sellingBroker)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result;
            long temp;
            result = buyingBroker.hashCode();
            result = 31 * result + sellingBroker.hashCode();
            result = 31 * result + quantity;
            temp = price != +0.0d ? Double.doubleToLongBits(price) : 0L;
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            return result;
        }

        @Override
        public String toString() {
            return "TradeRow{" +
                    "buyingBroker='" + buyingBroker + '\'' +
                    ", sellingBroker='" + sellingBroker + '\'' +
                    ", quantity=" + quantity +
                    ", price=" + price +
                    '}';
        }

        public void setBuyingBroker(String buyingBroker) {
            this.buyingBroker = buyingBroker;
        }

        public void setSellingBroker(String sellingBroker) {
            this.sellingBroker = sellingBroker;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public void setPrice(double price) {
            this.price = price;
        }
    }

    private static class OrderRow {
        private String broker;
        private Order.OrderSide side;
        private int quantity;
        private OrderType orderType;
        private Double price;
        private Double limit;

        public static Function<? super Order, OrderRow> FROM_Order = new Function<Order, OrderRow>() {
            @Override
            public OrderRow apply(@Nullable Order order) {
                final OrderTypeLimit orderTypeLimit = order.getOrderTypeLimit();
                final OrderRow orderRow = new OrderRow();

                orderRow.broker = order.getBroker();
                orderRow.side = order.getSide();
//                orderRow.price = orderTypeLimit.hasPrice()? orderTypeLimit.value() : null;
                orderRow.price = orderTypeLimit.hasLimit() ? orderTypeLimit.getLimit() : null;
                orderRow.orderType = orderTypeLimit.getOrderType();
                orderRow.quantity = order.getQuantity();
                return orderRow;
            }
        };

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            OrderRow orderRow = (OrderRow) o;

            if (quantity != orderRow.quantity) return false;
            if (!broker.equals(orderRow.broker)) return false;
            if (limit != null ? !limit.equals(orderRow.limit) : orderRow.limit != null) return false;
            if (orderType != orderRow.orderType) return false;
            if (price != null ? !price.equals(orderRow.price) : orderRow.price != null) return false;
            if (side != orderRow.side) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = broker.hashCode();
            result = 31 * result + side.hashCode();
            result = 31 * result + quantity;
            result = 31 * result + orderType.hashCode();
            result = 31 * result + (price != null ? price.hashCode() : 0);
            result = 31 * result + (limit != null ? limit.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "OrderRow{" +
                    "broker='" + broker + '\'' +
                    ", side=" + side +
                    ", quantity=" + quantity +
                    ", orderType=" + orderType +
                    ", price=" + price +
                    ", limit=" + limit +
                    '}';
        }
    }
}