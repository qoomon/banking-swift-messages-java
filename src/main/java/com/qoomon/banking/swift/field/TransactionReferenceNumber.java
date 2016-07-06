package com.qoomon.banking.swift.field;

import com.google.common.base.Preconditions;
import com.qoomon.banking.swift.field.notation.SwiftFieldNotation;

import java.util.List;

/**
 * Created by qoomon on 24/06/16.
 */
public class TransactionReferenceNumber implements SwiftMTField {
    /**
     * :20: – TransactionGroup Reference Number
     */
    public static final String TAG = "20";

    /**
     * 20x - Value
     */
    public static final SwiftFieldNotation SWIFT_NOTATION = new SwiftFieldNotation("20x");

    private final String value;

    public TransactionReferenceNumber(String value) {
        this.value = Preconditions.checkNotNull(value);
    }

    public static TransactionReferenceNumber of(GeneralMTField field) {
        Preconditions.checkArgument(field.getTag().equals(TAG), "unexpected field tag '" + field.getTag() + "'");

        List<String> subFields = SWIFT_NOTATION.parse(field.getContent());

        String value = subFields.get(0);

        return new TransactionReferenceNumber(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public String getTag() {
        return TAG;
    }
}
