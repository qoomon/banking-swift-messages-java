package com.qoomon.banking.swift.notation;

/**
 * Created by qoomon on 27/06/16.
 */
public class FieldNotationParseException extends Exception {

    private final int index;

    public FieldNotationParseException(String message, int index) {
        super(message + " at index " + index);
        this.index = index;
    }

    public FieldNotationParseException(String message, int index, Throwable cause) {
        super(message + " at index" + index, cause);
        this.index = index;
    }

    public FieldNotationParseException(Throwable cause) {
        super(cause);
        index = 0;
    }

    public int getIndex() {
        return index;
    }
}
