package com.qoomon.banking.swift.message.exception;

/**
 * Created by qoomon on 27/06/16.
 */
public class SwiftMessageParserException extends RuntimeException {

    private final int lineNumber;

    public SwiftMessageParserException(String message, int lineNumber) {
        super(message + " at line " + lineNumber);
        this.lineNumber = lineNumber;
    }

    public SwiftMessageParserException(String message, int lineNumber, Throwable cause) {
        super(message + " at line " + lineNumber, cause);
        this.lineNumber = lineNumber;
    }

    public int getLineNumber() {
        return lineNumber;
    }
}
