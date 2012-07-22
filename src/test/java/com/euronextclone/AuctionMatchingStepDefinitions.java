package com.euronextclone;

public class AuctionMatchingStepDefinitions {

    private final MatchingUnit matchingUnit;

    public AuctionMatchingStepDefinitions(World world) {
        this.matchingUnit = world.getMatchingUnit();
    }
}