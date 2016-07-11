package com.qoomon.banking.swift.message.submessage.field;

import com.google.common.base.Preconditions;
import com.qoomon.banking.swift.message.submessage.field.notation.SwiftFieldNotation;

import java.text.ParseException;
import java.util.List;

/**
 * <b>Information to Account Owner</b>
 * <p>
 * <b>Field Tag</b> :86:
 * <p>
 * <b>Format</b> 6*65x
 * <p>
 * <b>SubFields</b>
 * <pre>
 * 1: 6*65x - Value
 * </pre>
 */
public class InformationToAccountOwner implements SwiftField {

    public static final String FIELD_TAG_86 = "86";

    public static final SwiftFieldNotation SWIFT_NOTATION = new SwiftFieldNotation("6*65x");

    private final String value;

    public InformationToAccountOwner(String value) {

        Preconditions.checkArgument(value != null, "value can't be null");

        this.value = value;
    }

    public static InformationToAccountOwner of(GeneralField field) throws ParseException {
        Preconditions.checkArgument(field.getTag().equals(FIELD_TAG_86), "unexpected field tag '%s'",field.getTag());

        List<String> subFields = SWIFT_NOTATION.parse(field.getContent());

        String value = subFields.get(0);

        return new InformationToAccountOwner(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public String getTag() {
        return FIELD_TAG_86;
    }
}
