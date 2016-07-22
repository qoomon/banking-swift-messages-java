package com.qoomon.banking.swift.submessage.field;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.qoomon.banking.swift.notation.FieldNotationParseException;
import com.qoomon.banking.swift.notation.SwiftNotation;

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

    public static final SwiftNotation SWIFT_NOTATION = new SwiftNotation("6*65x");

    private final String content;


    public InformationToAccountOwner(String content) {

        Preconditions.checkArgument(content != null, "content can't be null");

        this.content = content;
    }

    public static InformationToAccountOwner of(GeneralField field) throws FieldNotationParseException {
        Preconditions.checkArgument(field.getTag().equals(FIELD_TAG_86), "unexpected field tag '%s'", field.getTag());

        List<String> subFields = SWIFT_NOTATION.parse(field.getContent());

        String value = subFields.get(0);

        return new InformationToAccountOwner(value);
    }

    @Override
    public String getTag() {
        return FIELD_TAG_86;
    }

    @Override
    public String getContent() {
        try {
            return SWIFT_NOTATION.render(Lists.newArrayList(content));
        } catch (FieldNotationParseException e) {
            throw new IllegalStateException("Invalid field values within " + getClass().getSimpleName() + " instance", e);
        }
    }
}
