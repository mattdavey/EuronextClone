package com.euronextclone;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import cucumber.annotation.en.Then;
import cucumber.table.DataTable;

import javax.annotation.Nullable;
import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: eprystupa
 * Date: 7/12/12
 * Time: 11:03 PM
 */
public class MatchingUnitStepDefinitions {

    private final MatchingUnit matchingUnit;
    private final List<Trade> generatedTrades;

    public MatchingUnitStepDefinitions(World world) {
        matchingUnit = world.getMatchingUnit();
        generatedTrades = world.getGeneratedTrades();
    }

    @Then("^\"([^\"]*)\" order book should look like:$")
    public void order_book_should_look_like(Order.OrderSide side, DataTable orderTable) throws Throwable {
        List<OrderRow> expectedOrders = orderTable.asList(OrderRow.class);
        List<OrderRow> actualOrders = FluentIterable.from(matchingUnit.getOrders(side)).transform(OrderRow.FROM_Order).toImmutableList();

        assertEquals(expectedOrders, actualOrders);
    }

    @Then("^the following trades are generated:$")
    public void the_following_trades_are_generated(DataTable expectedTradesTable) throws Throwable {
        List<TradeRow> expectedTrades = expectedTradesTable.asList(TradeRow.class);
        List<TradeRow> actualTrades = FluentIterable.from(generatedTrades).transform(TradeRow.FROM_TRADE).toImmutableList();
        assertEquals(expectedTrades, actualTrades);
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
                final OrderPrice orderPrice = order.getPrice();
                final OrderRow orderRow = new OrderRow();

                orderRow.broker = order.getBroker();
                orderRow.side = order.getSide();
                orderRow.price = orderPrice.hasPrice()? orderPrice.value() : null;
                orderRow.limit = orderPrice.hasLimit()? orderPrice.getLimit() : null;
                orderRow.orderType = orderPrice.getOrderType();
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
