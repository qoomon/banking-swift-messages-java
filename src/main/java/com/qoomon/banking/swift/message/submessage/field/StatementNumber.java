package com.qoomon.banking.swift.message.submessage.field;

import com.google.common.base.Preconditions;
import com.qoomon.banking.swift.message.submessage.field.exception.FieldNotationParseException;
import com.qoomon.banking.swift.message.submessage.field.notation.SwiftFieldNotation;

import java.util.List;
import java.util.Optional;

/**
 * <b>Statement Number</b>
 * <p>
 * <b>Field Tag</b> :28C:
 * <p>
 * <b>Format</b> 5n[/5n]
 * <p>
 * <b>SubFields</b>
 * <pre>
 * 1: 5n    - Value
 * 2: [/5n] - Sequence Number
 * </pre>
 */
public class StatementNumber implements SwiftField {

    public static final String FIELD_TAG_28C = "28C";

    public static final SwiftFieldNotation SWIFT_NOTATION = new SwiftFieldNotation("5n[/5n]");

    private final String value;

    private final Optional<String> sequenceNumber;


    public StatementNumber(String value, String sequenceNumber) {

        Preconditions.checkArgument(value != null, "value can't be null");

        this.value = value;
        this.sequenceNumber = Optional.ofNullable(sequenceNumber);
    }

    public static StatementNumber of(GeneralField field) throws FieldNotationParseException {
        Preconditions.checkArgument(field.getTag().equals(FIELD_TAG_28C), "unexpected field tag '%s'", field.getTag());

        List<String> subFields = SWIFT_NOTATION.parse(field.getContent());

        String value = subFields.get(0);
        String sequenceNumber = subFields.get(1);

        return new StatementNumber(value, sequenceNumber);
    }

    public String getValue() {
        return value;
    }

    public Optional<String> getSequenceNumber() {
        return sequenceNumber;
    }

    @Override
    public String getTag() {
        return FIELD_TAG_28C;
    }
}
