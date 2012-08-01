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
            final FixClient clientA = createClient("A");
            final FixClient clientB = createClient("B");

            final OrderBuilder orderBuilder = new OrderBuilder()
                    .withSymbol("MSFT")
                    .withOrderType(OrdType.LIMIT)
                    .withQuantity(1000);

            clientA.submitOrder(orderBuilder.buy());
            clientB.submitOrder(orderBuilder.sell());

            Scanner scanner = new Scanner(System.in);
            System.out.println("[Enter] q to exit");
            while (!scanner.nextLine().trim().equals("q")) {
                System.out.println("[Enter] q to exit");
            }

            System.out.println("Exiting...");

            clientA.stop();
            clientB.stop();

        } catch (ConfigError configError) {
            logger.error(configError.getMessage(), configError);
        } catch (SessionNotFound sessionNotFound) {
            logger.error(sessionNotFound.getMessage(), sessionNotFound);
        }
    }

    private static FixClient createClient(final String broker) throws ConfigError {
        final InputStream config = FixClientApp.class.getClassLoader().getResourceAsStream("FixBroker" + broker + ".cfg");
        final FixClient client = new FixClient(new SessionSettings(config));
        client.start();

        client.handleExecutions(new EventHandler<ExecutionReport>() {

            @Override
            public void onEvent(ExecutionReport report, long sequence, boolean endOfBatch) throws Exception {
                String side = report.getSide().getValue() == Side.BUY ? "bought" : "sold";
                double tradeQty = report.getLastShares().getValue();
                String symbol = report.getSymbol().getValue();
                double tradePrice = report.getLastPx().getValue();
                logger.debug("Broker {} {} {} shares of {} at {}", new Object[]{broker, side, tradeQty, symbol, tradePrice});
            }
        });
        return client;
    }
}
