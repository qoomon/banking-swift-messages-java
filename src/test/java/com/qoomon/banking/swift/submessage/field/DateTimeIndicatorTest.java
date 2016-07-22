package com.qoomon.banking.swift.submessage.field;


import org.junit.Test;

import static org.assertj.core.api.AssertionsForClassTypes.*;

/**
 * Created by qoomon on 22/07/16.
 */
public class DateTimeIndicatorTest {

    @Test
    public void of() throws Exception {
        // Given
        GeneralField generalField = new GeneralField(DateTimeIndicator.FIELD_TAG_13D, "1607171122-0130");

        // When
        DateTimeIndicator field = DateTimeIndicator.of(generalField);

        // Then
        assertThat(field.getTag()).isEqualTo(generalField.getTag());
        assertThat(field.getContent()).isEqualTo(generalField.getContent());
    }

}