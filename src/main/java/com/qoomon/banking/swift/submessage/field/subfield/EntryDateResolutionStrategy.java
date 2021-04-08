package com.qoomon.banking.swift.submessage.field.subfield;

import java.time.LocalDate;
import java.time.MonthDay;

/**
 * Strategy to determine the correct {@link com.qoomon.banking.swift.submessage.field.StatementLine} entry date.
 *
 * Statement line :61: sub field 2 contains an optional entry date:
 *
 * Notation: [4!n]
 * Format: 'MMDD'
 *
 * Implementers of this strategy must determine the correct year of the entry date based on the the given value date.
 */
public interface EntryDateResolutionStrategy {
    LocalDate resolve(MonthDay entryMonthDay, LocalDate valueDate);
}
