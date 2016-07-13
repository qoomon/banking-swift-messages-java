package com.qoomon.banking.swift.message;

import com.google.common.base.Throwables;
import com.google.common.io.Resources;
import com.qoomon.banking.swift.message.exception.SwiftMessageParseException;
import org.junit.Test;

import java.io.FileReader;
import java.io.StringReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

/**
 * Created by qoomon on 24/06/16.
 */
public class SwiftMessageReaderTest {

    private static final String BLOCK_1_DUMMY_VALID = "{1:F01YOURCODEZABC1234567890}";
    private static final String BLOCK_2_DUMMY_VALID = "{2:O1001200970103BANKBEBBAXXX22221234569701031201N}";
    private static final String BLOCK_3_DUMMY_VALID = "{3:{113:SEPA}{108:ILOVESEPA}}";
    private static final String BLOCK_4_DUMMY_EMPTY = "{4:\n-}";
    private static final String BLOCK_5_DUMMY_EMPTY = "{5:}";


    @Test
    public void parse_WHEN_detecting_whitespaces_between_blocks_THEN_throw_exception() throws Exception {

        // Given
        String swiftMessageText = BLOCK_1_DUMMY_VALID + BLOCK_2_DUMMY_VALID + BLOCK_3_DUMMY_VALID + " "
                + BLOCK_4_DUMMY_EMPTY
                + BLOCK_5_DUMMY_EMPTY;

        SwiftMessageReader classUnderTest = new SwiftMessageReader(new StringReader(swiftMessageText));

        // When
        Throwable exception = catchThrowable(() -> classUnderTest.readMessage());
        // Then
        assertThat(exception).as("Exception").isInstanceOf(SwiftMessageParseException.class);

        SwiftMessageParseException parseException = (SwiftMessageParseException) exception;
        assertThat(parseException.getLineNumber()).isEqualTo(1);
    }


    @Test
    public void parse_WHEN_first_bracket_is_missing_THEN_throw_exception() throws Exception {

        // Given
        String swiftMessageText = "1:}" + BLOCK_2_DUMMY_VALID + BLOCK_3_DUMMY_VALID + BLOCK_4_DUMMY_EMPTY
                + BLOCK_5_DUMMY_EMPTY;

        SwiftMessageReader classUnderTest = new SwiftMessageReader(new StringReader(swiftMessageText));

        // When
        Throwable exception = catchThrowable(classUnderTest::readMessage);

        // Then
        assertThat(exception).as("Exception").isInstanceOf(SwiftMessageParseException.class);

        SwiftMessageParseException parseException = (SwiftMessageParseException) exception;
        assertThat(parseException.getLineNumber()).isEqualTo(1);

    }

    @Test
    public void parse_WHEN_block_structure_is_wrong_THEN_throw_exception() throws Exception {

        // Given
        String swiftMessageText = "{:1:}" + BLOCK_2_DUMMY_VALID + BLOCK_3_DUMMY_VALID + BLOCK_4_DUMMY_EMPTY
                + BLOCK_5_DUMMY_EMPTY;

        SwiftMessageReader classUnderTest = new SwiftMessageReader(new StringReader(swiftMessageText));

        // When
        Throwable exception = catchThrowable(classUnderTest::readMessage);

        // Then
        assertThat(exception).as("Exception").isInstanceOf(SwiftMessageParseException.class);

        SwiftMessageParseException parseException = (SwiftMessageParseException) exception;
        assertThat(parseException.getLineNumber()).isEqualTo(1);

    }

    @Test
    public void parse_WHEN_message_is_not_finished_THEN_throw_exception() throws Exception {

        // Given
        String swiftMessageText = BLOCK_1_DUMMY_VALID + BLOCK_2_DUMMY_VALID;

        SwiftMessageReader classUnderTest = new SwiftMessageReader(new StringReader(swiftMessageText));

        // When
        Throwable exception = catchThrowable(classUnderTest::readMessage);

        // Then
        assertThat(exception).as("Exception").isInstanceOf(SwiftMessageParseException.class);

        SwiftMessageParseException parseException = (SwiftMessageParseException) exception;
        assertThat(parseException.getLineNumber()).isEqualTo(1);

    }


    @Test
    public void parse_WHEN_unknown_block_appears_THEN_throw_exception() throws Exception {

        // Given
        String swiftMessageText = BLOCK_1_DUMMY_VALID + BLOCK_2_DUMMY_VALID + BLOCK_3_DUMMY_VALID + BLOCK_4_DUMMY_EMPTY
                + BLOCK_5_DUMMY_EMPTY + "{6:}";

        SwiftMessageReader classUnderTest = new SwiftMessageReader(new StringReader(swiftMessageText));

        // When
        Throwable exception = catchThrowable(classUnderTest::readMessage);

        // Then
        assertThat(exception).as("Exception").isInstanceOf(SwiftMessageParseException.class);

        SwiftMessageParseException parseException = (SwiftMessageParseException) exception;
        assertThat(parseException.getLineNumber()).isEqualTo(2);

    }

    @Test
    public void parse_WHEN_brackets_are_unbalanced_THEN_throw_exception() throws Exception {

        // Given
        String swiftMessageText = BLOCK_1_DUMMY_VALID + BLOCK_2_DUMMY_VALID + BLOCK_3_DUMMY_VALID + BLOCK_4_DUMMY_EMPTY
                + "{5:";

        SwiftMessageReader classUnderTest = new SwiftMessageReader(new StringReader(swiftMessageText));

        // When
        Throwable exception = catchThrowable(classUnderTest::readMessage);

        // Then
        assertThat(exception).as("Exception").isInstanceOf(SwiftMessageParseException.class);

        SwiftMessageParseException parseException = (SwiftMessageParseException) exception;
        assertThat(parseException.getLineNumber()).isEqualTo(2);

    }

    @Test
    public void parse_SHOULD_parse_valid_files() throws Exception {

        // Given
        URL mt940_valid_folder = Resources.getResource("swiftmessage");
        Stream<Path> files = Files.walk(Paths.get(mt940_valid_folder.toURI())).filter(path -> Files.isRegularFile(path));

        // When
        final int[] errors = {0};
        files.forEach(filePath -> {
            try {
                System.out.println(filePath);
                SwiftMessageReader classUnderTest = new SwiftMessageReader(new FileReader(filePath.toFile()));
                classUnderTest.readMessage();
            } catch (Exception e) {
                System.out.println(Throwables.getStackTraceAsString(e));
                errors[0]++;
            }
        });

        // Then
        assertThat(errors[0]).isEqualTo(0);
    }

}