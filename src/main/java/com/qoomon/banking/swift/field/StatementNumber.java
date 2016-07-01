package com.qoomon.banking.swift.field;

import com.google.common.base.Preconditions;

/**
 * Created by qoomon on 24/06/16.
 */
public class StatementNumber {
    /**
     * :28C: – Statement Number/Sequence Number
     */
    public static final String TAG = "28C";
    
    private final String value;

    public StatementNumber(GeneralMTField field) {
        Preconditions.checkArgument(field.getTag().equals(TAG), "unexpected field tag '" + field.getTag() + "'");
        this.value = field.getContent();
    }
}
