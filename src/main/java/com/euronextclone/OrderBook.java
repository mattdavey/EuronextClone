package com.euronextclone;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import hu.akarnokd.reactive4java.reactive.DefaultObservable;
import hu.akarnokd.reactive4java.reactive.Observable;
import hu.akarnokd.reactive4java.reactive.Observer;

import javax.annotation.Nonnull;
import java.io.Closeable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class OrderBook implements Observable<Trade> {
    private final LinkedList<Order> orders = new LinkedList<Order>();
    private Double bestLimit;

    /**
     * The observable helper.
     */
    final private DefaultObservable<Trade> notifier = new DefaultObservable<Trade>();

    public OrderBook(final Order.OrderSide side) {
        bestLimit = side == Order.OrderSide.Buy ? Double.MAX_VALUE : 0;
    }

    public Double getBestLimit() {
        return bestLimit;
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

}
