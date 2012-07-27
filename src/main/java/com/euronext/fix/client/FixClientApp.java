package com.euronext.fix.client;

import quickfix.ConfigError;
import quickfix.SessionNotFound;
import quickfix.SessionSettings;
import quickfix.field.OrdType;

import java.io.InputStream;
import java.util.Scanner;

/**
 * Created with IntelliJ IDEA.
 * User: eprystupa
 * Date: 7/24/12
 * Time: 8:24 PM
 */
public class FixClientApp {
    public static void main(String args[]) {


        try {
            final InputStream config = FixClientApp.class.getClassLoader().getResourceAsStream("FixBrokerA.cfg");
            final FixClient client = new FixClient(new SessionSettings(config));
            client.start();

            final OrderBuilder orderBuilder = new OrderBuilder()
                    .withSymbol("MSFT")
                    .withOrderType(OrdType.LIMIT)
                    .withQuantity(1000);

            client.submitOrder(orderBuilder.buy());
            client.submitOrder(orderBuilder.sell());

            Scanner scanner = new Scanner(System.in);
            System.out.println("[Enter] q to exit");
            while (!scanner.nextLine().trim().equals("q")) {
                System.out.println("[Enter] q to exit");
            }

            System.out.println("Exiting...");

            client.stop();

        } catch (ConfigError configError) {
            configError.printStackTrace();
        } catch (SessionNotFound sessionNotFound) {
            sessionNotFound.printStackTrace();
        }
    }
}
