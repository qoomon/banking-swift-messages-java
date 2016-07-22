package com.qoomon.banking.swift;

import com.qoomon.banking.iban.IBAN;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Created by qoomon on 19/07/16.
 */
public class BICTest {

    @Test
    public void of_WHEN_valid_iban_RETURN_iban() throws Exception {

        // Given
        String ibanText = "DE44 5001 0517 5407 3249 31";

        // When
        IBAN iban = IBAN.of(ibanText);

        // Then
        assertThat(iban).isNotNull();
        assertThat(iban.getCountryCode()).isEqualTo("DE");
        assertThat(iban.getCheckDigits()).isEqualTo("44");
        assertThat(iban.getBban()).isEqualTo("500105175407324931");
    }

}