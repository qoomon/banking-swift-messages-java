package com.qoomon.banking.swift.mt;

import com.qoomon.banking.swift.field.MTField;
import com.qoomon.banking.swift.field.SwiftMTFieldParser;
import com.qoomon.banking.swift.field.SwiftMTFieldParserException;
import org.assertj.core.api.SoftAssertions;
import org.junit.Test;

import java.io.StringReader;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Created by qoomon on 27/06/16.
 */
public class SwiftMTFieldParserTest {

    private SoftAssertions softly = new SoftAssertions();

    private SwiftMTFieldParser classUnderTest = new SwiftMTFieldParser();

    @Test
    public void parse_WHEN_valid_message_text_THEN_return_fields() throws Exception {

        // Given
        String swiftMessage = ":1:fizz\n:2:buzz";

        // When
        List<MTField> fieldList = classUnderTest.parse(new StringReader(swiftMessage));

        // Then
        assertThat(fieldList).hasSize(2);
        softly.assertThat(fieldList.get(0).getTag()).isEqualTo("1");
        softly.assertThat(fieldList.get(0).getContent()).isEqualTo("fizz");;
        softly.assertThat(fieldList.get(1).getTag()).isEqualTo("2");;
        softly.assertThat(fieldList.get(1).getContent()).isEqualTo("buzz");
        softly.assertAll();

    }

    @Test
    public void parse_WHEN_detecting_multiline_fields_THEN_return_fields_with_joined_content() throws Exception {

        // Given
        String swiftMessage = ":1:fizz\n:2:multi\r\nline";

        // When
        List<MTField> fieldList = classUnderTest.parse(new StringReader(swiftMessage));

        // Then
        assertThat(fieldList).hasSize(2);
        softly.assertThat(fieldList.get(0).getTag()).isEqualTo("1");
        softly.assertThat(fieldList.get(0).getContent()).isEqualTo("fizz");
        softly.assertThat(fieldList.get(1).getTag()).isEqualTo("2");
        softly.assertThat(fieldList.get(1).getContent()).isEqualTo("multi\nline");
        softly.assertAll();

    }

    @Test
    public void parse_WHEN_detecting_content_without_field_tag_THEN_throw_exception() throws Exception {

        // Given
        String swiftMessage = "fizz\n:2:buzz";

        // When
        Throwable exception = catchThrowable(() -> classUnderTest.parse(new StringReader(swiftMessage)));

        // Then
        assertThat(exception).as("Exception").isInstanceOf(SwiftMTFieldParserException.class);

        SwiftMTFieldParserException parseException = (SwiftMTFieldParserException) exception;
        assertThat(parseException.getLineNumber()).isEqualTo(1);

    }

}