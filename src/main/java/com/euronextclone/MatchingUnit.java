package com.euronextclone;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import hu.akarnokd.reactive4java.base.Action1;
import hu.akarnokd.reactive4java.reactive.DefaultObservable;
import hu.akarnokd.reactive4java.reactive.Observable;
import hu.akarnokd.reactive4java.reactive.Observer;
import hu.akarnokd.reactive4java.reactive.Reactive;

import javax.annotation.Nonnull;
import java.io.Closeable;
import java.util.*;

public class MatchingUnit implements Observable<Trade> {

    private final OrderBook buyOrderBook;
    private final OrderBook sellOrderBook;
    private TradingPhase tradingPhase = TradingPhase.CoreContinuous;
    private TradingMode tradingMode = TradingMode.Continuous;
    private Double referencePrice;
    private Double indicativeMatchingPrice;

    /**
     * The observable helper.
     */
    private final DefaultObservable<Trade> notifier = new DefaultObservable<Trade>();

    public MatchingUnit() {
        buyOrderBook = new OrderBook(Order.OrderSide.Buy);
        sellOrderBook = new OrderBook(Order.OrderSide.Sell);

        buyOrderBook.register(Reactive.toObserver(new Action1<Trade>() {
            public void invoke(Trade value) {
                notifier.next(value);
            }
        }));
        sellOrderBook.register(Reactive.toObserver(new Action1<Trade>() {
            public void invoke(Trade value) {
                notifier.next(value);
            }
        }));
    }

    public void setReferencePrice(Double referencePrice) {
        this.referencePrice = referencePrice;
    }

    public Double getIndicativeMatchingPrice() {

        final List<Double> eligiblePrices = getListOfEligiblePrices();
        final List<Integer> cumulativeBuy = getCumulativeQuantity(eligiblePrices, buyOrderBook, Order.OrderSide.Buy);
        final List<Integer> cumulativeSell = getCumulativeQuantity(eligiblePrices, sellOrderBook, Order.OrderSide.Sell);
        final List<VolumeAtPrice> totalTradeableVolume = getTotalTradeableVolume(eligiblePrices, cumulativeBuy, cumulativeSell);

        final List<VolumeAtPrice> maximumExecutableVolume = determineMaximumExecutableValue(totalTradeableVolume);
        if (maximumExecutableVolume.size() == 1) {
            return maximumExecutableVolume.get(0).price;
        }

        final List<VolumeAtPrice> minimumSurplus = establishMinimumSurplus(maximumExecutableVolume);
        if (minimumSurplus.size() == 1) {
            return minimumSurplus.get(0).price;
        }

        final Optional<Double> pressurePrice = ascertainWhereMarketPressureExists(minimumSurplus);
        if (pressurePrice.isPresent()) {
            return pressurePrice.get();
        }

        return consultReferencePrice(minimumSurplus);
    }

    private double consultReferencePrice(List<VolumeAtPrice> minimumSurplus) {
        final FluentIterable<VolumeAtPrice> minimumSurplusIterable = FluentIterable.from(minimumSurplus);

        if (!minimumSurplus.isEmpty()) {
            double minPotentialPrice;
            double maxPotentialPrice;

            if (minimumSurplusIterable.allMatch(VolumeAtPrice.NO_PRESSURE)) {
                minPotentialPrice = minimumSurplusIterable.first().get().price;
                maxPotentialPrice = minimumSurplusIterable.last().get().price;
            } else {
                minPotentialPrice = minimumSurplusIterable.filter(VolumeAtPrice.BUYING_PRESSURE).last().get().price;
                maxPotentialPrice = minimumSurplusIterable.filter(VolumeAtPrice.SELLING_PRESSURE).first().get().price;
            }

            if (referencePrice == null) {
                return minPotentialPrice;
            }

            if (referencePrice >= maxPotentialPrice) {
                return maxPotentialPrice;
            }

            if (referencePrice <= minPotentialPrice) {
                return minPotentialPrice;
            }
        }

        return referencePrice;
    }

    private List<VolumeAtPrice> establishMinimumSurplus(List<VolumeAtPrice> maximumExecutableVolume) {

        if (maximumExecutableVolume.isEmpty()) {
            return maximumExecutableVolume;
        }

        final int minSurplus = Collections.min(maximumExecutableVolume, VolumeAtPrice.ABSOLUTE_SURPLUS_COMPARATOR).getAbsoluteSurplus();

        FluentIterable<VolumeAtPrice> minSurplusOnly = FluentIterable.from(maximumExecutableVolume).filter(new Predicate<VolumeAtPrice>() {
            @Override
            public boolean apply(VolumeAtPrice input) {
                return input.getAbsoluteSurplus() == minSurplus;
            }
        });

        return minSurplusOnly.toImmutableList();
    }

    private List<VolumeAtPrice> determineMaximumExecutableValue(List<VolumeAtPrice> totalTradeableVolume) {

        if (totalTradeableVolume.isEmpty()) {
            return totalTradeableVolume;
        }

        final int maxVolume = Collections.max(totalTradeableVolume, VolumeAtPrice.TRADEABLE_VOLUME_COMPARATOR).getTradeableVolume();

        FluentIterable<VolumeAtPrice> maxVolumeOnly = FluentIterable.from(totalTradeableVolume).filter(new Predicate<VolumeAtPrice>() {
            @Override
            public boolean apply(VolumeAtPrice input) {
                return input.getTradeableVolume() == maxVolume;
            }
        });

        return maxVolumeOnly.toImmutableList();
    }

    public void setTradingMode(TradingMode tradingMode) {
        this.tradingMode = tradingMode;
    }

    public void setTradingPhase(TradingPhase tradingPhase) {
        this.tradingPhase = tradingPhase;
    }

    private static class VolumeAtPrice {
        private double price;
        private int buyVolume;
        private int sellVolume;

        public VolumeAtPrice(double price, int buyVolume, int sellVolume) {
            this.price = price;
            this.buyVolume = buyVolume;
            this.sellVolume = sellVolume;
        }

        public int getTradeableVolume() {
            return Math.min(buyVolume, sellVolume);
        }

        public int getSurplus() {
            return buyVolume - sellVolume;
        }

        public int getAbsoluteSurplus() {
            return Math.abs(getSurplus());
        }

        public static final Comparator<VolumeAtPrice> TRADEABLE_VOLUME_COMPARATOR = new Comparator<VolumeAtPrice>() {

            @Override
            public int compare(VolumeAtPrice volumeAtPrice1, VolumeAtPrice volumeAtPrice2) {
                Integer tradeableVolume1 = volumeAtPrice1.getTradeableVolume();
                Integer tradeableVolume2 = volumeAtPrice2.getTradeableVolume();
                return tradeableVolume1.compareTo(tradeableVolume2);
            }
        };

        public static final Comparator<VolumeAtPrice> ABSOLUTE_SURPLUS_COMPARATOR = new Comparator<VolumeAtPrice>() {

            @Override
            public int compare(VolumeAtPrice volumeAtPrice1, VolumeAtPrice volumeAtPrice2) {
                Integer surplus1 = volumeAtPrice1.getAbsoluteSurplus();
                Integer surplus2 = volumeAtPrice2.getAbsoluteSurplus();
                return surplus1.compareTo(surplus2);
            }
        };

        public static Predicate<? super VolumeAtPrice> BUYING_PRESSURE = new Predicate<VolumeAtPrice>() {
            @Override
            public boolean apply(final VolumeAtPrice input) {
                return input.getSurplus() > 0;
            }
        };

        public static Predicate<? super VolumeAtPrice> SELLING_PRESSURE = new Predicate<VolumeAtPrice>() {
            @Override
            public boolean apply(final VolumeAtPrice input) {
                return input.getSurplus() < 0;
            }
        };

        public static Predicate<? super VolumeAtPrice> NO_PRESSURE = new Predicate<VolumeAtPrice>() {
            @Override
            public boolean apply(final VolumeAtPrice input) {
                return input.getSurplus() == 0;
            }
        };
    }

    private Optional<Double> ascertainWhereMarketPressureExists(List<VolumeAtPrice> minimumSurplus) {

        if (!minimumSurplus.isEmpty()) {
            final FluentIterable<VolumeAtPrice> minimumSurplusIterable = FluentIterable.from(minimumSurplus);

            final boolean buyingPressure = minimumSurplusIterable.allMatch(VolumeAtPrice.BUYING_PRESSURE);
            if (buyingPressure) {
                return Optional.of(minimumSurplusIterable.last().get().price);
            }

            final boolean sellingPressure = minimumSurplusIterable.allMatch(VolumeAtPrice.SELLING_PRESSURE);
            if (sellingPressure) {
                return Optional.of(minimumSurplusIterable.first().get().price);
            }
        }

        return Optional.absent();
    }

    private List<Integer> getCumulativeQuantity(
            final List<Double> eligiblePrices,
            final OrderBook book,
            final Order.OrderSide side) {

        final List<Integer> quantities = new ArrayList<Integer>(eligiblePrices.size());

        final Iterable<Double> priceIterable = side == Order.OrderSide.Sell ?
                eligiblePrices :
                Lists.reverse(eligiblePrices);
        final ListIterator<Order> current = book.getOrders().listIterator();
        int cumulative = 0;

        for (final Double price : priceIterable) {
            while (current.hasNext()) {
                final Order order = current.next();
                final OrderTypeLimit limit = order.getOrderTypeLimit();

                if (limit.canTrade(price, side)) {
                    cumulative += order.getQuantity();
                } else {
                    current.previous();
                    break;
                }
            }
            quantities.add(cumulative);
        }

        return side == Order.OrderSide.Sell ? quantities : Lists.reverse(quantities);
    }

    private List<VolumeAtPrice> getTotalTradeableVolume(
            final List<Double> eligiblePrices,
            final List<Integer> cumulativeBuy,
            final List<Integer> cumulativeSell) {

        final List<VolumeAtPrice> tradeableVolume = new ArrayList<VolumeAtPrice>();
        final Iterator<Double> priceIterator = eligiblePrices.iterator();
        final Iterator<Integer> buy = cumulativeBuy.iterator();
        final Iterator<Integer> sell = cumulativeSell.iterator();

        while (priceIterator.hasNext()) {
            final double price = priceIterator.next();
            final int buyVolume = buy.next();
            final int sellVolume = sell.next();
            tradeableVolume.add(new VolumeAtPrice(price, buyVolume, sellVolume));
        }

        return tradeableVolume;
    }

    private List<Double> getListOfEligiblePrices() {

        final TreeSet<Double> prices = new TreeSet<Double>();
        if (referencePrice != null) {
            prices.add(referencePrice);
        }
        prices.addAll(getLimitPrices(buyOrderBook));
        prices.addAll(getLimitPrices(sellOrderBook));

        return new ArrayList<Double>(prices);
    }

    private Collection<? extends Double> getLimitPrices(final OrderBook book) {

        return FluentIterable.from(book.getOrders()).filter(new Predicate<Order>() {
            @Override
            public boolean apply(Order input) {
                return input.getOrderTypeLimit().hasLimit();
            }
        }).transform(new Function<Order, Double>() {
            @Override
            public Double apply(Order input) {
                return input.getOrderTypeLimit().getLimit();
            }
        }).toImmutableSet();
    }

    public void auction() {
        tradingPhase = TradingPhase.CoreAuction;
        indicativeMatchingPrice = getIndicativeMatchingPrice();

        while (!buyOrderBook.getOrders().isEmpty()) {
            if (!tryMatchOrder(buyOrderBook.getOrders().get(0))) {
                break;
            }
        }

        upgradeMarketToLimitOrders(buyOrderBook);
        upgradeMarketToLimitOrders(sellOrderBook);
    }

    private void upgradeMarketToLimitOrders(OrderBook orderBook) {

        if (tradingMode == TradingMode.Continuous) {

            List<Order> orders = orderBook.getOrders();
            List<Order> mtlOrders = FluentIterable.from(orders).filter(new Predicate<Order>() {
                @Override
                public boolean apply(Order input) {
                    return input.getOrderTypeLimit().getOrderType() == OrderType.MarketToLimit;
                }
            }).toImmutableList();
            orders.removeAll(mtlOrders);

            for (Order mtlOrder : mtlOrders) {
                mtlOrder.getOrderTypeLimit().convertToLimit(indicativeMatchingPrice);
                orderBook.add(mtlOrder);
            }
        }
    }

    public List<Order> getOrders(final Order.OrderSide side) {
        return getBook(side).getOrders();
    }

    public void addOrder(final Order.OrderSide side, final String broker, final int quantity, final OrderTypeLimit orderTypeLimit) {
        final Order order = new Order(broker, quantity, orderTypeLimit, side);
        getBook(side).add(order);

        if (tradingPhase == TradingPhase.CoreContinuous) {
            tryMatchOrder(order);
        }
    }

    private boolean tryMatchOrder(final Order order) {
        final Order.OrderSide side = order.getSide();
        final OrderBook book = getBook(side);
        final int startQuantity = order.getQuantity();
        final OrderBook counterBook = getCounterBook(side);

        final OrderType initialOrderType = order.getOrderTypeLimit().getOrderType();
        if (!counterBook.match(order, tradingPhase, indicativeMatchingPrice)) {
            book.remove(order);
        } else {
            final OrderType currentOrderType = order.getOrderTypeLimit().getOrderType();
            if (initialOrderType != currentOrderType) {
                book.remove(order);
                book.add(order);
            }
        }

        return startQuantity != order.getQuantity();
    }

    public int orderBookDepth(final Order.OrderSide side) {
        final OrderBook orders = getBook(side);
        return orders.orderBookDepth();
    }

    public Double getBestLimit(final Order.OrderSide side) {
        return side != Order.OrderSide.Buy ? sellOrderBook.getBestLimit() : buyOrderBook.getBestLimit();
    }

    public void dump() {
        System.out.println();
        System.out.println("Buy Book:");
        buyOrderBook.dump();
        System.out.println("Sell Book:");
        sellOrderBook.dump();
        System.out.println();
    }

    @Nonnull
    public Closeable register(@Nonnull Observer<? super Trade> observer) {
        return notifier.register(observer);
    }

    private OrderBook getBook(final Order.OrderSide side) {
        return side != Order.OrderSide.Buy ? sellOrderBook : buyOrderBook;
    }

    private OrderBook getCounterBook(final Order.OrderSide side) {
        return side != Order.OrderSide.Buy ? buyOrderBook : sellOrderBook;
    }
}