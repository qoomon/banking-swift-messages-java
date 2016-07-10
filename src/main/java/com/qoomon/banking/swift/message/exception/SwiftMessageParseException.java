package com.qoomon.banking.swift.message.exception;

import com.qoomon.banking.swift.message.block.exception.BlockParseException;

/**
 * Created by qoomon on 27/06/16.
 */
public class SwiftMessageParseException extends Exception {

    private final int lineNumber;

    public SwiftMessageParseException(String message, int lineNumber) {
        super(message + " at line number " + lineNumber);
        this.lineNumber = lineNumber;
    }

    public SwiftMessageParseException(String message, int lineNumber, Throwable cause) {
        super(message + " at line number " + lineNumber, cause);
        this.lineNumber = lineNumber;
    }

    public SwiftMessageParseException(int lineNumber, Throwable cause) {
        super("at line number " + lineNumber, cause);
        this.lineNumber = lineNumber;
    }

    public SwiftMessageParseException(BlockParseException e) {
        super(e);
        lineNumber = 0;
    }

    public int getLineNumber() {
        return lineNumber;
    }
}
