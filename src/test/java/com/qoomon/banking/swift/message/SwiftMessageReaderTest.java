package com.qoomon.banking.swift.message;

import com.google.common.base.Throwables;
import com.google.common.io.Resources;
import com.qoomon.banking.TestUtils;
import com.qoomon.banking.swift.message.exception.SwiftMessageParseException;
import org.assertj.core.api.ThrowableAssert;
import org.junit.Test;

import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.concurrent.Callable;

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

        final SwiftMessageReader classUnderTest = new SwiftMessageReader(new StringReader(swiftMessageText));

        // When
        Throwable exception = catchThrowable(new ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                classUnderTest.read();
            }
        });
        // Then
        assertThat(exception).as("Exception").isInstanceOf(SwiftMessageParseException.class);

        SwiftMessageParseException parseException = (SwiftMessageParseException) exception;
        assertThat(parseException.getLineNumber()).isEqualTo(1);
    }

    @Test
    public void parse_SHOULD_read_multiple_messages() throws Exception {

        // Given
        String swiftMessageText = ""
                + BLOCK_1_DUMMY_VALID + BLOCK_2_DUMMY_VALID + BLOCK_3_DUMMY_VALID
                + BLOCK_4_DUMMY_EMPTY + BLOCK_5_DUMMY_EMPTY
                + BLOCK_1_DUMMY_VALID + BLOCK_2_DUMMY_VALID + BLOCK_3_DUMMY_VALID
                + BLOCK_4_DUMMY_EMPTY + BLOCK_5_DUMMY_EMPTY;

        final SwiftMessageReader classUnderTest = new SwiftMessageReader(new StringReader(swiftMessageText));

        // When
        List<SwiftMessage> messageList = TestUtils.collectUntilNull(new Callable<SwiftMessage>() {
            @Override
            public SwiftMessage call() throws Exception {
                return classUnderTest.read();
            }
        });

        // Then
        assertThat(messageList).hasSize(2);
    }


    @Test
    public void parse_WHEN_first_bracket_is_missing_THEN_throw_exception() throws Exception {

        // Given
        String swiftMessageText = "1:}" + BLOCK_2_DUMMY_VALID + BLOCK_3_DUMMY_VALID + BLOCK_4_DUMMY_EMPTY
                + BLOCK_5_DUMMY_EMPTY;

        final SwiftMessageReader classUnderTest = new SwiftMessageReader(new StringReader(swiftMessageText));

        // When
        Throwable exception = catchThrowable(new ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                classUnderTest.read();
            }
        });

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

        final SwiftMessageReader classUnderTest = new SwiftMessageReader(new StringReader(swiftMessageText));

        // When
        Throwable exception = catchThrowable(new ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                classUnderTest.read();
            }
        });

        // Then
        assertThat(exception).as("Exception").isInstanceOf(SwiftMessageParseException.class);

        SwiftMessageParseException parseException = (SwiftMessageParseException) exception;
        assertThat(parseException.getLineNumber()).isEqualTo(1);

    }

    @Test
    public void parse_WHEN_message_is_not_finished_THEN_throw_exception() throws Exception {

        // Given
        String swiftMessageText = BLOCK_1_DUMMY_VALID + BLOCK_2_DUMMY_VALID;

        final SwiftMessageReader classUnderTest = new SwiftMessageReader(new StringReader(swiftMessageText));

        // When
        Throwable exception = catchThrowable(new ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                classUnderTest.read();
            }
        });

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

        final SwiftMessageReader classUnderTest = new SwiftMessageReader(new StringReader(swiftMessageText));

        // When
        Throwable exception = catchThrowable(new ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                classUnderTest.read();
            }
        });

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

        final SwiftMessageReader classUnderTest = new SwiftMessageReader(new StringReader(swiftMessageText));

        // When
        Throwable exception = catchThrowable(new ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                classUnderTest.read();
            }
        });

        // Then
        assertThat(exception).as("Exception").isInstanceOf(SwiftMessageParseException.class);

        SwiftMessageParseException parseException = (SwiftMessageParseException) exception;
        assertThat(parseException.getLineNumber()).isEqualTo(2);

    }

    @Test
    public void parse_SHOULD_parse_valid_files() throws Exception {

        // Given
        URL mt940_valid_folder = Resources.getResource("swiftmessage");

        // When
        final int[] errors = {0};
        Files.walkFileTree(Paths.get(mt940_valid_folder.toURI()), new SimpleFileVisitor<Path>(){
            @Override
            public FileVisitResult visitFile(Path filePath, BasicFileAttributes attrs) throws IOException {

                if( !attrs.isRegularFile() ){
                    return FileVisitResult.CONTINUE;
                }

                try {
                    System.out.println(filePath);
                    SwiftMessageReader classUnderTest = new SwiftMessageReader(new FileReader(filePath.toFile()));
                    classUnderTest.read();
                } catch (Exception e) {
                    System.out.println(Throwables.getStackTraceAsString(e));
                    errors[0]++;
                }
                return FileVisitResult.CONTINUE;
            }
        });


        // Then
        assertThat(errors[0]).isEqualTo(0);
    }

    @Test
    public void getContent_SHOULD_return_input_text() throws Exception {

        // Given
        String contentInput = ""
                + BLOCK_1_DUMMY_VALID + BLOCK_2_DUMMY_VALID + BLOCK_3_DUMMY_VALID
                + BLOCK_4_DUMMY_EMPTY + BLOCK_5_DUMMY_EMPTY;
        final SwiftMessageReader messageReader = new SwiftMessageReader(new StringReader(contentInput));
        SwiftMessage classUnderTest = TestUtils.collectUntilNull(new Callable<SwiftMessage>() {
            @Override
            public SwiftMessage call() throws Exception {
                return messageReader.read();
            }
        }).get(0);

        // When
        String content = classUnderTest.getContent();

        // Then
        assertThat(content).isEqualTo(contentInput);
    }

}