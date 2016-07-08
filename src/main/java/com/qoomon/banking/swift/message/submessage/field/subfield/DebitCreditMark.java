package com.qoomon.banking.swift.message.submessage.field.subfield;

/**
 * Created by qoomon on 05/07/16.
 */
public enum DebitCreditMark {

    DEBIT,
    DEBIT_REVERSAL,
    CREDIT,
    CREDIT_REVERSAL;

    public static DebitCreditMark of(String value) {
        switch (value) {
            case "DEBIT":
            case "D":
                return DEBIT;
            case "CREDIT":
            case "C":
                return CREDIT;
            case "DEBIT_REVERSAL":
            case "DR":
                return DEBIT_REVERSAL;
            case "CREDIT_REVERSAL":
            case "CR":
                return CREDIT_REVERSAL;
            default:
                throw new IllegalArgumentException("No mapping found for value '" + value + "'");
        }
    }
}
