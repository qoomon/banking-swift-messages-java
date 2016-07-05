package com.qoomon.banking.swift.field;

import com.google.common.base.Preconditions;

/**
 * Created by qoomon on 24/06/16.
 */
public class RelatedReference implements SwiftMTField {
    /**
     * :21: –  Related Reference
     */
    public static final String TAG = "21";

    private final String value;

    public RelatedReference(GeneralMTField field) {
        Preconditions.checkArgument(field.getTag().equals(TAG), "unexpected field tag '" + field.getTag() + "'");
        this.value = Preconditions.checkNotNull(field.getContent());
    }

    @Override
    public String getTag() {
        return TAG;
    }
}
