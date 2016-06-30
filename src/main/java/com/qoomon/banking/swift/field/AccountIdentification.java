package com.qoomon.banking.swift.field;

/**
 * Created by qoomon on 24/06/16.
 */
public class AccountIdentification {
    /**
     * :25: – Account Identification
     */
    public static final String TAG_25 = "25";

    private final String value;

    public AccountIdentification(String value) {
        this.value = value;
    }
}
