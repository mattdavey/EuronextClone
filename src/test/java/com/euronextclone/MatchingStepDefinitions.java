package com.euronextclone;

import com.euronextclone.ordertypes.Limit;
import com.euronextclone.ordertypes.MarketOrder;
import com.euronextclone.ordertypes.MarketToLimit;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import cucumber.annotation.en.Given;
import cucumber.annotation.en.Then;
import cucumber.annotation.en.When;
import cucumber.table.DataTable;
import hu.akarnokd.reactive4java.reactive.Observer;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class MatchingStepDefinitions {

    private final MatchingUnit matchingUnit = new MatchingUnit();
    private final List<Trade> generatedTrades = new ArrayList<Trade>();

    public MatchingStepDefinitions() {
        matchingUnit.register(new Observer<Trade>() {
            @Override
            public void next(Trade trade) {
                generatedTrades.add(trade);
            }

            @Override
            public void error(@Nonnull Throwable throwable) {
            }

            @Override
            public void finish() {
            }
        });
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

    @Then("^the following trades are generated:$")
    public void the_following_trades_are_generated(DataTable expectedTradesTable) throws Throwable {
        List<TradeRow> rows = expectedTradesTable.asList(TradeRow.class);

        List<Trade> expectedTrades = FluentIterable.from(rows).transform(TradeRow.TO_TRADE).toImmutableList();
        //assertEquals(expectedTrades, generatedTrades);
    }

    @Then("^the book looks like:$")
    public void the_book_looks_like(DataTable expectedBooks) throws Throwable {
        List<MontageRow> rows = expectedBooks.asList(MontageRow.class);
        List<TestOrder> expectedBids = FluentIterable.from(rows).filter(MontageRow.NON_EMPTY_BID).transform(MontageRow.TO_TEST_BID).toImmutableList();
        List<TestOrder> expectedAsks = FluentIterable.from(rows).filter(MontageRow.NON_EMPTY_ASK).transform(MontageRow.TO_TEST_ASK).toImmutableList();

        List<TestOrder> actualBids = FluentIterable.from(matchingUnit.getOrders(Order.OrderSide.Buy)).transform(TestOrder.FROM_ORDER).toImmutableList();
        List<TestOrder> actualAsks = FluentIterable.from(matchingUnit.getOrders(Order.OrderSide.Sell)).transform(TestOrder.FROM_ORDER).toImmutableList();

        assertEquals(expectedBids, actualBids);
        assertEquals(expectedAsks, actualAsks);
    }

    private static class TestOrder {
        private String broker;
        private int orderId;
        private int quantity;
        private String price;

        public static final Function<? super Order, TestOrder> FROM_ORDER = new Function<Order, TestOrder>() {
            @Override
            public TestOrder apply(final Order input) {
                TestOrder order = new TestOrder();
                order.setBroker(input.getBroker());
                order.setOrderId(input.getId());
                order.setPrice(input.getPrice().toString());
                order.setQuantity(input.getQuantity());
                return order;
            }
        };

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TestOrder order = (TestOrder) o;

            if (orderId != order.orderId) return false;
            if (quantity != order.quantity) return false;
            if (!broker.equals(order.broker)) return false;
            if (!price.equals(order.price)) return false;

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

        public static final Function<? super MontageRow, TestOrder> TO_TEST_BID = new Function<MontageRow, TestOrder>() {
            @Override
            public TestOrder apply(final MontageRow input) {
                TestOrder order = new TestOrder();
                order.setBroker(input.bidBroker);
                order.setOrderId(input.bidOrderId);
                order.setPrice(input.bidPrice);
                order.setQuantity(input.bidQuantity);
                return order;
            }
        };

        public static final Function<? super MontageRow, TestOrder> TO_TEST_ASK = new Function<MontageRow, TestOrder>() {
            @Override
            public TestOrder apply(final MontageRow input) {
                TestOrder order = new TestOrder();
                order.setBroker(input.askBroker);
                order.setOrderId(input.askOrderId);
                order.setPrice(input.askPrice);
                order.setQuantity(input.askQuantity);
                return order;
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

        public String getBidBroker() {
            return bidBroker;
        }

        public void setBidBroker(String bidBroker) {
            this.bidBroker = bidBroker;
        }

        public Integer getBidOrderId() {
            return bidOrderId;
        }

        public void setBidOrderId(Integer bidOrderId) {
            this.bidOrderId = bidOrderId;
        }

        public Integer getBidQuantity() {
            return bidQuantity;
        }

        public void setBidQuantity(Integer bidQuantity) {
            this.bidQuantity = bidQuantity;
        }

        public String getBidPrice() {
            return bidPrice;
        }

        public void setBidPrice(String bidPrice) {
            this.bidPrice = bidPrice;
        }

        public String getAskBroker() {
            return askBroker;
        }

        public void setAskBroker(String askBroker) {
            this.askBroker = askBroker;
        }

        public Integer getAskOrderId() {
            return askOrderId;
        }

        public void setAskOrderId(Integer askOrderId) {
            this.askOrderId = askOrderId;
        }

        public Integer getAskQuantity() {
            return askQuantity;
        }

        public void setAskQuantity(Integer askQuantity) {
            this.askQuantity = askQuantity;
        }

        public String getAskPrice() {
            return askPrice;
        }

        public void setAskPrice(String askPrice) {
            this.askPrice = askPrice;
        }

        private static OrderPrice parseOrderPrice(String price) {
            if ("MTL".equals(price)) {
                return new OrderPrice(MarketToLimit.INSTANCE);
            }
            if ("MO".equals(price)) {
                return new OrderPrice(MarketOrder.INSTANCE);
            }
            return new OrderPrice(Limit.INSTANCE, Double.parseDouble(price));
        }
    }

    private static class TradeRow {
        private String buyingBroker;
        private String sellingBroker;
        private int quantity;
        private double price;


        public static final Function<? super TradeRow, Trade> TO_TRADE = new Function<TradeRow, Trade>() {
            @Override
            public Trade apply(TradeRow input) {
                return new Trade(input.getSellingBroker(), input.getBuyingBroker(), input.getQuantity(), input.getPrice());
            }
        };

        public String getBuyingBroker() {
            return buyingBroker;
        }

        public void setBuyingBroker(String buyingBroker) {
            this.buyingBroker = buyingBroker;
        }

        public String getSellingBroker() {
            return sellingBroker;
        }

        public void setSellingBroker(String sellingBroker) {
            this.sellingBroker = sellingBroker;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }
    }
}
