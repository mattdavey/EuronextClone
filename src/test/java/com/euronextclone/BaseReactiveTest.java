package com.euronextclone;

public abstract class BaseReactiveTest {
    private int receivedTradeCount = 0;

    public int getReceivedTradeCount() {
        return receivedTradeCount;
    }

    public int incTradeCount() {
        return receivedTradeCount++;
    }
}
