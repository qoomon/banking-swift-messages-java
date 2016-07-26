package com.qoomon.banking.swift.submessage.field;

import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Created by qoomon on 25/07/16.
 */
public class BCSMessageParserTest {

    @Test
    public void parse_SHOULD_parse_palid_message() throws Exception {
        // Given
        String messageText = "835?20foo?36bar";

        BCSMessageParser subjectUnderTest = new BCSMessageParser();

        // When
        BCSMessage message = subjectUnderTest.parseMessage(messageText);

        // Then
        assertThat(message).isNotNull();
        assertThat(message.getBusinessTransactionCode()).isEqualTo("835");
        assertThat(message.getFieldMap())
                .containsEntry("20", "foo")
                .containsEntry("36", "bar")
                .hasSize(2);
    }


}