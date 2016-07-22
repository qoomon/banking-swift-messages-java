package com.qoomon.banking.swift.submessage.field;

import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Created by qoomon on 20/07/16.
 */
public class StatementNumberTest {


    @Test
    public void of() throws Exception {
        // Given

        GeneralField generalField = new GeneralField(StatementNumber.FIELD_TAG_28C, "12345/67890");

        // When
        StatementNumber field = StatementNumber.of(generalField);

        // Then
        assertThat(field.getTag()).isEqualTo(generalField.getTag());
        assertThat(field.getContent()).isEqualTo(generalField.getContent());
    }

}