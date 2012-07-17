package com.euronextclone;

import java.util.ArrayList;
import java.util.List;

public class IndicativeMatchPriceCalculator
{
    private final Double referencePrice;
    private final OrderBook sellBook;
    private final OrderBook buyBook;

    public IndicativeMatchPriceCalculator(final Double referencePrice,
                                          final OrderBook sellBook,
                                          final OrderBook buyBook)
    {
        this.referencePrice = referencePrice;
        this.sellBook = sellBook;
        this.buyBook = buyBook;
    }

    public final Double get()
    {
        List<CalculationInfo> calculations = getMaximumExecutableVolume();
        if (calculations == null)
        {
            return null;
        }
        if (calculations.size() == 1)
        {
            return calculations.get(0).getPrice();
        }

        calculations = getMinimumSurplus(calculations);
        if (calculations == null)
        {
            return null;
        }
        if (calculations.size() == 1)
        {
            return calculations.get(0).getPrice();
        }

        calculations = getMarketPressure(calculations);
        if (calculations == null)
        {
            return null;
        }
        if (calculations.size() == 1)
        {
            return calculations.get(0).getPrice();
        }

        return getReferencePrice(calculations);
    }


    private List<CalculationInfo> getMaximumExecutableVolume()
    {
        return new ArrayList<CalculationInfo>();
    }

    private List<CalculationInfo> getMinimumSurplus(List<CalculationInfo> calculations)
    {
        int minSurplus = Integer.MAX_VALUE;
        final List<CalculationInfo> filtered = new ArrayList<CalculationInfo>();
        for (final CalculationInfo ci : calculations)
        {
            final int localMinSurplus = Math.abs(ci.getMinimumSurplus());
            if (localMinSurplus < minSurplus)
            {
                minSurplus = localMinSurplus;
                filtered.clear();
                filtered.add(ci);
            }
            else if (localMinSurplus == minSurplus)
            {
                filtered.add(ci);
            }
        }
        return filtered;
    }

    private List<CalculationInfo> getMarketPressure(List<CalculationInfo> calculations)
    {
        CalculationInfo buyCalculationInfo = null;
        CalculationInfo sellCalculationInfo = null;

        for (final CalculationInfo ci : calculations)
        {
            final int surplus = ci.getMinimumSurplus();
            if (surplus > 0)
            {
                if (buyCalculationInfo == null || (buyCalculationInfo.getPrice() < ci.getPrice()))
                {
                    buyCalculationInfo = ci;
                }
            }
            else if (surplus < 0)
            {
                if (buyCalculationInfo == null || (sellCalculationInfo.getPrice() > ci.getPrice()))
                {
                    sellCalculationInfo = ci;
                }
            }
            //return earlier if we have market pressure on both sides (buy/sell)
            if (buyCalculationInfo != null && sellCalculationInfo != null)
            {
                return calculations;
            }
        }

        if (buyCalculationInfo != null)
        {
            final ArrayList<CalculationInfo> c = new ArrayList<CalculationInfo>();
            c.add(buyCalculationInfo);
            return c;
        }

        if (sellCalculationInfo != null)
        {
            final ArrayList<CalculationInfo> c = new ArrayList<CalculationInfo>();
            c.add(sellCalculationInfo);
            return c;
        }
        return calculations;
    }

    private double getReferencePrice(List<CalculationInfo> calculations)
    {
        //get two prices
        CalculationInfo[] twoPrices = getTwoPrices(calculations);


        double max = Math.max(twoPrices[0].getPrice(), twoPrices[1].getPrice());
        double min = Math.min(twoPrices[0].getPrice(), twoPrices[1].getPrice());

        if (referencePrice == null)
        {
            return min;
        }

        if (referencePrice >= max)
        {
            return max;
        }
        if (referencePrice <= min)
        {
            return min;
        }
        return referencePrice;
    }

    private CalculationInfo[] getTwoPrices(final List<CalculationInfo> calculations)
    {
        assert (calculations.size() > 1);
        //check all prices have minimum surplus zero
        boolean allZero = true;
        for (final CalculationInfo ci : calculations)
        {
            allZero = ci.getMinimumSurplus() == 0;
            if (!allZero)
            {
                break;
            }
        }
        if (allZero)
        {
            return new CalculationInfo[]{calculations.get(0),
                                         calculations.get(calculations.size() - 1)};
        }

        CalculationInfo first = calculations.get(0);
        CalculationInfo second = null;
        for (int i = 1; i < calculations.size(); ++i)
        {
            final CalculationInfo temp = calculations.get(i);
            //the items are orderd and all the items minimum surplus are the same value
            if (first.getMinimumSurplus() != temp.getMinimumSurplus())
            {
                second = temp;
                break;
            }
            first = temp;
        }
        return new CalculationInfo[]{first, second};
    }

    private final class CalculationInfo
    {

        private final double price;
        private final int cumulativeBuyQuantity;
        private final int cumulativeSellQuantity;
        private final int maxExecutableVolume;

        private CalculationInfo(final double price,
                                final int cumulativeBuyQuantity,
                                final int cumulativeSellQuantity,
                                final int maxExecutableVolume)
        {
            this.price = price;
            this.cumulativeBuyQuantity = cumulativeBuyQuantity;
            this.cumulativeSellQuantity = cumulativeSellQuantity;
            this.maxExecutableVolume = maxExecutableVolume;
        }


        public double getPrice()
        {
            return price;
        }

        public int getCumulativeBuyQuantity()
        {
            return cumulativeBuyQuantity;
        }

        public int getCumulativeSellQuantity()
        {
            return cumulativeSellQuantity;
        }

        public int getMaxExecutableVolume()
        {
            return maxExecutableVolume;
        }

        public int getMinimumSurplus()
        {
            return cumulativeBuyQuantity - cumulativeSellQuantity;
        }
    }
}
