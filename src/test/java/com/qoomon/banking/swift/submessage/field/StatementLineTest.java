package com.qoomon.banking.swift.submessage.field;

import org.junit.Test;

import static org.assertj.core.api.AssertionsForClassTypes.*;

/**
 * Created by qoomon on 18/07/2016.
 */
public class StatementLineTest {

    @Test
    public void of() throws Exception {
        // Given
        GeneralField generalField = new GeneralField(StatementLine.FIELD_TAG_61, "160130" + "C" + "R" + "123,456" + "NSTO" + "abcdef" + "//xyz" + "\nfoobar");

        // When
        StatementLine field = StatementLine.of(generalField);

        // Then
        assertThat(field.getTag()).isEqualTo(generalField.getTag());
        assertThat(field.getContent()).isEqualTo(generalField.getContent());
    }
}