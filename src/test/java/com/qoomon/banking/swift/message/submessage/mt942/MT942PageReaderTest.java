package com.qoomon.banking.swift.message.submessage.mt942;


import com.google.common.base.Throwables;
import com.google.common.io.Resources;
import com.qoomon.banking.swift.TestUtils;
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
public class MT942PageReaderTest {

    @Test
    public void parse_WHEN_parse_valid_file_RETURN_message() throws Exception {

        // Given

        String mt920MessageText = "" +
                ":20:02761\n" +
                ":25:6-9412771\n" +
                ":28C:1/1\n" +
                ":34F:USD123,\n" +
                ":13D:0001032359+0500\n" +
                ":61:0312091209D880,FTRFBPHP/081203/0003//59512092915002\n" +
                ":86:multiline info\n" +
                "info\n" +
                ":61:0312091209D880,FTRFBPHP/081203/0003//59512092915002\n" +
                ":86:singleline info\n" +
                ":61:0312091209D880,FTRFBPHP/081203/0003//59512092915002\n" +
                ":90D:75475USD123,\n" +
                ":90C:75475USD123,\n" +
                ":86:multiline summary\n" +
                "summary\n" +
                "-";

        MT942PageReader classUnderTest = new MT942PageReader(new StringReader(mt920MessageText));

        // When
        List<MT942Page> pageList = TestUtils.collectAll(classUnderTest::readPage);

        // Then
        assertThat(pageList).hasSize(1);
        MT942Page MT942Page = pageList.get(0);
        assertThat(MT942Page.getTransactionGroupList()).hasSize(3);
        assertThat(MT942Page.getStatementNumber().getStatementNumber()).isEqualTo("1");
        assertThat(MT942Page.getStatementNumber().getSequenceNumber()).contains("1");
    }


    @Test
    public void parse_WHEN_parse_many_valid_file_RETURN_message() throws Exception {

        // Given
        URL mt942_valid_folder = Resources.getResource("submessage/mt942_valid");
        Stream<Path> files = Files.walk(Paths.get(mt942_valid_folder.toURI())).filter(path -> Files.isRegularFile(path));

        // When
        final int[] errors = {0};
        files.forEach(filePath -> {
            try {
                MT942PageReader classUnderTest = new MT942PageReader(new FileReader(filePath.toFile()));
                List<MT942Page> pageList = TestUtils.collectAll(classUnderTest::readPage);
                assertThat(pageList).isNotEmpty();
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