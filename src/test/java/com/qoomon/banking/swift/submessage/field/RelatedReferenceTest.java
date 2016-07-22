package com.qoomon.banking.swift.submessage.field;

import org.junit.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Created by qoomon on 22/07/16.
 */
public class RelatedReferenceTest {

    @Test
    public void of() throws Exception {
        // Given
        GeneralField generalField = new GeneralField(RelatedReference.FIELD_TAG_21, "1607171122-0130,");

        // When
        RelatedReference field = RelatedReference.of(generalField);

        // Then
        assertThat(field.getTag()).isEqualTo(generalField.getTag());
        assertThat(field.getContent()).isEqualTo(generalField.getContent());
    }

}