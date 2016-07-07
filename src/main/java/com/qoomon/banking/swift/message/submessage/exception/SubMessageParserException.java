package com.qoomon.banking.swift.message.submessage.exception;

/**
 * Created by qoomon on 27/06/16.
 */
public class SubMessageParserException extends RuntimeException {

    private final String fieldTag;
    private final int fieldNumber;

    public SubMessageParserException(String message, int fieldNumber, String fieldTag) {
        super(message + " at field number " + fieldNumber + ", field tag " + fieldTag);
        this.fieldTag = fieldTag;
        this.fieldNumber = fieldNumber;
    }

    public SubMessageParserException(String message, int fieldNumber, String fieldTag, Throwable cause) {
        super(message + " at field number " + fieldNumber + ", field tag " + fieldTag, cause);
        this.fieldTag = fieldTag;
        this.fieldNumber = fieldNumber;
    }

    public SubMessageParserException(Throwable cause) {
        super(cause);
        this.fieldTag = "";
        this.fieldNumber = 0;
    }

}
