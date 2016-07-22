package com.qoomon.banking.swift.submessage.field;

import org.junit.Test;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


/**
 * Created by qoomon on 22/07/16.
 */
public class AccountIdentificationTest {

    @Test
    public void of() throws Exception {
        // Given

        GeneralField generalField = new GeneralField(AccountIdentification.FIELD_TAG_25, "aabbccddeeff112233,");

        // When
        AccountIdentification field = AccountIdentification.of(generalField);

        // Then
        assertThat(field.getTag()).isEqualTo(generalField.getTag());
        assertThat(field.getContent()).isEqualTo(generalField.getContent());
    }

}