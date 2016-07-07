package com.qoomon.banking.swift.message.submessage.field.subfield;

/**
 * Created by qoomon on 05/07/16.
 */
public enum DebitCreditMark {

    DEBIT,
    CREDIT;

    public static DebitCreditMark of(String value) {
        switch (value) {
            case "DEBIT":
            case "D":
                return DEBIT;
            case "CREDIT":
            case "C":
                return CREDIT;
            default:
                throw new IllegalArgumentException("No mapping found for value '" + value + "'");
        }
    }
}
