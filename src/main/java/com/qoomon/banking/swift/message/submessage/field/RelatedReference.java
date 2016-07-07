package com.qoomon.banking.swift.message.submessage.field;

import com.google.common.base.Preconditions;
import com.qoomon.banking.swift.message.submessage.field.notation.SwiftFieldNotation;

import java.text.ParseException;
import java.util.List;

/**
 * <b>Related Reference</b>
 * <p>
 * <b>Field Tag</b> :21:
 * <p>
 * <b>Format</b> 16x
 * <p>
 * <b>SubFields</b>
 * <pre>
 * 1: 16x - Value
 * </pre>
 */
public class RelatedReference implements SwiftMTField {

    public static final String FIELD_TAG_21 = "21";

    public static final SwiftFieldNotation SWIFT_NOTATION = new SwiftFieldNotation("16x");

    private final String value;

    public RelatedReference(String value) {
        this.value = Preconditions.checkNotNull(value);
    }

    public static RelatedReference of(GeneralField field) throws ParseException {
        Preconditions.checkArgument(field.getTag().equals(FIELD_TAG_21), "unexpected field tag '" + field.getTag() + "'");

        List<String> subFields = SWIFT_NOTATION.parse(field.getContent());

        String value = subFields.get(0);

        return new RelatedReference(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public String getTag() {
        return FIELD_TAG_21;
    }
}
