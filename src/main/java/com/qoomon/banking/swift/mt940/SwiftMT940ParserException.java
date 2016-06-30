package com.qoomon.banking.swift.mt940;

/**
 * Created by qoomon on 27/06/16.
 */
public class SwiftMT940ParserException extends RuntimeException {

    private final String fieldTag;
    private final int fieldNumber;

    public SwiftMT940ParserException(String message, int fieldNumber, String fieldTag) {
        super(message + " at field number " + fieldNumber + " field " + fieldTag);
        this.fieldTag = fieldTag;
        this.fieldNumber = fieldNumber;
    }

    public SwiftMT940ParserException(String message, int fieldNumber, String fieldTag, Throwable cause) {
        super(message + " at field number " + fieldNumber + " field " + fieldTag, cause);
        this.fieldTag = fieldTag;
        this.fieldNumber = fieldNumber;
    }

    public SwiftMT940ParserException(Throwable cause) {
        super(cause);
        this.fieldTag = "";
        this.fieldNumber = 0;
    }

}
