package com.qoomon.banking.swift.field;

/**
 * Created by qoomon on 24/06/16.
 */
public class StatementNumber {
    /**
     * :28C: – Statement Number/Sequence Number
     */
    public static final String TAG_28C = "28C";
    
    private String value;

    public StatementNumber(String value) {
        this.value = value;
    }
}
