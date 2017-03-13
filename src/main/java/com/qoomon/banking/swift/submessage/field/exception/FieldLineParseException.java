package com.qoomon.banking.swift.submessage.field.exception;

/**
 * Created by qoomon on 27/06/16.
 */
public class FieldLineParseException extends Exception {

    private final int lineNumber;

    public FieldLineParseException(String message, int lineNumber, Throwable cause) {
        super(message + " at line number " + lineNumber, cause);
        this.lineNumber = lineNumber;
    }

    public int getLineNumber() {
        return lineNumber;
    }
}
