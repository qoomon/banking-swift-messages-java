package com.qoomon.banking.swift.message.submessage.field;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.qoomon.banking.swift.notation.FieldNotationParseException;
import com.qoomon.banking.swift.notation.SwiftNotation;

import java.math.BigDecimal;
import java.util.List;

/**
 * <b>Account Identification</b>
 * <p>
 * <b>Field Tag</b> :25:
 * <p>
 * <b>Format</b> 1!a6!n3!a15d
 * <p>
 * <b>SubFields</b>
 * <pre>{@literal
 * 1: 35x - Value
 * }</pre>
 */
public class AccountIdentification implements SwiftField {

    public static final String FIELD_TAG_25 = "25";

    public static final SwiftNotation SWIFT_NOTATION = new SwiftNotation("35x");

    private final String value;


    public AccountIdentification(String value) {
        Preconditions.checkArgument(value != null, "value can't be null");
        this.value = value;
    }

    public static AccountIdentification of(GeneralField field) throws FieldNotationParseException {
        Preconditions.checkArgument(field.getTag().equals(FIELD_TAG_25), "unexpected field tag '%s'", field.getTag());

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

    @Override
    public String getContent() {
        try {
            return SWIFT_NOTATION.render(Lists.newArrayList(value));
        } catch (FieldNotationParseException e) {
            throw new IllegalStateException("Invalid field values within " + getClass().getSimpleName() + " instance", e);
        }
    }
}
