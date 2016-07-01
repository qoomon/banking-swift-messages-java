package com.qoomon.banking.swift.field;

import com.google.common.base.Preconditions;

/**
 * Created by qoomon on 24/06/16.
 */
public class StatementLine implements SwiftMTField {
    /**
     * :61: – Statement Line
     */
    public static final String TAG = "61";

    private final String value;

    public StatementLine(GeneralMTField field) {
        Preconditions.checkArgument(field.getTag().equals(TAG), "unexpected field tag '" + field.getTag() + "'");
        this.value = field.getContent();
    }

    @Override
    public String getTag() {
        return TAG;
    }
}
