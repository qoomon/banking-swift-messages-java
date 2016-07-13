package com.qoomon.banking.swift.message.submessage.exception;

/**
 * Created by qoomon on 27/06/16.
 */
public class SubMessageParserException extends RuntimeException {

    private final int lineNumber;

    public SubMessageParserException(String message, int lineNumber) {
        super(message + " at line number " + lineNumber);
        this.lineNumber = lineNumber;
    }

    public SubMessageParserException(String message, int lineNumber, Throwable cause) {
        super(message + " at line number " + lineNumber , cause);
        this.lineNumber = lineNumber;
    }

    public SubMessageParserException(Throwable cause) {
        super(cause);
        this.lineNumber = 0;
    }

    public int getLineNumber() {
        return lineNumber;
    }
}
