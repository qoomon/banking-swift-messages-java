package com.qoomon.banking.swift.mt.exception;

/**
 * Created by qoomon on 27/06/16.
 */
public class SwiftMTParserException extends RuntimeException {

    private final String fieldTag;
    private final int fieldNumber;

    public SwiftMTParserException(String message, int fieldNumber, String fieldTag) {
        super(message + " at field number " + fieldNumber + " field " + fieldTag);
        this.fieldTag = fieldTag;
        this.fieldNumber = fieldNumber;
    }

    public SwiftMTParserException(String message, int fieldNumber, String fieldTag, Throwable cause) {
        super(message + " at field number " + fieldNumber + " field " + fieldTag, cause);
        this.fieldTag = fieldTag;
        this.fieldNumber = fieldNumber;
    }

    public SwiftMTParserException(Throwable cause) {
        super(cause);
        this.fieldTag = "";
        this.fieldNumber = 0;
    }

}
