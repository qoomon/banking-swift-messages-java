package com.qoomon.banking.swift.submessage.field;

import org.junit.Test;

import static org.assertj.core.api.AssertionsForInterfaceTypes.*;


/**
 * Created by qoomon on 22/07/16.
 */
public class ClosingAvailableBalanceTest {


    @Test
    public void of() throws Exception {
        // Given
        GeneralField generalField = new GeneralField(ClosingAvailableBalance.FIELD_TAG_64, "D160717EUR2233,");

        // When
        ClosingAvailableBalance field = ClosingAvailableBalance.of(generalField);

        // Then
        assertThat(field.getTag()).isEqualTo(generalField.getTag());
        assertThat(field.getContent()).isEqualTo(generalField.getContent());
    }

}