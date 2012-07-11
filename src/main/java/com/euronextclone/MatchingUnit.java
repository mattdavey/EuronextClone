package com.euronextclone;

import hu.akarnokd.reactive4java.base.Action1;
import hu.akarnokd.reactive4java.reactive.DefaultObservable;
import hu.akarnokd.reactive4java.reactive.Observable;
import hu.akarnokd.reactive4java.reactive.Observer;
import hu.akarnokd.reactive4java.reactive.Reactive;

import java.io.Closeable;

public class MatchingUnit implements Observable<Trade>
{
    public MatchingUnit()
    {
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

    public void addOrder(final Order.OrderSide side, final String broker, final int quantity, final OrderPrice price) {
        final OrderBook book = getBook(side);
        book.addOrder(new Order(broker, quantity, price, side));
    }

    public void match() {
        // TODO
    }

    public void newOrder(final Order.OrderSide side, final String broker, final int quantity, final OrderPrice price)
    {
        matchOrder(side, broker, quantity, price);
    }

    public int orderBookDepth(final Order.OrderSide side)
    {
        final OrderBook orders = getBook(side);
        return orders.orderBookDepth();
    }

    public String getBestLimit(final Order.OrderSide side)
    {
        return side != Order.OrderSide.Buy ? sellOrderBook.getBestLimit().toString() : buyOrderBook.getBestLimit().toString();
    }

    public void dump()
    {
        System.out.println();
        System.out.println("Buy Book:");
        buyOrderBook.dump();
        System.out.println("Sell Book:");
        sellOrderBook.dump();
        System.out.println();
    }

    private final OrderBook buyOrderBook;
    private final OrderBook sellOrderBook;

    /** The observable helper. */
    DefaultObservable<Trade> notifier = new DefaultObservable<Trade>();

    public Closeable register(Observer<? super Trade> observer) {
        return notifier.register(observer);
    }

    private void matchOrder(final Order.OrderSide side, final String broker, final int quantity, final OrderPrice price)
    {
        final OrderBook matchOrderBook = side != Order.OrderSide.Buy ? buyOrderBook : sellOrderBook;
        final Order order = new Order(broker, quantity, price, side);
        if(matchOrderBook.match(order))
        {
            final OrderBook orderBook = getBook(side);
            orderBook.addOrder(order);
        }
    }

    private OrderBook getBook(Order.OrderSide side) {
        return side != Order.OrderSide.Buy ? sellOrderBook : buyOrderBook;
    }
}
