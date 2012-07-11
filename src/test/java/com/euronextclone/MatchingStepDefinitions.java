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

import java.util.List;

public class MatchingStepDefinitions {

    private final MatchingUnit matchingUnit = new MatchingUnit();

    @Given("^that reference price is ([-+]?[0-9]*\\.?[0-9]+)$")
    public void that_reference_price_is_(int arg1) throws Throwable {
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
    public void the_following_trades_are_generated(DataTable trades) throws Throwable {
        // Express the Regexp above with the code you wish you had
        // For automatic conversion, change DataTable to List<YourType>
        // throw new PendingException();
    }

    @Then("^the book looks like:$")
    public void the_book_looks_like(DataTable expectedBooks) throws Throwable {
        // Express the Regexp above with the code you wish you had
        // For automatic conversion, change DataTable to List<YourType>
        // throw new PendingException();
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
        public static final Function<? super MontageRow,Order> TO_BID = new Function<MontageRow, Order>() {
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
        public static final Function<? super MontageRow,Order> TO_ASK = new Function<MontageRow, Order>() {
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
            if("MTL".equals(price)){
                return new OrderPrice(MarketToLimit.INSTANCE);
            }
            if("MO".equals(price)) {
                return new OrderPrice(MarketOrder.INSTANCE);
            }
            return new OrderPrice(Limit.INSTANCE, Double.parseDouble(price));
        }
    }
}
