package com.qoomon.banking.swift.submessage.field.subfield;

import java.time.LocalDate;
import java.time.MonthDay;

import static java.lang.Math.abs;
import static java.lang.Math.min;

/**
 * Resolve entry date based on the shortest delta between the entry date and the value date.
 * <p>
 * This strategy views the {@link MonthDay} timeline as a circle, like the hours on a clock. If the shortest delta
 * around the circle does not span 31 December, the entry date's year is the same as that of the value date. Otherwise
 * it is either the previous year if the entry date is before the value date on the circular timeline, or the next year
 * if the entry date is after the value date.
 * <p>
 * Limitations: This strategy can not account for an entry date more than 12 months in the past (or future).
 */
public class ShortestDeltaEntryDateResolutionStrategy implements EntryDateResolutionStrategy {
    @Override
    public LocalDate resolve(MonthDay entryMonthDay, LocalDate valueDate) {
        int e = LocalDate.from(entryMonthDay.adjustInto(valueDate)).getDayOfYear();
        int v = valueDate.getDayOfYear();
        int lengthOfYear = valueDate.lengthOfYear();

        int valueToEntryDelta = v - e;
        boolean valueAfterEntry = valueToEntryDelta > 0;
        int shortestDelta = min(abs(valueToEntryDelta), lengthOfYear - abs(valueToEntryDelta));

        boolean spans31December = valueAfterEntry ?
                e + shortestDelta != v :
                v + shortestDelta != e;

        int entryYear;
        if (!spans31December) {
            entryYear = valueDate.getYear();
        } else {
            if (valueAfterEntry) {
                entryYear = valueDate.getYear() + 1;
            } else {
                entryYear = valueDate.getYear() - 1;
            }
        }

        return entryMonthDay.atYear(entryYear);
    }
}
