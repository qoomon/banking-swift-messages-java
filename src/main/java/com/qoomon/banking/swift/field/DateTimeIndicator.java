package com.qoomon.banking.swift.field;

import com.google.common.base.Preconditions;
import com.qoomon.banking.swift.field.notation.SwiftFieldNotation;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.util.List;

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

    private final OffsetDateTime dateTime;

    public DateTimeIndicator(GeneralMTField field) {
        Preconditions.checkArgument(field.getTag().equals(TAG), "unexpected field tag '" + field.getTag() + "'");

        //ensure notation
        SWIFT_NOTATION.parse(field.getContent());
        //parse Date Time
        this.dateTime = OffsetDateTime.parse(field.getContent(), DATE_TIME_FORMATTER);
    }

    public OffsetDateTime getDateTime() {
        return dateTime;
    }

    @Override
    public String getTag() {
        return TAG;
    }

}
