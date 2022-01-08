package com.qoomon.banking.swift.submessage.mt940;

import com.google.common.base.Throwables;
import com.google.common.io.Resources;
import com.qoomon.banking.TestUtils;
import com.qoomon.banking.swift.message.exception.SwiftMessageParseException;
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
 * Created by qoomon on 27/06/16.
 */
public class MT940PageReaderTest {


    @Test
    public void parse_WHEN_parse_valid_file_RETURN_message() throws Exception {

        // Given
        String mt940MessageText = ":20:02618\n" +
                ":21:123456/DEV\n" +
                ":25:6-9412771\n" +
                ":28C:00102\n" +
                ":60F:C000103USD672,\n" +
                ":61:0312091211D880,FTRFBPHP/081203/0003//59512112915002\n" +
                "supplementary info\n" +
                ":86:multiline info\n" +
                "-info\n" +
                ":61:0312091211D880,FTRFBPHP/081203/0003//59512112915002\n" +
                ":86:singleline info\n" +
                ":61:0312091211D880,FTRFBPHP/081203/0003//59512112915002\n" +
                ":62F:C000103USD987,\n" +
                ":86:multiline summary\n" +
                "summary\n" +
                "-";

        MT940PageReader classUnderTest = new MT940PageReader(new StringReader(mt940MessageText));

        // When
        List<MT940Page> pageList = TestUtils.collectUntilNull(classUnderTest::read);

        // Then
        assertThat(pageList).hasSize(1);
        MT940Page MT940Page = pageList.get(0);
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(MT940Page.getTransactionGroupList()).hasSize(3);
        softly.assertThat(MT940Page.getTransactionGroupList()).hasSize(3);
    }

    @Test
    public void parse_WHEN_supplementary_details_start_with_colon_THEN_it_is_correctly_parsed() throws Exception {

        // Given
        String mt940MessageText = ":20:02618\n" +
                ":21:123456/DEV\n" +
                ":25:6-9412771\n" +
                ":28C:00102\n" +
                ":60F:C000103USD672,\n" +
                ":61:0312091211D880,FTRFBPHP/081203/0003//59512112915002\n" +
                ":colon is valid x char set\n" +
                ":62F:C000103USD987,\n" +
                "-";

        MT940PageReader classUnderTest = new MT940PageReader(new StringReader(mt940MessageText));

        // When
        List<MT940Page> pageList = TestUtils.collectUntilNull(classUnderTest::read);

        // Then
        assertThat(pageList).hasSize(1);
    }

    @Test
    public void getContent_SHOULD_return_input_text() throws Exception {

        // Given
        String contentInput = ":20:02618\n" +
                ":21:123456/DEV\n" +
                ":25:6-9412771\n" +
                ":28C:00102\n" +
                ":60F:C000103USD672,\n" +
                ":61:0312091211D880,FTRFBPHP/081203/0003//59512112915002\n" +
                ":86:multiline info\n" +
                "-info\n" +
                ":61:0312091211D880,FTRFBPHP/081203/0003//59512112915002\n" +
                ":86:singleline info\n" +
                ":61:0312091211D880,FTRFBPHP/081203/0003//59512112915002\n" +
                ":62F:C000103USD987,\n" +
                ":86:multiline summary\n" +
                "summary\n" +
                "-";
        MT940PageReader pageReader = new MT940PageReader(new StringReader(contentInput));
        MT940Page classUnderTest = TestUtils.collectUntilNull(pageReader::read).get(0);

        // When
        String content = classUnderTest.getContent();

        // Then
        assertThat(content).isEqualTo(contentInput);
    }

    @Test
    public void parse_WHEN_funds_code_does_not_match_statement_currency_THROW_exception() throws Exception {

        // Given
        String mt940MessageText = ":20:02618\n" +
                ":21:123456/DEV\n" +
                ":25:6-9412771\n" +
                ":28C:00102\n" +
                ":60F:C000103USD672,\n" + // currency USD
                ":61:0312091211DX880,FTRFBPHP/081203/0003//59512112915002\n" + // wrong funds code X expect usD
                ":62F:C000103USD987,\n" +
                "-";

        MT940PageReader classUnderTest = new MT940PageReader(new StringReader(mt940MessageText));

        // When
        Throwable exception = catchThrowable(classUnderTest::read);

        // Then
        assertThat(exception).isInstanceOf(SwiftMessageParseException.class).hasRootCauseInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void parse_WHEN_unfinished_page_detected_THROW_exception() throws Exception {

        // Given
        String mt940MessageText = ":20:02618\n";

        MT940PageReader classUnderTest = new MT940PageReader(new StringReader(mt940MessageText));

        // When
        Throwable exception = catchThrowable(classUnderTest::read);

        // Then
        assertThat(exception).isInstanceOf(SwiftMessageParseException.class);

    }

    @Test
    public void parse_WHEN_parse_many_valid_file_RETURN_message() throws Exception {

        // Given
        URL mt940_valid_folder = Resources.getResource("submessage/mt940_valid");
        Stream<Path> files = Files.walk(Paths.get(mt940_valid_folder.toURI())).filter(path -> Files.isRegularFile(path));

        // When
        final int[] errors = {0};
        files.forEach(filePath -> {
            try {
                MT940PageReader classUnderTest = new MT940PageReader(new FileReader(filePath.toFile()));
                List<MT940Page> messageList = TestUtils.collectUntilNull(classUnderTest::read);
                assertThat(messageList).isNotEmpty();
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