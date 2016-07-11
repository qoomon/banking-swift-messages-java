package com.qoomon.banking.swift.message.submessage.mt942;


import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.assertj.core.api.SoftAssertions;
import org.junit.Test;

import java.io.FileReader;
import java.io.StringReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

/**
 * Created by qoomon on 05/07/16.
 */
public class SwiftMT942ParserTest {

    private SoftAssertions softly = new SoftAssertions();

    private SwiftMT942Parser classUnderTest = new SwiftMT942Parser();

    @Test
    public void parse() throws Exception {

        // Given
        URL mt940MessageUrl = Resources.getResource("submessage/mt942_valid/valid-mt942-content.txt");
        String mt920MessageText = Resources.toString(mt940MessageUrl, Charsets.UTF_8);

        // When
        List<SwiftMT942> mt942MessageList = classUnderTest.parse(new StringReader(mt920MessageText));

        // Then
        assertThat(mt942MessageList).hasSize(1);
        SwiftMT942 swiftMT942 = mt942MessageList.get(0);
        assertThat(swiftMT942.getTransactionList()).hasSize(3);
        assertThat(swiftMT942.getStatementNumber().getValue()).isEqualTo("1");
        assertThat(swiftMT942.getStatementNumber().getSequenceNumber()).contains("1");
    }

    @Test
    public void parse_SHOULD_parse_valid_files() throws Exception {

        // Given
        URL mt942_valid_folder = Resources.getResource("submessage/mt942_valid");
        Stream<Path> files = Files.walk(Paths.get(mt942_valid_folder.toURI())).filter(path -> Files.isRegularFile(path));

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