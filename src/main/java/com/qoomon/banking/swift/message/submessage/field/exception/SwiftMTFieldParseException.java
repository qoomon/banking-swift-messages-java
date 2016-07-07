package com.qoomon.banking.swift.message.submessage.field.exception;

/**
 * Created by qoomon on 27/06/16.
 */
public class SwiftMTFieldParseException extends Exception {

    private final int lineNumber;

    public SwiftMTFieldParseException(String message, int lineNumber) {
        super(message + " at line " + lineNumber);
        this.lineNumber = lineNumber;
    }

    public SwiftMTFieldParseException(String message, int lineNumber, Throwable cause) {
        super(message + " at line " + lineNumber, cause);
        this.lineNumber = lineNumber;
    }

    public SwiftMTFieldParseException(Throwable cause) {
        super(cause);
        lineNumber = 0;
    }

    public int getLineNumber() {
        return lineNumber;
    }
}
