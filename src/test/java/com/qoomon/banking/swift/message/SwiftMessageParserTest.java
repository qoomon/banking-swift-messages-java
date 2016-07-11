package com.qoomon.banking.swift.message;

import com.google.common.io.Resources;
import com.qoomon.banking.swift.message.exception.SwiftMessageParseException;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.ThrowableAssert;
import org.assertj.core.api.ThrowableAssertAlternative;
import org.assertj.core.api.ThrowableTypeAssert;
import org.junit.Test;

import java.io.FileReader;
import java.io.StringReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

/**
 * Created by qoomon on 24/06/16.
 */
public class SwiftMessageParserTest {

    private static final String BLOCK_1_DUMMY_VALID = "{1:F01YOURCODEZABC1234567890}";
    private static final String BLOCK_2_DUMMY_VALID = "{2:O1001200970103BANKBEBBAXXX22221234569701031201N}";
    private static final String BLOCK_3_DUMMY_VALID = "{3:{113:SEPA}{108:ILOVESEPA}}";
    private static final String BLOCK_4_DUMMY_EMPTY = "{4:\n-}";
    private static final String BLOCK_5_DUMMY_EMPTY = "{5:}";

    private SoftAssertions softly = new SoftAssertions();

    private SwiftMessageParser classUnderTest = new SwiftMessageParser();

    @Test
    public void parse_WHEN_detecting_whitespaces_between_blocks_THEN_throw_exception() throws Exception {

        // Given
        String swiftMessageText = BLOCK_1_DUMMY_VALID + BLOCK_2_DUMMY_VALID + BLOCK_3_DUMMY_VALID + " "
                + BLOCK_4_DUMMY_EMPTY
                + BLOCK_5_DUMMY_EMPTY;

        // When
        Throwable exception = catchThrowable(() -> classUnderTest.parse(new StringReader(swiftMessageText)));
        // Then
        assertThat(exception).as("Exception").isInstanceOf(SwiftMessageParseException.class);

        SwiftMessageParseException parseException = (SwiftMessageParseException) exception;
        assertThat(parseException.getLineNumber()).isEqualTo(1);
    }

    @Test
    public void parse_WHEN_block4_has_wrong_termination_THEN_throw_exception() throws Exception {

        // Given
        String swiftMessageText = BLOCK_1_DUMMY_VALID + BLOCK_2_DUMMY_VALID + BLOCK_3_DUMMY_VALID + "{4:\n"
                + "}"
                + BLOCK_5_DUMMY_EMPTY;

        // When
        Throwable exception = catchThrowable(() -> classUnderTest.parse(new StringReader(swiftMessageText)));

        // Then
        assertThat(exception).as("Exception").isInstanceOf(SwiftMessageParseException.class);

        SwiftMessageParseException parseException = (SwiftMessageParseException) exception;
        assertThat(parseException.getLineNumber()).isEqualTo(2);
    }


    @Test
    public void parse_WHEN_first_bracket_is_missing_THEN_throw_exception() throws Exception {

        // Given
        String swiftMessageText = "1:}" + BLOCK_2_DUMMY_VALID + BLOCK_3_DUMMY_VALID + BLOCK_4_DUMMY_EMPTY
                + BLOCK_5_DUMMY_EMPTY;

        // When
        Throwable exception = catchThrowable(() -> classUnderTest.parse(new StringReader(swiftMessageText)));

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

        // When
        Throwable exception = catchThrowable(() -> classUnderTest.parse(new StringReader(swiftMessageText)));

        // Then
        assertThat(exception).as("Exception").isInstanceOf(SwiftMessageParseException.class);

        SwiftMessageParseException parseException = (SwiftMessageParseException) exception;
        assertThat(parseException.getLineNumber()).isEqualTo(1);

    }

    @Test
    public void parse_WHEN_block_appears_multiple_times_THEN_throw_exception() throws Exception {

        // Given
        String swiftMessageText = BLOCK_1_DUMMY_VALID + BLOCK_2_DUMMY_VALID + BLOCK_3_DUMMY_VALID + BLOCK_4_DUMMY_EMPTY
                + BLOCK_5_DUMMY_EMPTY + "{1:}";

        // When
        Throwable exception = catchThrowable(() -> classUnderTest.parse(new StringReader(swiftMessageText)));

        // Then
        assertThat(exception).as("Exception").isInstanceOf(SwiftMessageParseException.class);

        SwiftMessageParseException parseException = (SwiftMessageParseException) exception;
        assertThat(parseException.getLineNumber()).isEqualTo(2);

    }


    @Test
    public void parse_WHEN_unknown_block_appears_THEN_throw_exception() throws Exception {

        // Given
        String swiftMessageText = BLOCK_1_DUMMY_VALID + BLOCK_2_DUMMY_VALID + BLOCK_3_DUMMY_VALID + BLOCK_4_DUMMY_EMPTY
                + BLOCK_5_DUMMY_EMPTY + "{6:}";

        // When
        Throwable exception = catchThrowable(() -> classUnderTest.parse(new StringReader(swiftMessageText)));

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

        // When
        Throwable exception = catchThrowable(() -> classUnderTest.parse(new StringReader(swiftMessageText)));

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
        files.forEach(filePath -> {
            try {
                System.out.println(filePath);
                classUnderTest.parse(new FileReader(filePath.toFile()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        // Then
        // No Exception

    }

}