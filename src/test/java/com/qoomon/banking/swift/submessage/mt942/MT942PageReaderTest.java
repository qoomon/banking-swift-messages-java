package com.qoomon.banking.swift.submessage.mt942;


import com.google.common.base.Throwables;
import com.google.common.io.Resources;
import com.qoomon.banking.TestUtils;
import com.qoomon.banking.swift.message.exception.SwiftMessageParseException;
import com.qoomon.banking.swift.submessage.mt940.MT940PageReader;
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
 * Created by qoomon on 05/07/16.
 */
public class MT942PageReaderTest {

    @Test
    public void parse_WHEN_parse_valid_file_RETURN_message() throws Exception {

        // Given

        String mt942MessageText = "" +
                ":20:02761\n" +
                ":25:6-9412771\n" +
                ":28C:1/1\n" +
                ":34F:USD123,\n" +
                ":13D:0001032359+0500\n" +
                ":61:0312091211D880,FTRFBPHP/081203/0003//59512112915002\n" +
                ":86:multiline info\n" +
                "info\n" +
                ":61:0312091211D880,FTRFBPHP/081203/0003//59512112915002\n" +
                ":86:singleline info\n" +
                ":61:0312091211D880,FTRFBPHP/081203/0003//59512112915002\n" +
                ":90D:75475USD123,\n" +
                ":90C:75475USD123,\n" +
                ":86:multiline summary\n" +
                "summary\n" +
                "-";

        final MT942PageReader classUnderTest = new MT942PageReader(new StringReader(mt942MessageText));

        // When
        List<MT942Page> pageList = TestUtils.collectUntilNull(new Callable<MT942Page>() {
            @Override
            public MT942Page call() throws Exception {
                return classUnderTest.read();
            }
        });

        // Then
        assertThat(pageList).hasSize(1);
        MT942Page MT942Page = pageList.get(0);
        assertThat(MT942Page.getTransactionGroupList()).hasSize(3);
        assertThat(MT942Page.getStatementNumber().getStatementNumber()).isEqualTo("1");
        assertThat(MT942Page.getStatementNumber().getSequenceNumber().get()).isEqualTo("1");
    }

    @Test
    public void getContent_SHOULD_return_input_text() throws Exception {

        // Given
        String contentInput = "" +
                ":20:02761\n" +
                ":25:6-9412771\n" +
                ":28C:1/1\n" +
                ":34F:USD123,\n" +
                ":13D:0001032359+0500\n" +
                ":61:0312091211D880,FTRFBPHP/081203/0003//59512112915002\n" +
                ":86:multiline info\n" +
                "info\n" +
                ":61:0312091211D880,FTRFBPHP/081203/0003//59512112915002\n" +
                ":86:singleline info\n" +
                ":61:0312091211D880,FTRFBPHP/081203/0003//59512112915002\n" +
                ":90D:75475USD123,\n" +
                ":90C:75475USD123,\n" +
                ":86:multiline summary\n" +
                "summary\n" +
                "-";
        final MT942PageReader pageReader = new MT942PageReader(new StringReader(contentInput));
        MT942Page classUnderTest = TestUtils.collectUntilNull(new Callable<MT942Page>() {
            @Override
            public MT942Page call() throws Exception {
                return pageReader.read();
            }
        }).get(0);

        // When
        String content = classUnderTest.getContent();

        // Then
        assertThat(content).isEqualTo(contentInput);
    }

    @Test
    public void parse_WHEN_funds_code_does_not_match_statement_currency_THROW_exception() throws Exception {

        // Given
        String mt942MessageText = "" +
                ":20:02761\n" +
                ":25:6-9412771\n" +
                ":28C:1/1\n" +
                ":34F:USD123,\n" + // currency USD
                ":13D:0001032359+0500\n" +
                ":61:0312091211DX880,FTRFBPHP/081203/0003//59512112915002\n" +  // wrong funds code X expect usD
                "-";

        final MT942PageReader classUnderTest = new MT942PageReader(new StringReader(mt942MessageText));

        // When
        Throwable exception = catchThrowable(new ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                classUnderTest.read();
            }
        });

        // Then
        assertThat(exception).isInstanceOf(SwiftMessageParseException.class).hasRootCauseInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void parse_WHEN_unfinished_page_detected_THROW_exception() throws Exception {

        // Given
        String mt942MessageText = ":20:02618\n";

        final MT940PageReader classUnderTest = new MT940PageReader(new StringReader(mt942MessageText));

        // When
        Throwable exception = catchThrowable(new ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                classUnderTest.read();
            }
        });

        // Then
        assertThat(exception).isInstanceOf(SwiftMessageParseException.class);

    }


    @Test
    public void parse_WHEN_parse_many_valid_file_RETURN_message() throws Exception {

        // Given
        URL mt942_valid_folder = Resources.getResource("submessage/mt942_valid");

        // When
        final int[] errors = {0};
        Files.walkFileTree(Paths.get(mt942_valid_folder.toURI()), new SimpleFileVisitor<Path>(){
            @Override
            public FileVisitResult visitFile(Path filePath, BasicFileAttributes attrs) throws IOException {

                if( !attrs.isRegularFile() ){
                    return FileVisitResult.CONTINUE;
                }

                try {
                    final MT942PageReader classUnderTest = new MT942PageReader(new FileReader(filePath.toFile()));
                    List<MT942Page> pageList = TestUtils.collectUntilNull(new Callable<MT942Page>() {
                        @Override
                        public MT942Page call() throws Exception {
                            return classUnderTest.read();
                        }
                    });
                    assertThat(pageList).isNotEmpty();
                } catch (Exception e) {
                    System.out.println(filePath);
                    System.out.println(Throwables.getStackTraceAsString(e));
                    System.out.println();
                    errors[0]++;
                }
                return FileVisitResult.CONTINUE;
            }
        });

        // Then
        assertThat(errors[0]).isEqualTo(0);

    }

}