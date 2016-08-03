package com.qoomon.banking.swift.notation;

/**
 * Created by qoomon on 03/08/16.
 */
public class SwiftNotationParseException extends RuntimeException {

    public SwiftNotationParseException(String message) {
        super(message);
    }

    public SwiftNotationParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
