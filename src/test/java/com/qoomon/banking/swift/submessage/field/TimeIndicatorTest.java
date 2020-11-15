package com.qoomon.banking.swift.submessage.field;


import org.junit.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Created by qoomon on 04/11/20.
 */
public class TimeIndicatorTest {

    @Test
    public void of() throws Exception {
        // Given
        GeneralField generalField = new GeneralField(TimeIndicator.FIELD_TAG_13C, "/CLSTIME/0915+0100");

        // When
        TimeIndicator field = TimeIndicator.of(generalField);

        // Then
        assertThat(field.getTag()).isEqualTo(generalField.getTag());
        assertThat(field.getContent()).isEqualTo(generalField.getContent());
    }

}