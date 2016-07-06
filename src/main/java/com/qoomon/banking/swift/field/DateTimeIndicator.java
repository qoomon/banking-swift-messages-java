package com.qoomon.banking.swift.field;

import com.google.common.base.Preconditions;
import com.qoomon.banking.swift.field.notation.SwiftFieldNotation;

import java.text.ParseException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by qoomon on 04/07/16.
 */
public class DateTimeIndicator implements SwiftMTField {

    /**
     * :13D: - Date/Time Indicator
     */
    public static final String TAG = "13D";

    /**
     * 6!n4!n1x4!n - yyMMdd | HHmm | +/- | Zone Offset  - e.g. 1605191047+0100
     */
    public static final SwiftFieldNotation SWIFT_NOTATION = new SwiftFieldNotation("6!n4!n1x4!n");

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyMMddHHmmZ");

    private final OffsetDateTime value;

    public DateTimeIndicator(OffsetDateTime value) {
        this.value = Preconditions.checkNotNull(value);
    }

    public static DateTimeIndicator of(GeneralMTField field) throws ParseException {
        Preconditions.checkArgument(field.getTag().equals(TAG), "unexpected field tag '" + field.getTag() + "'");

        List<String> subFields = SWIFT_NOTATION.parse(field.getContent());

        OffsetDateTime value = OffsetDateTime.parse(subFields.stream().collect(Collectors.joining()), DATE_TIME_FORMATTER);

        return new DateTimeIndicator(value);
    }

    public OffsetDateTime getValue() {
        return value;
    }

    @Override
    public String getTag() {
        return TAG;
    }


}
