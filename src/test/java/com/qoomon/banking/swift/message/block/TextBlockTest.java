package com.qoomon.banking.swift.message.block;

import com.qoomon.banking.swift.message.block.exception.BlockFieldParseException;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;

/**
 * Created by qoomon on 07/07/16.
 */
public class TextBlockTest {

    @Test
    public void of_WHEN_valid_block_with_info_line_is_passed_RETURN_new_block() throws Exception {

        // Given
        GeneralBlock generalBlock = new GeneralBlock(TextBlock.BLOCK_ID_4, "info\nabc\n-");

        // When
        TextBlock block = TextBlock.of(generalBlock);

        // Then
        assertThat(block).isNotNull();
        assertThat(block.getInfoLine()).hasValue("info");
        assertThat(block.getText()).isEqualTo("abc\n-");
    }

    @Test
    public void of_WHEN_valid_block_is_passed_RETURN_new_block() throws Exception {

        // Given
        GeneralBlock generalBlock = new GeneralBlock(TextBlock.BLOCK_ID_4, "\nabc\n-");

        // When
        TextBlock block = TextBlock.of(generalBlock);

        // Then
        assertThat(block).isNotNull();
        assertThat(block.getInfoLine()).isNotPresent();
        assertThat(block.getText()).isEqualTo("abc\n-");
    }

    @Test
    public <T> void of_WHEN_block_with_invalid_id_is_passed_THROW_exception() throws Exception {

        // Given
        GeneralBlock generalBlock = new GeneralBlock("0", "\nabc\n-");

        // When
        Throwable exception = catchThrowable(() -> TextBlock.of(generalBlock));

        // Then
        assertThat(exception).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public <T> void of_WHEN_block_with_invalid_ending_is_passed_THROW_exception() throws Exception {

        // Given
        GeneralBlock generalBlock = new GeneralBlock(TextBlock.BLOCK_ID_4, "\nabc");

        // When
        Throwable exception = catchThrowable(() -> TextBlock.of(generalBlock));

        // Then
        assertThat(exception).isInstanceOf(BlockFieldParseException.class);
    }

}