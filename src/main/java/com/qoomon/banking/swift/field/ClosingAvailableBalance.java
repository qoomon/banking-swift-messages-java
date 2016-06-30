package com.qoomon.banking.swift.field;

/**
 * Created by qoomon on 24/06/16.
 */
public class ClosingAvailableBalance {
    /**
     * :64: – Closing Available Balance (Available Funds)
     */
    public static final String TAG_64 = "64";
    private String value;

    public ClosingAvailableBalance(String value) {
        this.value = value;
    }
}
