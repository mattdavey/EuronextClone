package com.euronextclone;

public enum OrderType {

    Limit {
        @Override
        public String format(double limit) {
            return Double.toString(limit).replace('.', ',');
        }
    },

    MarketOrder {
        @Override
        public String format(double limit) {
            return "MO";
        }
    },

    MarketToLimit {
        @Override
        public String format(double limit) {
            return "MTL";
        }
    },

    Peg {
        @Override
        public String format(double limit) {
            if (limit != Double.MAX_VALUE)
                return String.format("PEG [%s]", Double.toString(limit).replace('.', ','));
            else
                return "PEG";
        }
    };

    public abstract String format(double limit);
}
