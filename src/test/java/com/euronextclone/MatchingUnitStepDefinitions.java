package com.euronextclone;

import com.euronextclone.ordertypes.*;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import cucumber.annotation.en.Given;
import cucumber.annotation.en.Then;
import cucumber.annotation.en.When;
import cucumber.table.DataTable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    @Given("^that trading mode for security is \"([^\"]*)\" and phase is \"([^\"]*)\"$")
    public void that_trading_mode_for_security_is_and_phase_is(TradingMode tradingMode, TradingPhase phase) throws Throwable {
        matchingUnit.setTradingMode(tradingMode);
        matchingUnit.setTradingPhase(phase);
    }

    @Given("^the following orders are submitted in this order:$")
    public void the_following_orders_are_submitted_in_this_order(DataTable orderTable) throws Throwable {

        final List<OrderEntryRow> orderRows = orderTable.asList(OrderEntryRow.class);
        for (OrderEntryRow orderRow : orderRows) {
            matchingUnit.addOrder(orderRow.side, orderRow.broker, orderRow.quantity, orderRow.getOrderTypeLimit());
        }
    }

    @When("^class auction completes$")
    public void class_auction_completes() throws Throwable {
        matchingUnit.auction();
    }

    @Then("^\"([^\"]*)\" order book should look like:$")
    public void order_book_should_look_like(Order.OrderSide side, DataTable orderTable) throws Throwable {
        final List<OrderBookRow> expectedOrders = orderTable.asList(OrderBookRow.class);
        final List<OrderBookRow> actualOrders = FluentIterable
                .from(matchingUnit.getOrders(side))
                .transform(OrderBookRow.FROM_Order(matchingUnit))
                .toImmutableList();

        assertEquals(expectedOrders, actualOrders);
    }

    @Then("^\"([^\"]*)\" order book is empty$")
    public void order_book_is_empty(Order.OrderSide side) throws Throwable {
        final List<OrderBookRow> actualOrders = FluentIterable
                .from(matchingUnit.getOrders(side))
                .transform(OrderBookRow.FROM_Order(matchingUnit))
                .toImmutableList();

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

        generatedTrades.clear();
    }

    @Then("^no trades should have been generated$")
    public void no_trades_should_have_been_generated() throws Throwable {
        assertThat(generatedTrades, is(empty()));
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

    private static class OrderEntryRow {
        private static final Pattern PEG = Pattern.compile("Peg(?:\\[(.+)\\])?", Pattern.CASE_INSENSITIVE);
        private String broker;
        private Order.OrderSide side;
        private int quantity;
        private String price;

        public OrderTypeLimit getOrderTypeLimit() {

            Matcher peg = PEG.matcher(price);
            if (peg.matches()) {
                String limit = peg.group(1);
                return limit != null ? new PegWithLimit(Double.parseDouble(limit)) : new Peg();
            }
            if ("MTL".compareToIgnoreCase(price) == 0) {
                return new MarketToLimit();
            }
            if ("MO".compareToIgnoreCase(price) == 0) {
                return new Market();
            }

            return new Limit(Double.parseDouble(price));
        }
    }

    private static class OrderBookRow {
        private String broker;
        private Order.OrderSide side;
        private int quantity;
        private OrderType orderType;
        private Double price;

        public static Function<? super Order, OrderBookRow> FROM_Order(final MatchingUnit matchingUnit) {
            return new Function<Order, OrderBookRow>() {
                @Override
                public OrderBookRow apply(final Order order) {
                    final Order.OrderSide side = order.getSide();
                    final OrderTypeLimit orderTypeLimit = order.getOrderTypeLimit();
                    final Double bestLimit = matchingUnit.getBestLimit(side);
                    final OrderBookRow orderRow = new OrderBookRow();

                    orderRow.broker = order.getBroker();
                    orderRow.side = order.getSide();
                    orderRow.price = orderTypeLimit.price(side, matchingUnit.getBestLimit(side));
                    orderRow.orderType = orderTypeLimit.getOrderType();
                    orderRow.quantity = order.getQuantity();
                    return orderRow;
                }
            };
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            OrderBookRow orderRow = (OrderBookRow) o;

            if (quantity != orderRow.quantity) return false;
            if (!broker.equals(orderRow.broker)) return false;
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
            return result;
        }

        @Override
        public String toString() {
            return "OrderBookRow{" +
                    "broker='" + broker + '\'' +
                    ", side=" + side +
                    ", quantity=" + quantity +
                    ", orderType=" + orderType +
                    ", price=" + price +
                    '}';
        }
    }
}