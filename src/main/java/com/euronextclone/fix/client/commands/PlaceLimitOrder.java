package com.euronextclone.fix.client.commands;

import com.euronextclone.fix.client.FixClient;
import com.euronextclone.fix.client.OrderBuilder;
import quickfix.SessionNotFound;
import quickfix.field.OrdType;
import quickfix.fix42.NewOrderSingle;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: eprystupa
 * Date: 8/1/12
 * Time: 7:38 PM
 */
public class PlaceLimitOrder implements ClientCommand {

    private static final Pattern INSTRUCTION = Pattern.compile("^(Buy|Sell) (\\S+) (\\d+)@(\\d+)$", Pattern.CASE_INSENSITIVE);

    @Override
    public String name() {
        return "Place limit order";
    }

    @Override
    public Pattern pattern() {
        return INSTRUCTION;
    }

    @Override
    public void execute(final FixClient client, final Matcher input) throws SessionNotFound {

        if (!input.matches()) {
            throw new RuntimeException("Bad input for place limit order command");
        }
        final String side = input.group(1);
        final String symbol = input.group(2);
        final String quantity = input.group(3);
        final String price = input.group(4);

        final OrderBuilder orderBuilder = new OrderBuilder()
                .withSymbol(symbol)
                .withOrderType(OrdType.LIMIT)
                .withQuantity(Double.parseDouble(quantity))
                .at(Double.parseDouble(price));
        final NewOrderSingle order = side.compareToIgnoreCase("Buy") == 0 ? orderBuilder.buy() : orderBuilder.sell();
        client.submitOrder(order);
    }
}
