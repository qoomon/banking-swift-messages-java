package com.qoomon.banking.swift.field;

import com.google.common.base.Preconditions;

/**
 * Created by qoomon on 24/06/16.
 */
public class ClosingAvailableBalance implements SwiftMTField {
    /**
     * :64: – Closing Available Balance (Available Funds)
     */
    public static final String TAG = "64";

    private final String value;

    public ClosingAvailableBalance(GeneralMTField field) {
        Preconditions.checkArgument(field.getTag().equals(TAG), "unexpected field tag '" + field.getTag() + "'");
        this.value = Preconditions.checkNotNull(field.getContent());
    }

    @Override
    public String getTag() {
        return TAG;
    }
}
