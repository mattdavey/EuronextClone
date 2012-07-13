package com.euronextclone;

import com.euronextclone.OrderPrice;

/**
 * Created with IntelliJ IDEA.
 * User: eprystupa
 * Date: 7/12/12
 * Time: 7:47 PM
 */
public enum OrderType {

    Limit {
        @Override
        public String format(double price, double limit) {
            return Double.toString(price).replace('.', ',');
        }

        @Override
        public boolean markToBestLimit(OrderPrice orderPrice, OrderPrice bestLimit) {
            return false;
        }

        @Override
        public boolean canBeTopOfBook() {
            return true;
        }
    },

    MarketOrder {
        @Override
        public String format(double price, double limit) {
            return "MO";
        }

        @Override
        public boolean markToBestLimit(OrderPrice orderPrice, OrderPrice bestLimit) {
            return false;
        }

        @Override
        public boolean canBeTopOfBook() {
            return true;
        }
    },

    MarketToLimit {
        @Override
        public String format(double price, double limit) {
            return "MTL";
        }

        @Override
        public boolean markToBestLimit(OrderPrice orderPrice, OrderPrice bestLimit) {
            return false;
        }

        @Override
        public boolean canBeTopOfBook() {
            return true;
        }
    },

    Peg {
        @Override
        public String format(double price, double limit) {
            return String.format("PEG (%s)", Double.toString(price).replace('.', ','));
        }

        @Override
        public boolean markToBestLimit(OrderPrice orderPrice, OrderPrice bestLimit) {
            orderPrice.update(bestLimit.value());
            return true;
        }

        @Override
        public boolean canBeTopOfBook() {
            return false;
        }
    },

    PegWithLimit {
        @Override
        public String format(double price, double limit) {
            return String.format("PEG (%s)[%s]", Double.toString(price).replace('.', ','), Double.toString(limit).replace('.', ','));
        }

        @Override
        public boolean markToBestLimit(OrderPrice orderPrice, OrderPrice bestLimit) {
            double newVal = bestLimit.value() <= orderPrice.getLimit() ? bestLimit.value() : orderPrice.getLimit();
            orderPrice.update(newVal);
            return true;
        }

        @Override
        public boolean canBeTopOfBook() {
            return false;
        }
    };

    public abstract String format(double price, double limit);

    public abstract boolean markToBestLimit(OrderPrice orderPrice, OrderPrice bestLimit);

    public abstract boolean canBeTopOfBook();
}
