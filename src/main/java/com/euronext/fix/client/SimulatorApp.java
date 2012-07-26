package com.euronext.fix.client;

import quickfix.ConfigError;
import quickfix.SessionNotFound;
import quickfix.SessionSettings;
import quickfix.field.OrdType;

import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: eprystupa
 * Date: 7/24/12
 * Time: 8:24 PM
 */
public class SimulatorApp {
    public static void main(String args[]) {


        try {
            final InputStream config = SimulatorApp.class.getClassLoader().getResourceAsStream("FixClient.cfg");
            final FixClient client = new FixClient(new SessionSettings(config));
            client.start();

            final OrderBuilder orderBuilder = new OrderBuilder()
                    .withSymbol("MSFT")
                    .withOrderType(OrdType.LIMIT)
                    .withQuantity(1000);

            client.submitOrder(orderBuilder.buy());
            client.submitOrder(orderBuilder.sell());

            client.stop();

        } catch (ConfigError configError) {
            configError.printStackTrace();
        } catch (SessionNotFound sessionNotFound) {
            sessionNotFound.printStackTrace();
        }
    }
}
