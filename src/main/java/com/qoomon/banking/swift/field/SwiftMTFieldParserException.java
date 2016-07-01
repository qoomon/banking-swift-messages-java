package com.qoomon.banking.swift.field;

/**
 * Created by qoomon on 27/06/16.
 */
public class SwiftMTFieldParserException extends RuntimeException {

    private final int lineNumber;

    public SwiftMTFieldParserException(String message, int lineNumber) {
        super(message + " at line " + lineNumber);
        this.lineNumber = lineNumber;
    }

    public SwiftMTFieldParserException(String message, int lineNumber, Throwable cause) {
        super(message + " at line " + lineNumber, cause);
        this.lineNumber = lineNumber;
    }

    public SwiftMTFieldParserException(Throwable cause) {
        super(cause);
        lineNumber = 0;
    }

    public int getLineNumber() {
        return lineNumber;
    }
}
