package com.euronextclone;

import com.euronextclone.ordertypes.Limit;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import hu.akarnokd.reactive4java.reactive.DefaultObservable;
import hu.akarnokd.reactive4java.reactive.Observable;
import hu.akarnokd.reactive4java.reactive.Observer;

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
        buyOrderBook = new OrderBook();
        sellOrderBook = new OrderBook();
    }

    public void setReferencePrice(Double referencePrice) {
        this.referencePrice = referencePrice;
    }

    public Double getIndicativeMatchingPrice() {

        final List<Double> eligiblePrices = getListOfEligiblePrices();
        final List<Integer> cumulativeBuy = getCumulativeQuantity(eligiblePrices, buyOrderBook, OrderSide.Buy);
        final List<Integer> cumulativeSell = getCumulativeQuantity(eligiblePrices, sellOrderBook, OrderSide.Sell);
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
            final OrderSide side) {

        final List<Integer> quantities = new ArrayList<Integer>(eligiblePrices.size());

        final Iterable<Double> priceIterable = side == OrderSide.Sell ?
                eligiblePrices :
                Lists.reverse(eligiblePrices);
        final ListIterator<Order> current = book.getOrders().listIterator();
        int cumulative = 0;

        for (final double price : priceIterable) {
            while (current.hasNext()) {
                final Order order = current.next();
                final OrderType orderType = order.getOrderType();

                Double orderPrice = orderType.price(side, book.getBestLimit());
                if (canTrade(side, orderPrice, price)) {
                    cumulative += order.getQuantity();
                } else {
                    current.previous();
                    break;
                }
            }
            quantities.add(cumulative);
        }

        return side == OrderSide.Sell ? quantities : Lists.reverse(quantities);
    }

    private boolean canTrade(OrderSide orderSide, Double orderPrice, double testPrice) {

        if (orderPrice == null) {
            return true;
        }

        if (orderSide == OrderSide.Buy) {
            return testPrice <= orderPrice;
        }

        return testPrice >= orderPrice;
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
                return input.getOrderType().providesLimit();
            }
        }).transform(new Function<Order, Double>() {
            @Override
            public Double apply(Order input) {
                return input.getOrderType().getLimit();
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
                    return input.getOrderType().convertsToLimit();
                }
            }).toImmutableList();
            orders.removeAll(mtlOrders);

            for (Order mtlOrder : mtlOrders) {
                Order limit = mtlOrder.convertTo(new Limit(indicativeMatchingPrice));
                orderBook.add(limit);
            }
        }
    }

    public List<Order> getOrders(final OrderSide side) {
        return getBook(side).getOrders();
    }

    public void addOrder(final OrderSide side, final String broker, final int quantity, final OrderType orderType) {
        final Order order = new Order(broker, quantity, orderType, side);
        boolean topOfTheBook = getBook(side).add(order) == 0;

        if (tradingPhase == TradingPhase.CoreContinuous && topOfTheBook) {
            tryMatchOrder(order);
        }
    }

    private boolean tryMatchOrder(final Order newOrder) {
        final OrderSide side = newOrder.getSide();
        final OrderBook book = getBook(side);
        final int startQuantity = newOrder.getQuantity();
        final OrderBook counterBook = getCounterBook(side);

        Double newOrderPrice = newOrder.getOrderType().providesLimit() ? newOrder.getOrderType().getLimit() : null;

        List<Order> toRemove = new ArrayList<Order>();
        List<Order> toAdd = new ArrayList<Order>();

        Order currentOrder = newOrder;

        for (final Order order : counterBook.getOrders()) {

            // Determine the price at which the trade happens
            final Double bookOrderPrice = order.getOrderType().price(order.getSide(), counterBook.getBestLimit());

            Double tradePrice = determineTradePrice(newOrderPrice, bookOrderPrice, order.getSide(), counterBook.getBestLimit());
            if (tradePrice == null) {
                break;
            }

            if (tradingPhase == TradingPhase.CoreAuction) {
                tradePrice = indicativeMatchingPrice;
            }

            // Determine the amount to trade
            int tradeQuantity = determineTradeQuantity(currentOrder, order);

            // Trade
            currentOrder.decrementQuantity(tradeQuantity);
            order.decrementQuantity(tradeQuantity);
            generateTrade(currentOrder, order, tradeQuantity, tradePrice);

            if (order.getQuantity() == 0) {
                toRemove.add(order);
            } else {
                if (order.getOrderType().convertsToLimit()) {
                    toRemove.add(order);
                    toAdd.add(order.convertTo(new Limit(tradePrice)));
                }
                break;
            }

            if (currentOrder.getQuantity() == 0) {
                break;
            }

            if (currentOrder.getOrderType().convertsToLimit()) {
                book.remove(currentOrder);
                currentOrder = currentOrder.convertTo(new Limit(tradePrice));
                book.add(currentOrder);
                newOrderPrice = tradePrice;
            }
        }

        counterBook.removeAll(toRemove);
        for (Order order : toAdd) {
            counterBook.add(order);
        }

        if (currentOrder.getQuantity() == 0) {
            book.remove(currentOrder);
        }

        return startQuantity != currentOrder.getQuantity();
    }

    private void generateTrade(final Order newOrder, final Order order, final int tradeQuantity, final double price) {
        notifier.next(new Trade(newOrder.getSide() == OrderSide.Buy ? newOrder.getBroker() : order.getBroker(),
                newOrder.getSide() == OrderSide.Sell ? newOrder.getBroker() : order.getBroker(),
                tradeQuantity, price));
    }

    private int determineTradeQuantity(Order newOrder, Order order) {
        return Math.min(newOrder.getQuantity(), order.getQuantity());
    }

    private Double determineTradePrice(Double newOrderPrice, Double counterBookOrderPrice, OrderSide counterBookSide, Double counterBookBestLimit) {

        if (newOrderPrice == null && counterBookOrderPrice == null) {
            if (counterBookBestLimit != null) {
                return counterBookBestLimit;
            }

            return referencePrice;
        }

        if (newOrderPrice == null) {
            return counterBookOrderPrice;
        }

        if (counterBookOrderPrice == null) {
            return tryImprovePrice(newOrderPrice, counterBookSide, counterBookBestLimit);
        }

        if (counterBookSide == OrderSide.Buy && counterBookOrderPrice >= newOrderPrice) {
            return counterBookOrderPrice;
        }

        if (counterBookSide == OrderSide.Sell && counterBookOrderPrice <= newOrderPrice) {
            return counterBookOrderPrice;
        }

        return null;  // Can't trade
    }

    private double tryImprovePrice(double price, OrderSide counterBookSide, Double counterBookBestLimit) {
        if (counterBookBestLimit == null) {
            return price;   // can't improve, not best limit in counter book
        }

        if (counterBookSide == OrderSide.Buy && counterBookBestLimit > price) {
            return counterBookBestLimit;
        }
        if (counterBookSide == OrderSide.Sell && counterBookBestLimit < price) {
            return counterBookBestLimit;
        }

        return price;   // can't improve
    }

    public Double getBestLimit(final OrderSide side) {
        return side != OrderSide.Buy ? sellOrderBook.getBestLimit() : buyOrderBook.getBestLimit();
    }

    @Nonnull
    public Closeable register(@Nonnull Observer<? super Trade> observer) {
        return notifier.register(observer);
    }

    private OrderBook getBook(final OrderSide side) {
        return side != OrderSide.Buy ? sellOrderBook : buyOrderBook;
    }

    private OrderBook getCounterBook(final OrderSide side) {
        return side != OrderSide.Buy ? buyOrderBook : sellOrderBook;
    }
}