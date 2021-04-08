package com.qoomon.banking.swift.submessage.field.subfield;

import java.time.LocalDate;
import java.time.MonthDay;

public class EarlierMonthImpliesFollowingYearEntryDateResolutionStrategy implements EntryDateResolutionStrategy {
    @Override
    public LocalDate resolve(MonthDay entryMonthDay, LocalDate valueDate) {
        int entryYear = entryMonthDay.getMonthValue() >= valueDate.getMonthValue()
                ? valueDate.getYear()
                : valueDate.getYear() + 1;
        return entryMonthDay.atYear(entryYear);
    }
}
