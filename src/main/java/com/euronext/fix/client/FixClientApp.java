package com.euronext.fix.client;

import com.lmax.disruptor.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.ConfigError;
import quickfix.SessionNotFound;
import quickfix.SessionSettings;
import quickfix.field.OrdType;
import quickfix.field.Side;
import quickfix.fix42.ExecutionReport;

import java.io.InputStream;
import java.util.Scanner;

/**
 * Created with IntelliJ IDEA.
 * User: eprystupa
 * Date: 7/24/12
 * Time: 8:24 PM
 */
public class FixClientApp {
    private static Logger logger = LoggerFactory.getLogger(FixClientApp.class);

    public static void main(String args[]) {

        try {
            final InputStream config = FixClientApp.class.getClassLoader().getResourceAsStream("FixBrokerA.cfg");
            final FixClient client = new FixClient(new SessionSettings(config));
            client.start();

            final OrderBuilder orderBuilder = new OrderBuilder()
                    .withSymbol("MSFT")
                    .withOrderType(OrdType.LIMIT)
                    .withQuantity(1000);

            client.handleExecutions(new EventHandler<ExecutionReport>() {

                @Override
                public void onEvent(ExecutionReport report, long sequence, boolean endOfBatch) throws Exception {
                    String side = report.getSide().getValue() == Side.BUY ? "Bought" : "Sold";
                    double tradeQty = report.getLastShares().getValue();
                    String symbol = report.getSymbol().getValue();
                    double tradePrice = report.getLastPx().getValue();
                    logger.debug("Received execution report: {} {} shares of {} at {}", new Object[]{side, tradeQty, symbol, tradePrice});

                }
            });

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
