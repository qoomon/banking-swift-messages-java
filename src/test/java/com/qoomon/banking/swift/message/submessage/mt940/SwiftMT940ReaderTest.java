package com.qoomon.banking.swift.message.submessage.mt940;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.io.Resources;
import com.qoomon.banking.swift.TestUtils;
import com.qoomon.banking.swift.message.submessage.field.PageSeperator;
import org.assertj.core.api.SoftAssertions;
import org.joda.money.CurrencyUnit;
import org.junit.Ignore;
import org.junit.Test;

import java.io.FileReader;
import java.io.StringReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;


/**
 * Created by qoomon on 27/06/16.
 */
public class SwiftMT940ReaderTest {


    @Test
    public void parse_WHEN_parse_valid_file_RETURN_message() throws Exception {

        // Given
        String mt940MessageText = ":20:02618\n" +
                ":21:123456/DEV\n" +
                ":25:6-9412771\n" +
                ":28C:00102\n" +
                ":60F:C000103USD672,\n" +
                ":61:0312091209D880,FTRFREF:BPHPBK/081203/0003//59512092915002\n" +
                ":86:multiline info\n" +
                "info\n" +
                ":61:0312091209D880,FTRFREF:BPHPBK/081203/0003//59512092915002\n" +
                ":86:singleline info\n" +
                ":61:0312091209D880,FTRFREF:BPHPBK/081203/0003//59512092915002\n" +
                ":62F:C000103USD987,\n" +
                ":86:multiline summary\n" +
                "summary\n" +
                "-";

        SwiftMT940Reader classUnderTest = new SwiftMT940Reader(new StringReader(mt940MessageText));

        // When
        List<SwiftMT940> mt940MessageList = TestUtils.collectAll(classUnderTest::readMessage);

        // Then
        assertThat(mt940MessageList).hasSize(1);
        SwiftMT940 swiftMT940 = mt940MessageList.get(0);
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(swiftMT940.getTransactionGroupList()).hasSize(3);
        softly.assertThat(swiftMT940.getTransactionGroupList()).hasSize(3);
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
                SwiftMT940Reader classUnderTest = new SwiftMT940Reader(new FileReader(filePath.toFile()));
                TestUtils.collectAll(classUnderTest::readMessage);
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