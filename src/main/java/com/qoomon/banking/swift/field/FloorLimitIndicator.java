package com.qoomon.banking.swift.field;

import com.google.common.base.Preconditions;
import com.qoomon.banking.swift.field.notation.SwiftFieldNotation;

import java.util.List;
import java.util.Optional;

/**
 * Created by qoomon on 04/07/16.
 */
public class FloorLimitIndicator implements SwiftMTField {


    /**
     * :34F: - Floor limit indicator debit/credit
     */
    public static final String TAG = "34F";

    /**
     * 3!a[1!a]15d -  Currency | D/C | Amount
     */
    public static final SwiftFieldNotation SWIFT_NOTATION = new SwiftFieldNotation("3!a[1!a]15d");


    private final String currency;
    private final Optional<String> debitCreditMark;
    private final String amount;

    public FloorLimitIndicator(GeneralMTField field) {
        Preconditions.checkArgument(field.getTag().equals(TAG), "unexpected field tag '" + field.getTag() + "'");

        List<String> subFields = SWIFT_NOTATION.parse(field.getContent());
        this.currency = Preconditions.checkNotNull(subFields.get(0));
        Preconditions.checkArgument(subFields.get(1) == null
                || subFields.get(1).equals("C")
                || subFields.get(1).equals("D"));
        this.debitCreditMark = Optional.ofNullable(subFields.get(1));
        this.amount = Preconditions.checkNotNull(subFields.get(2));
    }


    public Optional<String> getDebitCreditMark() {
        return debitCreditMark;
    }

    public String getCurrency() {
        return currency;
    }

    public String getAmount() {
        return amount;
    }

    @Override
    public String getTag() {
        return TAG;
    }
}
