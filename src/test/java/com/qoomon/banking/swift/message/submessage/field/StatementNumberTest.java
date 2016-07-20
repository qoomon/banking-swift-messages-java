package com.qoomon.banking.swift.message.submessage.field;

import org.assertj.core.api.Assertions;
import org.junit.Test;

/**
 * Created by qoomon on 20/07/16.
 */
public class StatementNumberTest {

    @Test
    public void getValue() throws Exception {

        // Given
        String statementNumber = "12345";
        String sequenceNumber = "12345";
        StatementNumber field = new StatementNumber(statementNumber, sequenceNumber);

        // When
        String value = field.getValue();

        // Then
        Assertions.assertThat(value).isEqualTo(statementNumber + "/" + sequenceNumber);

    }

}