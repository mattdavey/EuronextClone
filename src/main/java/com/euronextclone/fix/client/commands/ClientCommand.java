package com.euronextclone.fix.client.commands;

import com.euronextclone.fix.client.FixClient;
import quickfix.SessionNotFound;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: eprystupa
 * Date: 8/1/12
 * Time: 7:34 PM
 */
public interface ClientCommand {
    String name();

    Pattern pattern();

    void execute(FixClient client, Matcher input) throws SessionNotFound;
}
