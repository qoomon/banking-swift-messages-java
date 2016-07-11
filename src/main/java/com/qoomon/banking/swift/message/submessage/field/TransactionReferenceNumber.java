package com.qoomon.banking.swift.message.submessage.field;

import com.google.common.base.Preconditions;
import com.qoomon.banking.swift.message.submessage.field.notation.SwiftFieldNotation;

import java.text.ParseException;
import java.util.List;


/**
 * <b>Transaction Reference Number</b>
 * <p>
 * <b>Field Tag</b> :20:
 * <p>
 * <b>Format</b> 20x
 * <p>
 * <b>SubFields</b>
 * <pre>
 * 1: 20x    - Value
 * </pre>
 */
public class TransactionReferenceNumber implements SwiftField {

    public static final String FIELD_TAG_20 = "20";

    public static final SwiftFieldNotation SWIFT_NOTATION = new SwiftFieldNotation("20x");

    private final String value;

    public TransactionReferenceNumber(String value) {

        Preconditions.checkArgument(value != null, "value can't be null");

        this.value = value;
    }

    public static TransactionReferenceNumber of(GeneralField field) throws ParseException {
        Preconditions.checkArgument(field.getTag().equals(FIELD_TAG_20), "unexpected field tag '%s'", field.getTag());

        List<String> subFields = SWIFT_NOTATION.parse(field.getContent());

        String value = subFields.get(0);

        return new TransactionReferenceNumber(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public String getTag() {
        return FIELD_TAG_20;
    }
}
