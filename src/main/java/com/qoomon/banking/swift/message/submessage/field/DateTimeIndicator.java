package com.qoomon.banking.swift.message.submessage.field;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.qoomon.banking.swift.notation.FieldNotationParseException;
import com.qoomon.banking.swift.notation.SwiftNotation;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <b>Date Time Indicator</b>
 * <p>
 * <b>Field Tag</b> :13D:
 * <p>
 * <b>Format</b> 6!n4!n1x4!n
 * <p>
 * <b>SubFields</b>
 * <pre>
 * 1: 6!n - Date - Format 'YYMMDD'
 * 2: 4!n - Time - Format 'hhmm'
 * 3: 1x  - Offset sign - '+' or '-'
 * 4: 4!n - Offset - Format 'hhmm'
 * </pre>
 * <b>Example</b>
 * <pre>
 * 1605191047+0100
 * </pre>
 */
public class DateTimeIndicator implements SwiftField {

    public static final String FIELD_TAG_13D = "13D";

    public static final SwiftNotation SWIFT_NOTATION = new SwiftNotation("6!n4!n1x4!n");

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyMMddHHmmZ");

    private final OffsetDateTime dateTime;


    public DateTimeIndicator(OffsetDateTime dateTime) {

        Preconditions.checkArgument(dateTime != null, "dateTime can't be null");

        this.dateTime = dateTime;
    }

    public static DateTimeIndicator of(GeneralField field) throws FieldNotationParseException {
        Preconditions.checkArgument(field.getTag().equals(FIELD_TAG_13D), "unexpected field tag '%s'", field.getTag());

        List<String> subFields = SWIFT_NOTATION.parse(field.getContent());

        OffsetDateTime value = OffsetDateTime.parse(subFields.stream().collect(Collectors.joining()), DATE_TIME_FORMATTER);

        return new DateTimeIndicator(value);
    }

    public OffsetDateTime getDateTime() {
        return dateTime;
    }

    @Override
    public String getTag() {
        return FIELD_TAG_13D;
    }

    @Override
    public String getContent() {
        try {
            return SWIFT_NOTATION.render(Lists.newArrayList(DATE_TIME_FORMATTER.format(dateTime)));
        } catch (FieldNotationParseException e) {
            throw new IllegalStateException("Invalid field values within " + getClass().getSimpleName() + " instance", e);
        }
    }

}
