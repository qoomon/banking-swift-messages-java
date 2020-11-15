package com.qoomon.banking.swift.submessage.field;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.qoomon.banking.swift.notation.FieldNotationParseException;
import com.qoomon.banking.swift.notation.SwiftNotation;
import com.qoomon.banking.swift.submessage.field.subfield.BankOperationType;

import java.util.List;

/**
 * <b>Bank Operation</b>
 * <p>
 * <b>Field Tag</b> :23B:
 * <p>
 * <b>Format</b> 4!c
 * <p>
 * <b>SubFields</b>
 * <pre>
 * 1: 4!c - Code
 * </pre>
 * <b>Example</b>
 * <pre>
 * SSTD
 * </pre>
 */
public class BankOperation implements SwiftField {

    public static final String FIELD_TAG_23B = "23B";

    public static final SwiftNotation SWIFT_NOTATION = new SwiftNotation("4!c");

    private final BankOperationType type;


    public BankOperation(BankOperationType type) {

        Preconditions.checkArgument(type != null, "type can't be null");

        this.type = type;
    }

    public static BankOperation of(GeneralField field) throws FieldNotationParseException {
        Preconditions.checkArgument(field.getTag().equals(FIELD_TAG_23B), "unexpected field tag '%s'", field.getTag());

        List<String> subFields = SWIFT_NOTATION.parse(field.getContent());

        BankOperationType type = BankOperationType.ofFieldValue(subFields.get(0));

        return new BankOperation(type);
    }

    public BankOperationType getType() {
        return type;
    }

    @Override
    public String getTag() {
        return FIELD_TAG_23B;
    }

    @Override
    public String getContent() {
        try {
            return SWIFT_NOTATION.render(Lists.newArrayList(type.toFieldValue()));
        } catch (FieldNotationParseException e) {
            throw new IllegalStateException("Invalid field values within " + getClass().getSimpleName() + " instance", e);
        }
    }

}
