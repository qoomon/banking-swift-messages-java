package com.qoomon.banking.swift.submessage.field;

import org.junit.Test;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

/**
 * Created by qoomon on 22/07/16.
 */
public class InformationToAccountOwnerTest {

    @Test
    public void of() throws Exception {
        // Given
        GeneralField generalField = new GeneralField(InformationToAccountOwner.FIELD_TAG_86, "1607171122-0130,");

        // When
        InformationToAccountOwner field = InformationToAccountOwner.of(generalField);

        // Then
        assertThat(field.getTag()).isEqualTo(generalField.getTag());
        assertThat(field.getContent()).isEqualTo(generalField.getContent());
    }

}