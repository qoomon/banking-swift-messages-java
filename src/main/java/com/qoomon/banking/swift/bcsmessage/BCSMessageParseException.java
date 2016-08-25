package com.qoomon.banking.swift.bcsmessage;

/**
 * Created by qoomon on 03/08/16.
 */
public class BCSMessageParseException extends Exception {

    public BCSMessageParseException(String message) {
        super(message);
    }

    public BCSMessageParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public BCSMessageParseException(Throwable cause) {
        super(cause);
    }
}
