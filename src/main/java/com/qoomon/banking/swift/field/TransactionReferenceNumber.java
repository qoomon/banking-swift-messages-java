package com.qoomon.banking.swift.field;

import com.google.common.base.Preconditions;

/**
 * Created by qoomon on 24/06/16.
 */
public class TransactionReferenceNumber implements SwiftMTField {
    /**
     * :20: – Transaction Reference Number
     */
    public static final String TAG = "20";

    private final String value;

    public TransactionReferenceNumber(GeneralMTField field) {
        Preconditions.checkArgument(field.getTag().equals(TAG), "unexpected field tag '" + field.getTag() + "'");
        this.value = field.getContent();
    }

    public String getValue() {
        return value;
    }

    @Override
    public String getTag() {
        return TAG;
    }
}
