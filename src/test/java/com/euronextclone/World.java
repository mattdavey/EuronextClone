package com.euronextclone;

import cucumber.annotation.Before;
import hu.akarnokd.reactive4java.reactive.Observer;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: eprystupa
 * Date: 7/12/12
 * Time: 10:53 PM
 */
public class World {

    private final List<Trade> generatedTrades = new ArrayList<Trade>();
    private final MatchingUnit matchingUnit = new MatchingUnit();

    public List<Trade> getGeneratedTrades() {
        return generatedTrades;
    }

    public MatchingUnit getMatchingUnit() {
        return matchingUnit;
    }

    @Before
    public void setUp() {
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
}
