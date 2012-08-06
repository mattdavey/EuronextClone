package com.euronextclone.fix.server;

import com.euronextclone.messaging.Publisher;
import quickfix.ConfigError;
import quickfix.SessionSettings;

import java.io.InputStream;
import java.util.Scanner;

/**
 * Created with IntelliJ IDEA.
 * User: eprystupa
 * Date: 7/26/12
 * Time: 9:55 PM
 */
public class FixServerApp {

    public static void main(String[] args) throws ConfigError {

        final InputStream config = FixServerApp.class.getClassLoader().getResourceAsStream("FixServer.cfg");

        FixServer fixServer = new FixServer(
                new SessionSettings(config),
                new Publisher("tcp://*:5555"));
        fixServer.start();

        Scanner scanner = new Scanner(System.in);
        System.out.println("[Enter] q to exit");
        while (!scanner.nextLine().trim().equals("q")) System.out.println("[Enter] q to exit");

        System.out.println("Exiting...");
        fixServer.stop();
    }
}
