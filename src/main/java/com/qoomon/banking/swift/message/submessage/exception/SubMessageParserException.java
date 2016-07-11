package com.qoomon.banking.swift.message.submessage.exception;

/**
 * Created by qoomon on 27/06/16.
 */
public class SubMessageParserException extends RuntimeException {

    private final String fieldTag;
    private final int lineNumber;

    public SubMessageParserException(String message, int lineNumber, String fieldTag) {
        super(message + " at line number " + lineNumber + ", field tag " + fieldTag);
        this.fieldTag = fieldTag;
        this.lineNumber = lineNumber;
    }

    public SubMessageParserException(String message, int lineNumber, String fieldTag, Throwable cause) {
        super(message + " at line number " + lineNumber + ", field tag " + fieldTag, cause);
        this.fieldTag = fieldTag;
        this.lineNumber = lineNumber;
    }

    public SubMessageParserException(Throwable cause) {
        super(cause);
        this.fieldTag = "";
        this.lineNumber = 0;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getFieldTag() {
        return fieldTag;
    }
}
