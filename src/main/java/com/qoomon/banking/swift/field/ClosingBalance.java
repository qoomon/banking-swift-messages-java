package com.qoomon.banking.swift.field;

import com.qoomon.banking.swift.field.notation.SwiftFieldNotation;

/**
 * Created by qoomon on 24/06/16.
 */
public class ClosingBalance {
    /**
     * :62F: - Closing Balance (Booked Funds)
     */
    public static final String TAG_62F = "62F";
    /**
     * :62M: - Intermediate Balance
     */
    public static final String TAG_62M = "62M";

    /**
     * 1!a6!n3!a15d - D/C | Date | Currency | Amount
     */
    public static final SwiftFieldNotation SWIFT_NOTATION = new SwiftFieldNotation("1!a6!n3!a15d");

    private final Type type;

    private final String debitCreditMark;
    private final String date;
    private final String currency;
    private final String amount;

    public ClosingBalance(Type type, String debitCreditMark, String date, String currency, String amount) {
        this.type = type;
        this.debitCreditMark = debitCreditMark;
        this.date = date;
        this.currency = currency;
        this.amount = amount;
    }

    public enum Type {
        OPENING,
        INTERMEDIATE
    }
}
