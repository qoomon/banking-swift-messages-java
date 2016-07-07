package com.qoomon.banking.swift.message.block.exception;

/**
 * Created by qoomon on 07/07/16.
 */
public class BlockParseException extends Exception {

    private final int lineNumber;

    public BlockParseException(String message) {
        super(message);
        this.lineNumber = 0;
    }

    public BlockParseException(String message, int lineNumber) {
        super(message + " at line " + lineNumber);
        this.lineNumber = lineNumber;
    }

    public BlockParseException(String message, int lineNumber, Throwable cause) {
        super(message + " at line " + lineNumber, cause);
        this.lineNumber = lineNumber;
    }

    public int getLineNumber() {
        return lineNumber;
    }
}
