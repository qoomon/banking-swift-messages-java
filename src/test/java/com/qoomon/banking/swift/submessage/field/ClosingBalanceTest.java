package com.qoomon.banking.swift.submessage.field;

import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Created by qoomon on 21/07/16.
 */
public class ClosingBalanceTest {

    @Test
    public void of() throws Exception {
        // Given

        GeneralField generalField = new GeneralField(ClosingBalance.FIELD_TAG_62F, "D160717EUR123,");

        // When
        ClosingBalance field = ClosingBalance.of(generalField);

        // Then
        assertThat(field.getTag()).isEqualTo(generalField.getTag());
        assertThat(field.getContent()).isEqualTo(generalField.getContent());
    }
}