package com.qoomon.banking.swift.submessage.field;

import org.junit.Test;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

/**
 * Created by qoomon on 22/07/16.
 */
public class TransactionReferenceNumberTest {

    @Test
    public void of() throws Exception {
        // Given
        GeneralField generalField = new GeneralField(TransactionReferenceNumber.FIELD_TAG_20, "1234567-0130,");

        // When
        TransactionReferenceNumber field = TransactionReferenceNumber.of(generalField);

        // Then
        assertThat(field.getTag()).isEqualTo(generalField.getTag());
        assertThat(field.getContent()).isEqualTo(generalField.getContent());
    }

}