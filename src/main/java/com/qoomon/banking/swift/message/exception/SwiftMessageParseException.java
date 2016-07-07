package com.qoomon.banking.swift.message.exception;

import com.qoomon.banking.swift.message.block.exception.BlockParseException;

/**
 * Created by qoomon on 27/06/16.
 */
public class SwiftMessageParseException extends Exception {

    private final int blockNumber;

    public SwiftMessageParseException(String message, int blockNumber) {
        super(message + " at block number " + blockNumber);
        this.blockNumber = blockNumber;
    }

    public SwiftMessageParseException(String message, int blockNumber, Throwable cause) {
        super(message + " at block number " + blockNumber, cause);
        this.blockNumber = blockNumber;
    }

    public SwiftMessageParseException(int blockNumber, Throwable cause) {
        super("at block number " + blockNumber, cause);
        this.blockNumber = blockNumber;
    }

    public SwiftMessageParseException(BlockParseException e) {
        super(e);
        blockNumber = 0;
    }

    public int getBlockNumber() {
        return blockNumber;
    }
}
