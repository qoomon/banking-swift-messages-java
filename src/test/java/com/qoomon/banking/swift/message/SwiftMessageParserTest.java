package com.qoomon.banking.swift.message;

import com.qoomon.banking.swift.message.SwiftMessage;
import com.qoomon.banking.swift.message.SwiftMessageParser;
import com.qoomon.banking.swift.message.exception.SwiftMessageParserException;
import org.assertj.core.api.SoftAssertions;
import org.junit.Test;

import java.io.StringReader;

import static org.assertj.core.api.Assertions.*;

/**
 * Created by qoomon on 24/06/16.
 */
public class SwiftMessageParserTest {

    private SoftAssertions softly = new SoftAssertions();

    private SwiftMessageParser classUnderTest = new SwiftMessageParser();

    @Test
    public void parse_WHEN_detecting_whitespaces_between_blocks_THEN_throw_exception() throws Exception {

        // Given
        String swiftMessageText = "{1:}{2:}{3:} {4:x\nx x   x   -}{5:}";

        // When
        Throwable exception = catchThrowable(() -> classUnderTest.parse(new StringReader(swiftMessageText)));

        // Then
        assertThat(exception).as("Exception").isInstanceOf(SwiftMessageParserException.class);

        SwiftMessageParserException parseException = (SwiftMessageParserException) exception;
        assertThat(parseException.getLineNumber()).isEqualTo(1);
    }

    @Test
    public void parse_WHEN_block4_has_wrong_termination_THEN_throw_exception() throws Exception {

        // Given
        String swiftMessageText = "{1:}{2:}{3:}{4:}{5:}";

        // When
        Throwable exception = catchThrowable(() -> classUnderTest.parse(new StringReader(swiftMessageText)));

        // Then
        assertThat(exception).as("Exception").isInstanceOf(SwiftMessageParserException.class);

        SwiftMessageParserException parseException = (SwiftMessageParserException) exception;
        assertThat(parseException.getLineNumber()).isEqualTo(1);

    }




    @Test
    public void parse_WHEN_first_bracket_is_missing_THEN_throw_exception() throws Exception {

        // Given
        String swiftMessageText = "1:}{2:}{3:}{4:-}{5:}";

        // When
        Throwable exception = catchThrowable(() -> classUnderTest.parse(new StringReader(swiftMessageText)));

        // Then
        assertThat(exception).as("Exception").isInstanceOf(SwiftMessageParserException.class);

        SwiftMessageParserException parseException = (SwiftMessageParserException) exception;
        assertThat(parseException.getLineNumber()).isEqualTo(1);

    }

    @Test
    public void parse_WHEN_block_structure_is_wrong_THEN_throw_exception() throws Exception {

        // Given
        String swiftMessageText = "{:1:}{2:}{3:}{4:-}{5:}";

        // When
        Throwable exception = catchThrowable(() -> classUnderTest.parse(new StringReader(swiftMessageText)));

        // Then
        assertThat(exception).as("Exception").isInstanceOf(SwiftMessageParserException.class);

        SwiftMessageParserException parseException = (SwiftMessageParserException) exception;
        assertThat(parseException.getLineNumber()).isEqualTo(1);

    }

    @Test
    public void parse_WHEN_block_appears_multiple_times_THEN_throw_exception() throws Exception {

        // Given
        String swiftMessageText = "{1:}{2:}{3:}{4:-}{5:}{1:}";

        // When
        Throwable exception = catchThrowable(() -> classUnderTest.parse(new StringReader(swiftMessageText)));

        // Then
        assertThat(exception).as("Exception").isInstanceOf(SwiftMessageParserException.class);

        SwiftMessageParserException parseException = (SwiftMessageParserException) exception;
        assertThat(parseException.getLineNumber()).isEqualTo(1);

    }

    @Test
    public void parse_WHEN_unknown_block_appears_THEN_throw_exception() throws Exception {

        // Given
        String swiftMessageText = "{1:}{2:}{3:}{4:-}{5:}{6:}";

        // When
        Throwable exception = catchThrowable(() -> classUnderTest.parse(new StringReader(swiftMessageText)));

        // Then
        assertThat(exception).as("Exception").isInstanceOf(SwiftMessageParserException.class);

        SwiftMessageParserException parseException = (SwiftMessageParserException) exception;
        assertThat(parseException.getLineNumber()).isEqualTo(1);

    }

    @Test
    public void parse_WHEN_brackets_are_unbalanced_THEN_throw_exception() throws Exception {

        // Given
        String swiftMessageText = "{1:}{2:}{3:}{4:-}{5:";

        // When
        Throwable exception = catchThrowable(() -> classUnderTest.parse(new StringReader(swiftMessageText)));

        // Then
        assertThat(exception).as("Exception").isInstanceOf(SwiftMessageParserException.class);

        SwiftMessageParserException parseException = (SwiftMessageParserException) exception;
        assertThat(parseException.getLineNumber()).isEqualTo(1);

    }


}