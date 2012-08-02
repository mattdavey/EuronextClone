package com.euronext.fix.client;

import com.euronext.fix.client.commands.ClientCommand;
import com.euronext.fix.client.commands.PlaceLimitOrder;
import com.lmax.disruptor.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.ConfigError;
import quickfix.SessionNotFound;
import quickfix.SessionSettings;
import quickfix.field.Side;
import quickfix.fix42.ExecutionReport;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: eprystupa
 * Date: 7/24/12
 * Time: 8:24 PM
 */
public class FixClientApp {
    private static Logger logger = LoggerFactory.getLogger(FixClientApp.class);
    private static List<ClientCommand> commands = new ArrayList<ClientCommand>();

    public static void main(String args[]) {

        commands.add(new PlaceLimitOrder());

        try {
            final String broker = getBroker(args, "A");
            final FixClient client = createClient(broker);

            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter [q] to exit, or [h] to list commands");
            while (true) {
                System.out.print("Broker " + broker + "> ");
                final String command = scanner.nextLine().trim();
                if (command.equals("q")) {
                    break;
                }

                if (command.equals("h")) {
                    listCommands();
                    continue;
                }

                for (ClientCommand clientCommand : commands) {
                    final Matcher matcher = clientCommand.pattern().matcher(command);
                    if (matcher.matches()) {
                        clientCommand.execute(client, matcher);
                        break;
                    }
                }
            }

            System.out.println("Exiting...");

            client.stop();

        } catch (ConfigError configError) {
            logger.error(configError.getMessage(), configError);
        } catch (SessionNotFound sessionNotFound) {
            logger.error(sessionNotFound.getMessage(), sessionNotFound);
        }
    }

    private static String getBroker(String[] args, String defaultBroker) {
        // TODO: replace with some Java library for command arg parsing
        String result = defaultBroker;
        for (String arg : args) {
            final Matcher matcher = Pattern.compile("--broker=(\\S)").matcher(arg);
            if (matcher.matches()) {
                result = matcher.group(1);
            }
        }
        return result;
    }

    private static void listCommands() {
        for (ClientCommand command : commands) {
            System.out.println(command.name() + ":\t" + command.pattern().pattern());
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
