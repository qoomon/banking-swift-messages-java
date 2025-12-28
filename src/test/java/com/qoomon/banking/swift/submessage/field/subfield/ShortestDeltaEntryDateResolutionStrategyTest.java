package com.qoomon.banking.swift.submessage.field.subfield;

import org.junit.Test;

import java.time.LocalDate;
import java.time.MonthDay;

import static org.assertj.core.api.Assertions.assertThat;

public class ShortestDeltaEntryDateResolutionStrategyTest {
    private final EntryDateResolutionStrategy strategy = new ShortestDeltaEntryDateResolutionStrategy();

    @Test
    public void of_WHEN_smallest_delta_between_entry_date_and_value_date_does_not_span_December_THEN_entry_year_is_same_as_value_year() {
        // When
        LocalDate entryDate = strategy.resolve(MonthDay.parse("--05-12"), LocalDate.parse("2016-03-31"));

        // Then
        assertThat(entryDate).isEqualTo(LocalDate.parse("2016-05-12"));
    }

    @Test
    public void of_WHEN_smallest_delta_does_not_span_December_and_entry_date_before_value_date_THEN_entry_year_is_same_as_value_year() {
        // When
        LocalDate entryDate = strategy.resolve(MonthDay.parse("--03-12"), LocalDate.parse("2016-05-31"));

        // Then
        assertThat(entryDate).isEqualTo(LocalDate.parse("2016-03-12"));
    }

    @Test
    public void of_WHEN_smallest_delta_spans_December_and_entry_date_before_value_date_THEN_entry_year_is_next_year() {
        // When
        LocalDate entryDate = strategy.resolve(MonthDay.parse("--01-12"), LocalDate.parse("2015-10-31"));

        // Then
        assertThat(entryDate).isEqualTo(LocalDate.parse("2016-01-12"));
    }

    @Test
    public void of_WHEN_smallest_delta_spans_December_and_entry_date_after_value_date_THEN_entry_year_is_previous_year() {
        // When
        LocalDate entryDate = strategy.resolve(MonthDay.parse("--11-12"), LocalDate.parse("2016-03-31"));

        // Then
        assertThat(entryDate).isEqualTo(LocalDate.parse("2015-11-12"));
    }
}