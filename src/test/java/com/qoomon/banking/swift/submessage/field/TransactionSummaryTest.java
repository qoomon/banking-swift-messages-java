package com.qoomon.banking.swift.submessage.field;

import org.junit.Test;

import static org.assertj.core.api.AssertionsForClassTypes.*;

/**
 * Created by qoomon on 22/07/16.
 */
public class TransactionSummaryTest {

    @Test
    public void of() throws Exception {
        // Given
        GeneralField generalField = new GeneralField(TransactionSummary.FIELD_TAG_90C, "12EUR123,");

        // When
        TransactionSummary field = TransactionSummary.of(generalField);

        // Then
        assertThat(field.getTag()).isEqualTo(generalField.getTag());
        assertThat(field.getContent()).isEqualTo(generalField.getContent());
    }

}