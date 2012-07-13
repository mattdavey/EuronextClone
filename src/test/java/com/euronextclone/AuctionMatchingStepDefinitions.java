package com.euronextclone;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import cucumber.annotation.en.Given;
import cucumber.annotation.en.Then;
import cucumber.annotation.en.When;
import cucumber.table.DataTable;

import java.util.List;

import static junit.framework.Assert.assertEquals;

public class AuctionMatchingStepDefinitions {

    private final MatchingUnit matchingUnit;

    public AuctionMatchingStepDefinitions(World world) {
        this.matchingUnit = world.getMatchingUnit();
    }

    @Given("^that reference price is ([0-9]*\\.?[0-9]+)$")
    public void that_reference_price_is_(double price) throws Throwable {
        // Express the Regexp above with the code you wish you had
//        throw new PendingException();
    }

    @Given("^the following orders submitted to the book:$")
    public void the_following_orders_submitted_to_the_book(DataTable ordersTable) throws Throwable {

        List<MontageRow> rows = ordersTable.asList(MontageRow.class);

        Iterable<Order> bids = FluentIterable.from(rows).filter(MontageRow.NON_EMPTY_BID).transform(MontageRow.TO_BID);
        for (Order bid : bids) {
            matchingUnit.addOrder(bid.getSide(), bid.getBroker(), bid.getQuantity(), bid.getPrice());
        }

        Iterable<Order> asks = FluentIterable.from(rows).filter(MontageRow.NON_EMPTY_ASK).transform(MontageRow.TO_ASK);
        for (Order ask : asks) {
            matchingUnit.addOrder(ask.getSide(), ask.getBroker(), ask.getQuantity(), ask.getPrice());
        }
    }

    @When("^class auction completes$")
    public void class_auction_completes() throws Throwable {

        matchingUnit.auction();
    }

    @Then("^the book looks like:$")
    public void the_book_looks_like(DataTable expectedBooks) throws Throwable {
        List<MontageRow> rows = expectedBooks.asList(MontageRow.class);
        List<OrderRow> expectedBids = FluentIterable.from(rows).filter(MontageRow.NON_EMPTY_BID).transform(MontageRow.TO_TEST_BID).toImmutableList();
        List<OrderRow> expectedAsks = FluentIterable.from(rows).filter(MontageRow.NON_EMPTY_ASK).transform(MontageRow.TO_TEST_ASK).toImmutableList();

        List<OrderRow> actualBids = FluentIterable.from(matchingUnit.getOrders(Order.OrderSide.Buy)).transform(OrderRow.FROM_ORDER).toImmutableList();
        List<OrderRow> actualAsks = FluentIterable.from(matchingUnit.getOrders(Order.OrderSide.Sell)).transform(OrderRow.FROM_ORDER).toImmutableList();

        assertEquals(expectedBids, actualBids);
        assertEquals(expectedAsks, actualAsks);
    }

    private static class OrderRow {
        private String broker;
        private int orderId;
        private int quantity;
        private String price;

        public static final Function<? super Order, OrderRow> FROM_ORDER = new Function<Order, OrderRow>() {
            @Override
            public OrderRow apply(final Order input) {
                OrderRow orderRow = new OrderRow();
                orderRow.setBroker(input.getBroker());
                orderRow.setOrderId(input.getId());
                orderRow.setPrice(input.getPrice().toString());
                orderRow.setQuantity(input.getQuantity());
                return orderRow;
            }
        };

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            OrderRow orderRow = (OrderRow) o;

            if (orderId != orderRow.orderId) return false;
            if (quantity != orderRow.quantity) return false;
            if (!broker.equals(orderRow.broker)) return false;
            if (!price.equals(orderRow.price)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = broker.hashCode();
            result = 31 * result + orderId;
            result = 31 * result + quantity;
            result = 31 * result + price.hashCode();
            return result;
        }

        public String getBroker() {
            return broker;
        }

        public void setBroker(String broker) {
            this.broker = broker;
        }

        public int getOrderId() {
            return orderId;
        }

        public void setOrderId(int orderId) {
            this.orderId = orderId;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }
    }

    private static class MontageRow {

        private String bidBroker;
        private Integer bidOrderId;
        private Integer bidQuantity;
        private String bidPrice;
        private String askBroker;
        private Integer askOrderId;
        private Integer askQuantity;
        private String askPrice;

        public static final Predicate<? super MontageRow> NON_EMPTY_BID = new Predicate<MontageRow>() {
            @Override
            public boolean apply(final MontageRow input) {
                return input.bidBroker != null && !"".equals(input.bidBroker);
            }
        };

        public static final Function<? super MontageRow, OrderRow> TO_TEST_BID = new Function<MontageRow, OrderRow>() {
            @Override
            public OrderRow apply(final MontageRow input) {
                OrderRow orderRow = new OrderRow();
                orderRow.setBroker(input.bidBroker);
                orderRow.setOrderId(input.bidOrderId);
                orderRow.setPrice(input.bidPrice);
                orderRow.setQuantity(input.bidQuantity);
                return orderRow;
            }
        };

        public static final Function<? super MontageRow, OrderRow> TO_TEST_ASK = new Function<MontageRow, OrderRow>() {
            @Override
            public OrderRow apply(final MontageRow input) {
                OrderRow orderRow = new OrderRow();
                orderRow.setBroker(input.askBroker);
                orderRow.setOrderId(input.askOrderId);
                orderRow.setPrice(input.askPrice);
                orderRow.setQuantity(input.askQuantity);
                return orderRow;
            }
        };

        public static final Function<? super MontageRow, Order> TO_BID = new Function<MontageRow, Order>() {
            @Override
            public Order apply(final MontageRow input) {
                OrderPrice price = parseOrderPrice(input.bidPrice);
                return new Order(input.bidBroker, input.bidQuantity, price, Order.OrderSide.Buy);
            }
        };
        public static final Predicate<? super MontageRow> NON_EMPTY_ASK = new Predicate<MontageRow>() {
            @Override
            public boolean apply(final MontageRow input) {
                return input.askBroker != null && !"".equals(input.askBroker);
            }
        };
        public static final Function<? super MontageRow, Order> TO_ASK = new Function<MontageRow, Order>() {
            @Override
            public Order apply(final MontageRow input) {
                OrderPrice price = parseOrderPrice(input.askPrice);
                return new Order(input.askBroker, input.askQuantity, price, Order.OrderSide.Sell);
            }
        };


        private static OrderPrice parseOrderPrice(String price) {
            if ("MTL".equals(price)) {
                return new OrderPrice(OrderType.MarketToLimit);
            }
            if ("MO".equals(price)) {
                return new OrderPrice(OrderType.MarketOrder);
            }
            return new OrderPrice(OrderType.Limit, Double.parseDouble(price));
        }
    }
}
