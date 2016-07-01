package com.qoomon.banking.swift.field;

import com.google.common.base.Preconditions;

/**
 * Created by qoomon on 24/06/16.
 */
public class AccountIdentification implements SwiftMTField {
    /**
     * :25: – Account Identification
     */
    public static final String TAG = "25";

    private final String value;

    public AccountIdentification(GeneralMTField field) {
        Preconditions.checkArgument(field.getTag().equals(getTag()), "unexpected field tag '" + field.getTag() + "'");
        this.value = field.getContent();
    }

    @Override
    public String getTag() {
        return TAG;
    }
}
