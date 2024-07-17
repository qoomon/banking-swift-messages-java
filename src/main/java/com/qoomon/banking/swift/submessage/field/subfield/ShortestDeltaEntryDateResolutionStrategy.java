package com.qoomon.banking.swift.submessage.field.subfield;

import java.time.LocalDate;
import java.time.MonthDay;

import static java.lang.Math.abs;
import static java.lang.Math.min;

/**
 * Resolve entry date based on the shortest delta between the entry date and the statement date.
 * <p>
 * This strategy views the {@link MonthDay} timeline as a circle, like the hours on a clock. If the shortest delta
 * around the circle does not span 31 December, the entry date's year is the same as that of the statement. Otherwise,
 * it is either the previous year if the entry date is before the statement date on the circular timeline, or the next year
 * if the entry date is after the statement date.
 * <p>
 * Limitations: This strategy can not account for an entry date more than 12 months in the past (or future).
 */
public class ShortestDeltaEntryDateResolutionStrategy implements EntryDateResolutionStrategy {
    @Override
    public LocalDate resolve(MonthDay entryMonthDay, LocalDate statementDate) {
        int e = LocalDate.from(entryMonthDay.adjustInto(statementDate)).getDayOfYear();
        int s = statementDate.getDayOfYear();
        int lengthOfYear = statementDate.lengthOfYear();

        int statementToEntryDelta = s - e;
        boolean statementAfterEntry = statementToEntryDelta > 0;
        int shortestDelta = min(abs(statementToEntryDelta), lengthOfYear - abs(statementToEntryDelta));

        boolean spans31December = statementAfterEntry ?
                e + shortestDelta != s :
                s + shortestDelta != e;

        int entryYear;
        if (!spans31December) {
            entryYear = statementDate.getYear();
        } else {
            if (statementAfterEntry) {
                entryYear = statementDate.getYear() + 1;
            } else {
                entryYear = statementDate.getYear() - 1;
            }
        }

        return entryMonthDay.atYear(entryYear);
    }
}
