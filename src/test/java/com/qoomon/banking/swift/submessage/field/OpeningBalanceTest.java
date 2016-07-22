package com.qoomon.banking.swift.submessage.field;

import org.junit.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Created by qoomon on 22/07/16.
 */
public class OpeningBalanceTest {

    @Test
    public void of() throws Exception {
        // Given
        GeneralField generalField = new GeneralField(OpeningBalance.FIELD_TAG_60F, "D160717EUR123,");

        // When
        OpeningBalance field = OpeningBalance.of(generalField);

        // Then
        assertThat(field.getTag()).isEqualTo(generalField.getTag());
        assertThat(field.getContent()).isEqualTo(generalField.getContent());
    }

}