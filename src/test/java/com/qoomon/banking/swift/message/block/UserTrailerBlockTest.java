package com.qoomon.banking.swift.message.block;

import org.assertj.core.api.SoftAssertions;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;

/**
 * Created by qoomon on 14/07/16.
 */
public class UserTrailerBlockTest {

    @Test
    public void of_WHEN_valid_block_is_passed_RETURN_new_block() throws Exception {

        // Given
        GeneralBlock generalBlock = new GeneralBlock(UserTrailerBlock.BLOCK_ID_5, "{CHK:F7C4F89AF66D}{TNG:}");

        // When
        UserTrailerBlock block = UserTrailerBlock.of(generalBlock);

        // Then
        assertThat(block).isNotNull();
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(block.getChecksum()).contains("F7C4F89AF66D");
        softly.assertThat(block.getTraining()).contains("");
        softly.assertAll();
    }

    @Test
    public <T> void of_WHEN_block_with_invalid_id_is_passed_THROW_exception() throws Exception {

        // Given
        GeneralBlock generalBlock = new GeneralBlock("0", "\nabc\n-");

        // When
        Throwable exception = catchThrowable(() -> UserTrailerBlock.of(generalBlock));

        // Then
        assertThat(exception).isInstanceOf(IllegalArgumentException.class);
    }

}