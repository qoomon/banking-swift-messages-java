package com.qoomon.banking.swift.mt.mt940;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.assertj.core.api.SoftAssertions;
import org.junit.Test;

import java.io.StringReader;
import java.net.URL;
import java.util.List;

import static org.assertj.core.api.Assertions.*;


/**
 * Created by qoomon on 27/06/16.
 */
public class SwiftMT940ParserTest {

    private SoftAssertions softly = new SoftAssertions();

    private SwiftMT940Parser classUnderTest = new SwiftMT940Parser();

    @Test
    public void parse() throws Exception {

        // Given
        URL mt940MessageUrl = Resources.getResource("valid-mt940-content.txt");
        String mt940MessageText = Resources.toString(mt940MessageUrl, Charsets.UTF_8);

        // When
        List<SwiftMT940> mt940MessageList = classUnderTest.parse(new StringReader(mt940MessageText));

        // Then
        assertThat(mt940MessageList).hasSize(1);
        assertThat(mt940MessageList.get(0).getTransactionGroupList()).hasSize(3);
    }
}