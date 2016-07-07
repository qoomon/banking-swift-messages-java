package com.qoomon.banking.swift.message.submessage.field;

import com.google.common.base.Preconditions;
import com.qoomon.banking.swift.message.submessage.field.notation.SwiftFieldNotation;

import java.text.ParseException;
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
 * 1: 6!n - Debit/Credit Mark - 'D' = Debit, 'C' Credit
 * 2: 4!n - Date - Format 'YYMMDD'
 * 3: 1x  - Offset sign - '+' or '-'
 * 4: 4!n - Offset - Format 'hhmm'
 * </pre>
 * <p>
 * <b>Example</b>
 * <pre>
 * 1605191047+0100
 * </pre>
 */
public class DateTimeIndicator implements SwiftMTField {

    public static final String FIELD_TAG_13D = "13D";

    public static final SwiftFieldNotation SWIFT_NOTATION = new SwiftFieldNotation("6!n4!n1x4!n");

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyMMddHHmmZ");

    private final OffsetDateTime value;

    public DateTimeIndicator(OffsetDateTime value) {
        this.value = Preconditions.checkNotNull(value);
    }

    public static DateTimeIndicator of(GeneralMTField field) throws ParseException {
        Preconditions.checkArgument(field.getTag().equals(FIELD_TAG_13D), "unexpected field tag '" + field.getTag() + "'");

        List<String> subFields = SWIFT_NOTATION.parse(field.getContent());

        OffsetDateTime value = OffsetDateTime.parse(subFields.stream().collect(Collectors.joining()), DATE_TIME_FORMATTER);

        return new DateTimeIndicator(value);
    }

    public OffsetDateTime getValue() {
        return value;
    }

    @Override
    public String getTag() {
        return FIELD_TAG_13D;
    }


}
