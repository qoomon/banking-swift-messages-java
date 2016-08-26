package com.qoomon.banking.swift.message.block;

import org.assertj.core.api.SoftAssertions;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;

/**
 * Created by qoomon on 14/07/16.
 */
public class UserHeaderBlockTest {

    @Test
    public void of_WHEN_valid_block_is_passed_RETURN_new_block() throws Exception {

        // Given
        GeneralBlock generalBlock = new GeneralBlock(UserHeaderBlock.BLOCK_ID_3, "{113:SEPA}{108:ILOVESEPA}");

        // When
        UserHeaderBlock block = UserHeaderBlock.of(generalBlock);

        // Then
        assertThat(block).isNotNull();
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(block.getBankingPriorityCode()).contains("SEPA");
        softly.assertThat(block.getMessageUserReference()).contains("ILOVESEPA");
        softly.assertAll();
    }

    @Test
    public <T> void of_WHEN_block_with_invalid_id_is_passed_THROW_exception() throws Exception {

        // Given
        GeneralBlock generalBlock = new GeneralBlock("0", "\nabc\n-");

        // When
        Throwable exception = catchThrowable(() -> UserHeaderBlock.of(generalBlock));

        // Then
        assertThat(exception).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void getContent_SHOULD_return_input_text() throws Exception {

        // Given
        String contentInput = "{113:SEPA}{108:ILOVESEPA}";
        GeneralBlock generalBlock = new GeneralBlock(UserHeaderBlock.BLOCK_ID_3, contentInput);
        UserHeaderBlock classUnderTest = UserHeaderBlock.of(generalBlock);

        // When
        String content = classUnderTest.getContent();

        // Then
        assertThat(content).isEqualTo(contentInput);
    }

}