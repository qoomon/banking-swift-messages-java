package com.qoomon.banking.swift.message.block;

import org.assertj.core.api.SoftAssertions;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;

/**
 * Created by qoomon on 14/07/16.
 */
public class BasicHeaderBlockTest {

    @Test
    public void of_WHEN_valid_block_is_passed_RETURN_new_block() throws Exception {

        // Given
        GeneralBlock generalBlock = new GeneralBlock(BasicHeaderBlock.BLOCK_ID_1, "F01YOURCODEZABC2222777777");

        // When
        BasicHeaderBlock block = BasicHeaderBlock.of(generalBlock);

        // Then
        assertThat(block).isNotNull();
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(block.getApplicationId()).isEqualTo("F");
        softly.assertThat(block.getServiceId()).isEqualTo("01");
        softly.assertThat(block.getLogicalTerminalAddress()).isEqualTo("YOURCODEZABC");
        softly.assertThat(block.getSessionNumber()).isEqualTo("2222");
        softly.assertThat(block.getSequenceNumber()).isEqualTo("777777");
        softly.assertAll();
    }

    @Test
    public <T> void of_WHEN_block_with_invalid_id_is_passed_THROW_exception() throws Exception {

        // Given
        GeneralBlock generalBlock = new GeneralBlock("0", "\nabc\n-");

        // When
        Throwable exception = catchThrowable(() -> BasicHeaderBlock.of(generalBlock));

        // Then
        assertThat(exception).isInstanceOf(IllegalArgumentException.class);
    }

}