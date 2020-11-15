package com.qoomon.banking.swift.submessage.field;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.qoomon.banking.swift.notation.FieldNotationParseException;
import com.qoomon.banking.swift.notation.SwiftNotation;
import com.qoomon.banking.swift.submessage.field.subfield.TimeType;

import java.time.OffsetTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * <b>Time Indicator</b>
 * <p>
 * <b>Field Tag</b> :13C:
 * <p>
 * <b>Format</b> /8c/4!n1!x4!n
 * <p>
 * <b>SubFields</b>
 * <pre>
 * 1: 8c  - Time Code
 * 2: 4!n - Time - Format 'hhmm'
 * 3: 1!x - Offset sign - '+' or '-'
 * 4: 4!n - Offset - Format 'hhmm'
 * </pre>
 * <b>Example</b>
 * <pre>
 * /CLSTIME/0915+0100
 * </pre>
 */
public class TimeIndicator implements SwiftField {

    public static final String FIELD_TAG_13C = "13C";

    public static final SwiftNotation SWIFT_NOTATION = new SwiftNotation("/8c/4!n1!x4!n");

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HHmmZ");

    private final TimeType type;

    private final OffsetTime time;


    public TimeIndicator(TimeType type, OffsetTime time) {

        Preconditions.checkArgument(time != null, "time can't be null");

        this.type = type;
        this.time = time;
    }

    public static TimeIndicator of(GeneralField field) throws FieldNotationParseException {
        Preconditions.checkArgument(field.getTag().equals(FIELD_TAG_13C), "unexpected field tag '%s'", field.getTag());

        List<String> subFields = SWIFT_NOTATION.parse(field.getContent());

        TimeType code = TimeType.valueOf(subFields.get(0));
        OffsetTime time = OffsetTime.parse(Joiner.on("").join(subFields.subList(1, 4)), TIME_FORMATTER);

        return new TimeIndicator(code, time);
    }

    public TimeType getType() {
        return type;
    }

    public OffsetTime getTime() {
        return time;
    }

    @Override
    public String getTag() {
        return FIELD_TAG_13C;
    }

    @Override
    public String getContent() {
        try {
            return SWIFT_NOTATION.render(SWIFT_NOTATION.parse("/" + type + "/" + TIME_FORMATTER.format(time)));
        } catch (FieldNotationParseException e) {
            throw new IllegalStateException("Invalid field values within " + getClass().getSimpleName() + " instance", e);
        }
    }

}
