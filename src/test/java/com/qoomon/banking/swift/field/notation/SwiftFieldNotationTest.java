package com.qoomon.banking.swift.field.notation;

import org.assertj.core.data.Index;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Created by qoomon on 29/06/16.
 */
public class SwiftFieldNotationTest {

    @Test
    public void parse() throws Exception {
        // Given

        String swiftFieldNotation = "1!a6!n3!a15d"; // Tag 60a – Opening Balance

        String fieldText = "A" + "123456" + "ABC" + "1234,";

        // When

        List<String> fieldValueList = new SwiftFieldNotation(swiftFieldNotation).parse(fieldText);

        // Then

        assertThat(fieldValueList).hasSize(4)
                .contains("A", Index.atIndex(0))
                .contains("123456", Index.atIndex(1))
                .contains("ABC", Index.atIndex(2))
                .contains("1234,", Index.atIndex(3));


    }

    @Test
    public void parse_SCHOULD_accept_multiline_subfield() throws Exception {

        // Given

        String swiftFieldNotation = "3*5a"; // Tag 60a – Opening Balance

        String fieldText = "A\nAA\nAAA";

        // When

        List<String> fieldValueList = new SwiftFieldNotation(swiftFieldNotation).parse(fieldText);

        // Then

        assertThat(fieldValueList).hasSize(1)
                .contains(fieldText, Index.atIndex(0));

    }
}