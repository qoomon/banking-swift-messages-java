package com.qoomon.banking.swift.field;

import com.google.common.base.Preconditions;
import com.qoomon.banking.swift.field.notation.SwiftFieldNotation;

import java.util.List;

/**
 * Created by qoomon on 24/06/16.
 */
public class InformationToAccountOwner implements SwiftMTField {
    /**
     * :86: – Information to Account Owner
     */
    public static final String TAG = "86";

    /**
     * 6*65x - Value
     */
    public static final SwiftFieldNotation SWIFT_NOTATION = new SwiftFieldNotation("6*65x");

    private final String value;

    public InformationToAccountOwner(String value) {
        this.value = Preconditions.checkNotNull(value);
    }

    public static InformationToAccountOwner of(GeneralMTField field) {
        Preconditions.checkArgument(field.getTag().equals(TAG), "unexpected field tag '" + field.getTag() + "'");

        List<String> subFields = SWIFT_NOTATION.parse(field.getContent());

        String value = subFields.get(0);

        return new InformationToAccountOwner(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public String getTag() {
        return TAG;
    }
}
