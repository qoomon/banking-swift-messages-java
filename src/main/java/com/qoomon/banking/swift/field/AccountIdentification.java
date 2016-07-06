package com.qoomon.banking.swift.field;

import com.google.common.base.Preconditions;
import com.qoomon.banking.swift.field.notation.SwiftFieldNotation;

import java.util.List;

/**
 * Created by qoomon on 24/06/16.
 */
public class AccountIdentification implements SwiftMTField {
    /**
     * :25: – Account Identification
     */
    public static final String TAG = "25";

    /**
     * 35x - Value
     */
    public static final SwiftFieldNotation SWIFT_NOTATION = new SwiftFieldNotation("35x");

    private final String value;

    public AccountIdentification(String value) {
        this.value = Preconditions.checkNotNull(value);
    }

    public static AccountIdentification of(GeneralMTField field) {
        Preconditions.checkArgument(field.getTag().equals(TAG), "unexpected field tag '" + field.getTag() + "'");

        List<String> subFields = SWIFT_NOTATION.parse(field.getContent());

        String value = subFields.get(0);

        return new AccountIdentification(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public String getTag() {
        return TAG;
    }
}
