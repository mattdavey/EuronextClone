package com.euronext.client.simulator;

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
            final InputStream config = SimulatorApp.class.getClassLoader().getResourceAsStream("com/euronext/client/FixClient.cfg");
            final FixGateway client = new FixGateway(new SessionSettings(config));
            client.start();

            final OrderBuilder orderBuilder = new OrderBuilder()
                    .withSymbol("MSFT")
                    .withOrderType(OrdType.LIMIT);

            client.submitOrder(orderBuilder.buy());

            client.stop();

        } catch (ConfigError configError) {
            configError.printStackTrace();
        } catch (SessionNotFound sessionNotFound) {
            sessionNotFound.printStackTrace();
        }
    }
}
