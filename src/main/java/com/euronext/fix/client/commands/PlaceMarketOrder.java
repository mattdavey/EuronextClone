package com.euronext.fix.client.commands;

import com.euronext.fix.client.FixClient;
import com.euronext.fix.client.OrderBuilder;
import quickfix.SessionNotFound;
import quickfix.field.OrdType;
import quickfix.fix42.NewOrderSingle;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: eprystupa
 * Date: 8/1/12
 * Time: 10:22 PM
 */
public class PlaceMarketOrder implements ClientCommand {

    private static final Pattern INSTRUCTION = Pattern.compile("^(Buy|Sell) (\\S+) (\\d+)$", Pattern.CASE_INSENSITIVE);

    @Override
    public String name() {
        return "Place market order";
    }

    @Override
    public Pattern pattern() {
        return INSTRUCTION;
    }

    @Override
    public void execute(FixClient client, Matcher input) throws SessionNotFound {
        if (!input.matches()) {
            throw new RuntimeException("Bad input for place market order command");
        }
        final String side = input.group(1);
        final String symbol = input.group(2);
        final String quantity = input.group(3);

        final OrderBuilder orderBuilder = new OrderBuilder()
                .withSymbol(symbol)
                .withOrderType(OrdType.MARKET)
                .withQuantity(Double.parseDouble(quantity));
        final NewOrderSingle order = side.compareToIgnoreCase("Buy") == 0 ? orderBuilder.buy() : orderBuilder.sell();
        client.submitOrder(order);
    }
}
