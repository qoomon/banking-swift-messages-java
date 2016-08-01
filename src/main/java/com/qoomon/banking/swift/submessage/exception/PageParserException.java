package com.qoomon.banking.swift.submessage.exception;

/**
 * Created by qoomon on 27/06/16.
 */
public class PageParserException extends RuntimeException {

    private final int lineNumber;

    public PageParserException(String message, int lineNumber) {
        super(message + " at line number " + lineNumber);
        this.lineNumber = lineNumber;
    }

    public PageParserException(String message, int lineNumber, Throwable cause) {
        super(message + " at line number " + lineNumber, cause);
        this.lineNumber = lineNumber;
    }

    public PageParserException(Throwable cause) {
        super(cause);
        this.lineNumber = 0;
    }

    public int getLineNumber() {
        return lineNumber;
    }
}
