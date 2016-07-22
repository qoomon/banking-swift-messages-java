package com.qoomon.banking.swift.message.block;

import com.qoomon.banking.swift.submessage.field.subfield.MessagePriority;
import org.assertj.core.api.SoftAssertions;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;


public class ApplicationHeaderBlockTest {

    @Test
    public void of_WHEN_valid_output_block_is_passed_RETURN_new_block() throws Exception {

        // Given
        GeneralBlock generalBlock = new GeneralBlock(ApplicationHeaderBlock.BLOCK_ID_2, "O9401506110804LRLRXXXX4A1100009040831108041707N");

        // When
        ApplicationHeaderBlock block = ApplicationHeaderBlock.of(generalBlock);

        // Then
        assertThat(block).isNotNull();
        assertThat(block.getOutput()).isPresent();
        if (block.getOutput().isPresent()) {
            SoftAssertions softly = new SoftAssertions();
            ApplicationHeaderOutputBlock outputBlock = block.getOutput().get();
            softly.assertThat(outputBlock.getMessageType()).isEqualTo("940");
            softly.assertThat(outputBlock.getInputDateTime()).isEqualTo(LocalDateTime.of(2011, 8, 4, 15, 6));
            softly.assertThat(outputBlock.getInputReference()).isEqualTo("LRLRXXXX4A11");
            softly.assertThat(outputBlock.getSessionNumber()).isEqualTo("0000");
            softly.assertThat(outputBlock.getSequenceNumber()).isEqualTo("904083");
            softly.assertThat(outputBlock.getOutputDateTime()).isEqualTo(LocalDateTime.of(2011, 8, 4, 17, 7));
            softly.assertThat(outputBlock.getMessagePriority()).isEqualTo(MessagePriority.NORMAL);
            softly.assertAll();
        }

        assertThat(block.getInput()).isNotPresent();
    }


    @Test
    public void of_WHEN_valid_input_block_is_passed_RETURN_new_block() throws Exception {

        // Given
        GeneralBlock generalBlock = new GeneralBlock(ApplicationHeaderBlock.BLOCK_ID_2, "I101YOURBANKXJKLU3003");

        // When
        ApplicationHeaderBlock block = ApplicationHeaderBlock.of(generalBlock);

        // Then
        assertThat(block).isNotNull();
        assertThat(block.getInput()).isPresent();
        if (block.getInput().isPresent()) {
            SoftAssertions softly = new SoftAssertions();
            ApplicationHeaderInputBlock inputBlock = block.getInput().get();
            softly.assertThat(inputBlock.getMessageType()).isEqualTo("101");
            softly.assertThat(inputBlock.getReceiverAddress()).isEqualTo("YOURBANKXJKL");
            softly.assertThat(inputBlock.getMessagePriority()).isEqualTo(MessagePriority.URGENT);
            softly.assertThat(inputBlock.getDeliveryMonitoring()).contains("3");
            softly.assertThat(inputBlock.getObsolescencePeriod()).contains("003");
            softly.assertAll();
        }

        assertThat(block.getOutput()).isNotPresent();
    }

    @Test
    public void of_WHEN_block_with_invalid_id_is_passed_THROW_exception() throws Exception {

        // Given
        GeneralBlock generalBlock = new GeneralBlock("0", "\nabc\n-");

        // When
        Throwable exception = catchThrowable(() -> ApplicationHeaderBlock.of(generalBlock));

        // Then
        assertThat(exception).isInstanceOf(IllegalArgumentException.class);
    }

}