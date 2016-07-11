package com.qoomon.banking.swift.message.block;

import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Created by qoomon on 07/07/16.
 */
public class TextBlockTest {

    @Test
    public void of_SHOULD_remove_leading_blank_line_and_trailing_minus() throws Exception {

        // Given
        GeneralBlock generalBlock = new GeneralBlock("4", "\nabc\n-");

        // When
        TextBlock textBlock = TextBlock.of(generalBlock);

        // Then
        assertThat(textBlock).isNotNull();
        assertThat(textBlock.getContent()).isEqualTo("abc\n-");

    }

}