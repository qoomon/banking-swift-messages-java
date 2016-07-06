package com.qoomon.banking.swift.field;

import com.google.common.base.Preconditions;
import com.qoomon.banking.swift.field.notation.SwiftFieldNotation;

import java.text.ParseException;
import java.util.List;

/**
 * <b>Account Identification</b>
 * <p>
 * <b>Field Tag</b> :25:
 * <p>
 * <b>Format</b> 1!a6!n3!a15d
 * <p>
 * <b>SubFields</b>
 * <pre>
 * 1: 35x - Value
 * </pre>
 */
public class AccountIdentification implements SwiftMTField {

    public static final String FIELD_TAG_25 = "25";

    public static final SwiftFieldNotation SWIFT_NOTATION = new SwiftFieldNotation("35x");

    private final String value;

    public AccountIdentification(String value) {
        this.value = Preconditions.checkNotNull(value);
    }

    public static AccountIdentification of(GeneralMTField field) throws ParseException {
        Preconditions.checkArgument(field.getTag().equals(FIELD_TAG_25), "unexpected field tag '" + field.getTag() + "'");

        List<String> subFields = SWIFT_NOTATION.parse(field.getContent());

        String value = subFields.get(0);

        return new AccountIdentification(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public String getTag() {
        return FIELD_TAG_25;
    }
}
