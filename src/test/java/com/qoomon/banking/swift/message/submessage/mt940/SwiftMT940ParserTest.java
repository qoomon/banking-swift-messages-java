package com.qoomon.banking.swift.message.submessage.mt940;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.io.Resources;
import com.qoomon.banking.swift.message.submessage.field.PageSeperator;
import org.assertj.core.api.SoftAssertions;
import org.joda.money.CurrencyUnit;
import org.junit.Test;

import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

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
        URL mt940MessageUrl = Resources.getResource("submessage/mt940_valid/valid-mt940-content.txt");
        String mt940MessageText = Resources.toString(mt940MessageUrl, Charsets.UTF_8);

        // When
        List<SwiftMT940> mt940MessageList = classUnderTest.parse(new StringReader(mt940MessageText));

        // Then
        assertThat(mt940MessageList).hasSize(1);
        assertThat(mt940MessageList.get(0).getTransactionGroupList()).hasSize(3);
    }

    @Test
    public void parse_SHOULD_parse_valid_files() throws Exception {

        // Given
        URL mt940_valid_folder = Resources.getResource("submessage/mt940_valid");
        Stream<Path> files = Files.walk(Paths.get(mt940_valid_folder.toURI())).filter(path -> Files.isRegularFile(path));

        // When
        final int[] errors = {0};
        files.forEach(filePath -> {
            try {
                classUnderTest.parse(new FileReader(filePath.toFile()));
            } catch (Exception e) {
                System.out.println(filePath);
                System.out.println(Throwables.getStackTraceAsString(e));
                System.out.println();
                errors[0]++;
            }
        });

        // Then
        assertThat(errors[0]).isEqualTo(0);

    }
}