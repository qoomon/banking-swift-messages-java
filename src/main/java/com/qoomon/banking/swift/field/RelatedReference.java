package com.qoomon.banking.swift.field;

import com.google.common.base.Preconditions;
import com.qoomon.banking.swift.field.notation.SwiftFieldNotation;

import java.util.List;

/**
 * Created by qoomon on 24/06/16.
 */
public class RelatedReference implements SwiftMTField {
    /**
     * :21: –  Related Reference
     */
    public static final String TAG = "21";

    /**
     * 16x - Value
     */
    public static final SwiftFieldNotation SWIFT_NOTATION = new SwiftFieldNotation("16x");

    private final String value;

    public RelatedReference(String value) {
        this.value = Preconditions.checkNotNull(value);
    }

    public static RelatedReference of(GeneralMTField field) {
        Preconditions.checkArgument(field.getTag().equals(TAG), "unexpected field tag '" + field.getTag() + "'");

        List<String> subFields = SWIFT_NOTATION.parse(field.getContent());

        String value = subFields.get(0);

        return new RelatedReference(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public String getTag() {
        return TAG;
    }
}
