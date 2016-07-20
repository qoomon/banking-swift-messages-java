package com.qoomon.banking.swift.notation;

import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Created by qoomon on 29/06/16.
 */
public class SwiftNotationTest {

    @Test
    public void parse() throws Exception {
        // Given

        String swiftFieldNotation = "1!a6!n3!a15d"; // Tag 60a – Opening Balance

        String fieldText = "A" + "123456" + "ABC" + "1234,";

        // When

        List<String> fieldValueList = new SwiftNotation(swiftFieldNotation).parse(fieldText);

        // Then

        assertThat(fieldValueList).hasSize(4);
        assertThat(fieldValueList.get(0)).isEqualTo("A");
        assertThat(fieldValueList.get(1)).isEqualTo("123456");
        assertThat(fieldValueList.get(2)).isEqualTo("ABC");
        assertThat(fieldValueList.get(3)).isEqualTo("1234,");
    }

    @Test
    public void parse_SHOULD_accept_multiline_subfields() throws Exception {

        // Given

        String swiftFieldNotation = "3*5a"; // Tag 60a – Opening Balance

        String fieldText = "A\nAA\nAAA";

        // When

        List<String> fieldValueList = new SwiftNotation(swiftFieldNotation).parse(fieldText);

        // Then

        assertThat(fieldValueList).hasSize(1);
        assertThat(fieldValueList.get(0)).isEqualTo(fieldText);

    }

    @Test
    public void parse_SHOULD_ignore_field_separators() throws Exception {

        // Given

        String swiftFieldNotation = "5n[/5n]";

        String fieldText = "123/11";

        // When

        List<String> fieldValueList = new SwiftNotation(swiftFieldNotation).parse(fieldText);

        // Then

        assertThat(fieldValueList).hasSize(2);
        assertThat(fieldValueList.get(0)).isEqualTo("123");
        assertThat(fieldValueList.get(1)).isEqualTo("11");

    }

}