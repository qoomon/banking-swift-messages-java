package com.qoomon.banking.swift.field;

import com.google.common.base.Preconditions;

/**
 * Created by qoomon on 24/06/16.
 */
public class ForwardAvailableBalance implements SwiftMTField {
    /**
     * :65: – Forward Available Balance
     */
    public static final String TAG = "65";

    private final String value;

    public ForwardAvailableBalance(GeneralMTField field) {
        Preconditions.checkArgument(field.getTag().equals(TAG), "unexpected field tag '" + field.getTag() + "'");
        this.value = field.getContent();
    }

    @Override
    public String getTag() {
        return TAG;
    }
}
