package com.qoomon.banking.swift.message.block.exception;

/**
 * Created by qoomon on 07/07/16.
 */
public class BlockFieldParseException extends Exception {


    public BlockFieldParseException(String message) {
        super(message);
    }


    public BlockFieldParseException(String message, Throwable cause) {
        super(message, cause);
    }

}
