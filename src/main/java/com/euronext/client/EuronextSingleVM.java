package com.euronext.client;

import com.euronextclone.gateway.FixGateway;
import quickfix.SessionSettings;

import java.io.InputStream;

public class EuronextSingleVM {
    public static void main(String args[]) throws Exception {
        final InputStream inputStream = FixGateway.getSettingsInputStream(args);
        final SessionSettings settings = new SessionSettings(inputStream);
        inputStream.close();

        final FixGateway executor = new FixGateway(settings);
        executor.start();

        final FixClient client = new FixClient();
        client.run(args);

        System.out.println("press <enter> to quit");
        System.in.read();

        executor.stop();
    }
}
