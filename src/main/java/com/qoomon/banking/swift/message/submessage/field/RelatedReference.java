package com.qoomon.banking.swift.message.submessage.field;

import com.google.common.base.Preconditions;
import com.qoomon.banking.swift.notation.FieldNotationParseException;
import com.qoomon.banking.swift.notation.SwiftNotation;

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
public class RelatedReference implements SwiftField {

    public static final String FIELD_TAG_21 = "21";

    public static final SwiftNotation SWIFT_NOTATION = new SwiftNotation("16x");

    private final String value;


    public RelatedReference(String value) {

        Preconditions.checkArgument(value != null, "value can't be null");

        this.value = value;
    }

    public static RelatedReference of(GeneralField field) throws FieldNotationParseException {
        Preconditions.checkArgument(field.getTag().equals(FIELD_TAG_21), "unexpected field tag '%s'", field.getTag());

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
