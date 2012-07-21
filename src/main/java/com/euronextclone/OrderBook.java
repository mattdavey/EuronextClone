package com.euronextclone;

import com.euronextclone.ordertypes.Limit;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import hu.akarnokd.reactive4java.reactive.DefaultObservable;
import hu.akarnokd.reactive4java.reactive.Observable;
import hu.akarnokd.reactive4java.reactive.Observer;

import javax.annotation.Nonnull;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class OrderBook implements Observable<Trade> {
    private final LinkedList<Order> orders = new LinkedList<Order>();
    private final Order.OrderSide bookSide;
    private Double bestLimit;

    /**
     * The observable helper.
     */
    final private DefaultObservable<Trade> notifier = new DefaultObservable<Trade>();

    public OrderBook(final Order.OrderSide side) {
        bookSide = side;
        bestLimit = side == Order.OrderSide.Buy ? Double.MAX_VALUE : 0;
    }

    public Double getBestLimit() {
        return bestLimit;
    }

    public boolean match2(final Order newOrder, final TradingPhase currentTradingPhase, final Double imp) {

        Double newOrderPrice = newOrder.getOrderTypeLimit().providesLimit() ? newOrder.getOrderTypeLimit().getLimit() : null;

        List<Order> toRemove = new ArrayList<Order>();
        List<Order> toAdd = new ArrayList<Order>();

        OrderType newOrderType = newOrder.getOrderTypeLimit().getOrderType();

        for (final Order order : orders) {

            // Determine the price at which the trade happens
            final Double bookOrderPrice = order.getOrderTypeLimit().price(bookSide, bestLimit);

            Double tradePrice = determineTradePrice(newOrderPrice, bookOrderPrice);
            if (tradePrice == null) {
                break;
            }

            if (currentTradingPhase == TradingPhase.CoreAuction) {
                tradePrice = imp;
            }

            // Determine the amount to trade
            int tradeQuantity = determineTradeQuantity(newOrder, order);

            // Trade
            newOrder.decrementQuantity(tradeQuantity);
            order.decrementQuantity(tradeQuantity);
            generateTrade(newOrder, order, tradeQuantity, tradePrice);

            if (order.getQuantity() == 0) {
                toRemove.add(order);
            } else {
                if (order.getOrderTypeLimit().getOrderType() == OrderType.MarketToLimit) {
                    toRemove.add(order);
                    toAdd.add(order.convertTo(new Limit(tradePrice)));
                }
                break;
            }

            if (newOrderType == OrderType.MarketToLimit) {
                newOrderPrice = tradePrice;
                newOrderType = OrderType.Limit;
            }
        }

        orders.removeAll(toRemove);
        for (Order order : toAdd) {
            placeOrderInBook(order);
        }

        return newOrder.getQuantity() != 0;
    }

    private int determineTradeQuantity(Order newOrder, Order order) {
        return Math.min(newOrder.getQuantity(), order.getQuantity());
    }

    private Double determineTradePrice(Double newOrderPrice, Double bookOrderPrice) {

        if (newOrderPrice == null && bookOrderPrice == null) {
            return bestLimit;
        }

        if (newOrderPrice == null) {
            return bookOrderPrice;
        }

        if (bookOrderPrice == null) {
            return newOrderPrice;
        }

        if (bookSide == Order.OrderSide.Buy && bookOrderPrice >= newOrderPrice) {
            return bookOrderPrice;
        }

        if (bookSide == Order.OrderSide.Sell && bookOrderPrice <= newOrderPrice) {
            return bookOrderPrice;
        }

        return null;  // Can't trade
    }

    public boolean match(final Order newOrder, final TradingPhase currentTradingPhase, final Double imp) {
        // Bit of a cheat
        if (currentTradingPhase == TradingPhase.CoreAuction) {
            bestLimit = imp;
        }

        final ArrayList<Order> rebalance = new ArrayList<Order>();

        for (final Order order : orders) {

            if (currentTradingPhase == TradingPhase.CoreAuction) {
                if ((bookSide == Order.OrderSide.Buy && order.getOrderTypeLimit().value(bestLimit) >= imp) ||
                        (bookSide == Order.OrderSide.Sell && order.getOrderTypeLimit().value(bestLimit) <= imp)) {
                    order.getOrderTypeLimit().setLimit(imp);
                } else {
                    continue;
                }
            } else if (order.getOrderTypeLimit().value(bestLimit) != newOrder.getOrderTypeLimit().value(bestLimit)) {
                if (newOrder.getOrderTypeLimit().getOrderType() == OrderType.MarketOrder &&
                        newOrder.getPartlyFilled() &&
                        order.getOrderTypeLimit().getOrderType() == OrderType.Limit) {

                    // Rule 1 of Pure Market Order continuous trading

                } else {
                    if (order.getOrderTypeLimit().getOrderType() != OrderType.MarketOrder)
                        continue;
                }
            }

            if (order.getQuantity() == newOrder.getQuantity()) {
                orders.remove(order);
                double price = imp != null ? imp : newOrder.getOrderTypeLimit().getLimit();
                generateTrade(newOrder, order, order.getQuantity(), price);
                newOrder.decrementQuantity(newOrder.getQuantity());
                break;
            }

            if (order.getQuantity() > newOrder.getQuantity()) {
                if (!order.getOrderTypeLimit().hasLimit()) {
                    order.getOrderTypeLimit().convertToLimit(newOrder.getOrderTypeLimit().getLimit());
                }

                order.decrementQuantity(newOrder.getQuantity());
                if (!newOrder.getOrderTypeLimit().hasLimit() && !order.getOrderTypeLimit().hasLimit()) {
                    generateTrade(newOrder, order, newOrder.getQuantity(), newOrder.getOrderTypeLimit().getLimit());
                } else {
                    generateTrade(newOrder, order, newOrder.getQuantity(), order.getOrderTypeLimit().getLimit());
                }

                newOrder.decrementQuantity(newOrder.getQuantity());
                break;
            }

            if (order.getQuantity() < newOrder.getQuantity()) {
                rebalance.add(order);
                if (!newOrder.getOrderTypeLimit().hasLimit() && newOrder.getOrderTypeLimit().getOrderType() == OrderType.MarketToLimit) {
                    newOrder.getOrderTypeLimit().convertToLimit(bestLimit);
                    order.getOrderTypeLimit().convertToLimit(bestLimit);
                }

                generateTrade(newOrder, order, order.getQuantity(), order.getOrderTypeLimit().getLimit());
                newOrder.decrementQuantity(order.getQuantity());
                order.decrementQuantity(order.getQuantity());
            }
        }

        if (rebalance.size() > 0)
            orders.removeAll(rebalance);

        validatePegOrderPositions();
        return newOrder.getQuantity() != 0;
    }

    private void generateTrade(final Order newOrder, final Order order, final int tradeQuantity, final double price) {
        notifier.next(new Trade(newOrder.getSide() == Order.OrderSide.Buy ? newOrder.getBroker() : order.getBroker(),
                newOrder.getSide() == Order.OrderSide.Sell ? newOrder.getBroker() : order.getBroker(),
                tradeQuantity, price));
    }


    public void removeAll(Collection<Order> ordersToRemove) {
        orders.removeAll(ordersToRemove);
        validatePegOrderPositions();
    }

    public void remove(Order order) {
        orders.remove(order);
        validatePegOrderPositions();
    }

    public int add(final Order order) {
        int position = placeOrderInBook(order);
        validatePegOrderPositions();
        return position;
    }

    private int placeOrderInBook(final Order newOrder) {
        int count = 0;
        for (final Order order : orders) {

            final int compare = order.compareTo(newOrder, bestLimit);
            if (compare < 0) {
                break;
            }

            count++;
        }

        orders.add(count, newOrder);
        return count;
    }

    private void validatePegOrderPositions() {
        if (orders.size() == 0)
            return;

        Double oldBestLimit = bestLimit;
        bestLimit = calculateBestLimit();
        boolean changed = oldBestLimit != null ? !oldBestLimit.equals(bestLimit) : bestLimit != null;

        if (changed) {
            List<Order> pegged = FluentIterable.from(orders)
                    .filter(new Predicate<Order>() {
                        @Override
                        public boolean apply(final Order input) {
                            return input.getOrderTypeLimit().canPegLimit();
                        }
                    }).toImmutableList();

            orders.removeAll(pegged);
            for (Order order : pegged) {
                placeOrderInBook(order.convertTo(order.getOrderTypeLimit()));
            }
        }

        // Cheat if the order book only has a PEG left
        if (orders.size() == 1 && orders.get(0).getOrderTypeLimit().getOrderType() == OrderType.Peg)
            orders.clear();

    }

    private Double calculateBestLimit() {
        // Order book should be good, just reset best
        for (final Order order : orders) {
            if (order.getOrderTypeLimit().providesLimit()) {
                return order.getOrderTypeLimit().getLimit();
            }
        }

        return bestLimit;
    }

    public int orderBookDepth() {
        return orders.size();
    }

    public void dump() {
        for (final Order order : orders)
            order.dump();
    }

    @Nonnull
    public Closeable register(@Nonnull Observer<? super Trade> observer) {
        return notifier.register(observer);
    }

    public List<Order> getOrders() {
        return orders;
    }

    class Temp {
        private Double d;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Temp temp = (Temp) o;

            return !(d != null ? !d.equals(temp.d) : temp.d != null);

        }

        @Override
        public int hashCode() {
            return d != null ? d.hashCode() : 0;
        }
    }
}
