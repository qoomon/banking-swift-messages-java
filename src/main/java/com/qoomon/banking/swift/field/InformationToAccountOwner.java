package com.qoomon.banking.swift.field;

import com.google.common.base.Preconditions;

/**
 * Created by qoomon on 24/06/16.
 */
public class InformationToAccountOwner implements SwiftMTField {
    /**
     * :86: – Information to Account Owner
     */
    public static final String TAG = "86";

    private final String value;

    public InformationToAccountOwner(GeneralMTField field) {
        Preconditions.checkArgument(field.getTag().equals(TAG), "unexpected field tag '" + field.getTag() + "'");
        this.value = field.getContent();
    }

    @Override
    public String getTag() {
        return TAG;
    }
}
