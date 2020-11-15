package com.qoomon.banking.swift.submessage.field;


import org.junit.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Created by qoomon on 04/11/20.
 */
public class BankOperationTest {

    @Test
    public void of() throws Exception {
        // Given
        GeneralField generalField = new GeneralField(BankOperation.FIELD_TAG_23B, "SSTD");

        // When
        BankOperation field = BankOperation.of(generalField);

        // Then
        assertThat(field.getTag()).isEqualTo(generalField.getTag());
        assertThat(field.getContent()).isEqualTo(generalField.getContent());
    }

}