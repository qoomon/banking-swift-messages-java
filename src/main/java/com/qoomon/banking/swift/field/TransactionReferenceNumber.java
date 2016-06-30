package com.qoomon.banking.swift.field;

/**
 * Created by qoomon on 24/06/16.
 */
public class TransactionReferenceNumber {
    /**
     * :20: – Transaction Reference Number
     */
    public static final String TAG_20 = "20";

    private final String value;

    public TransactionReferenceNumber(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
