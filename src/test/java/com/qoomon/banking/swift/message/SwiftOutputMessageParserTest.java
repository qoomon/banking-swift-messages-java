package com.qoomon.banking.swift.message;

import com.qoomon.banking.swift.message.block.exception.BlockParseException;
import com.qoomon.banking.swift.message.exception.SwiftMessageParseException;
import org.assertj.core.api.Fail;
import org.assertj.core.api.SoftAssertions;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

/**
 * Created by qoomon on 24/06/16.
 */
public class SwiftOutputMessageParserTest {

    private static final String BLOCK_1_DUMMY_VALID = "{1:F01YOURCODEZABC1234567890}";
    private static final String BLOCK_2_DUMMY_VALID = "{2:O1001200970103BANKBEBBAXXX22221234569701031201N}";
    private static final String BLOCK_3_DUMMY_VALID = "{3:{113:SEPA}{108:ILOVESEPA}}";
    private static final String BLOCK_4_DUMMY_EMPTY = "{4:\n-}";
    private static final String BLOCK_5_DUMMY_EMPTY = "{5:}";

    private SoftAssertions softly = new SoftAssertions();

    private SwiftOutputMessageParser classUnderTest = new SwiftOutputMessageParser();

    @Test
    public void parse_WHEN_detecting_whitespaces_between_blocks_THEN_throw_exception() throws Exception {

        // Given
        String swiftMessageText = BLOCK_1_DUMMY_VALID + BLOCK_2_DUMMY_VALID + "{3:}" + " " + BLOCK_4_DUMMY_EMPTY + BLOCK_5_DUMMY_EMPTY;

        // When
        Throwable exception = catchThrowable(() -> classUnderTest.parse(new StringReader(swiftMessageText)));

        // Then
        assertThat(exception).as("Exception").isInstanceOf(BlockParseException.class);

        BlockParseException parseException = (BlockParseException) exception;
        assertThat(parseException.getLineNumber()).isEqualTo(1);
    }

    @Test
    public void parse_WHEN_block4_has_wrong_termination_THEN_throw_exception() throws Exception {

        // Given
        String swiftMessageText = BLOCK_1_DUMMY_VALID + BLOCK_2_DUMMY_VALID + BLOCK_3_DUMMY_VALID + "{4:\n}" + BLOCK_5_DUMMY_EMPTY;

        // When
        Throwable exception = catchThrowable(() -> classUnderTest.parse(new StringReader(swiftMessageText)));

        // Then
        assertThat(exception).as("Exception").isInstanceOf(BlockParseException.class);

        BlockParseException parseException = (BlockParseException) exception;
        assertThat(parseException.getLineNumber()).isEqualTo(0);
    }


    @Test
    public void parse_WHEN_first_bracket_is_missing_THEN_throw_exception() throws Exception {

        // Given
        String swiftMessageText = "1:}" + BLOCK_2_DUMMY_VALID + BLOCK_3_DUMMY_VALID + BLOCK_4_DUMMY_EMPTY + BLOCK_5_DUMMY_EMPTY;

        // When
        Throwable exception = catchThrowable(() -> classUnderTest.parse(new StringReader(swiftMessageText)));

        // Then
        assertThat(exception).as("Exception").isInstanceOf(BlockParseException.class);

        BlockParseException parseException = (BlockParseException) exception;
        assertThat(parseException.getLineNumber()).isEqualTo(1);

    }

    @Test
    public void parse_WHEN_block_structure_is_wrong_THEN_throw_exception() throws Exception {

        // Given
        String swiftMessageText = "{:1:}" + BLOCK_2_DUMMY_VALID + BLOCK_3_DUMMY_VALID + BLOCK_4_DUMMY_EMPTY + BLOCK_5_DUMMY_EMPTY;

        // When
        Throwable exception = catchThrowable(() -> classUnderTest.parse(new StringReader(swiftMessageText)));

        // Then
        assertThat(exception).as("Exception").isInstanceOf(BlockParseException.class);

        BlockParseException parseException = (BlockParseException) exception;
        assertThat(parseException.getLineNumber()).isEqualTo(1);

    }

    @Test
    public void parse_WHEN_block_appears_multiple_times_THEN_throw_exception() throws Exception {

        // Given
        String swiftMessageText = BLOCK_1_DUMMY_VALID + BLOCK_2_DUMMY_VALID + BLOCK_3_DUMMY_VALID + BLOCK_4_DUMMY_EMPTY + BLOCK_5_DUMMY_EMPTY + "{1:}";

        // When
        Throwable exception = catchThrowable(() -> classUnderTest.parse(new StringReader(swiftMessageText)));

        // Then
        assertThat(exception).as("Exception").isInstanceOf(SwiftMessageParseException.class);

        SwiftMessageParseException parseException = (SwiftMessageParseException) exception;
        assertThat(parseException.getBlockNumber()).isEqualTo(6);

    }

    @Test
    public void parse_WHEN_unknown_block_appears_THEN_throw_exception() throws Exception {

        // Given
        String swiftMessageText = BLOCK_1_DUMMY_VALID + BLOCK_2_DUMMY_VALID + BLOCK_3_DUMMY_VALID + BLOCK_4_DUMMY_EMPTY + BLOCK_5_DUMMY_EMPTY + "{6:}";

        // When
        Throwable exception = catchThrowable(() -> classUnderTest.parse(new StringReader(swiftMessageText)));

        // Then
        assertThat(exception).as("Exception").isInstanceOf(SwiftMessageParseException.class);

        SwiftMessageParseException parseException = (SwiftMessageParseException) exception;
        assertThat(parseException.getBlockNumber()).isEqualTo(6);

    }

    @Test
    public void parse_WHEN_brackets_are_unbalanced_THEN_throw_exception() throws Exception {

        // Given
        String swiftMessageText = BLOCK_1_DUMMY_VALID + BLOCK_2_DUMMY_VALID + BLOCK_3_DUMMY_VALID + BLOCK_4_DUMMY_EMPTY + "{5:";

        // When
        Throwable exception = catchThrowable(() -> classUnderTest.parse(new StringReader(swiftMessageText)));

        // Then
        assertThat(exception).as("Exception").isInstanceOf(BlockParseException.class);

        BlockParseException parseException = (BlockParseException) exception;
        assertThat(parseException.getLineNumber()).isEqualTo(2);

    }

    @Test
    public void parse_SHOULD_parse_valid_file() throws Exception {

        // Given
        String swiftMessageText = "{1:F01DEUTDEFFXNAI2260805235}{2:O9421247160519DEUTNL20PXXX22608052351605191247N}{3:{113:BL03}{108:484047}}{4:\n" +
                ":20:S000000008944056\n" +
                ":25:NL35DEUT0319841642CHF\n" +
                ":28C:466/2\n" +
                ":34F:CHF0,\n" +
                ":13D:1605191047+0100\n" +
                ":61:1605190519C304,59NTRFCT61408023551016//1614000208533186 \n" +
                "/OCMT/EUR278,24//EXCH/1,0947/\n" +
                ":86:SEPA Receipt (CR) \n" +
                "/EREF/NOTPROVIDED//ORDP/PHILIP V BVBA DEN HEUVE L 20 2970 SCHILDE BELGIUM \n" +
                "//REMI/ FN 6031818//ACCW/BE954164127511 58,KREDBEBB/ \n" +
                ":61:1605190519C803,08NTRFCT61408023551017//1614000208533184 \n" +
                "/OCMT/EUR733,61//EXCH/1,0947/\n" +
                ":86:SEPA Receipt (CR) /EREF/NOTPROVIDED//ORDP/DVMC \n" +
                "BVBA LOPPEMSESTRAA T 44 8210 ZEDELGEM BELGIUM //REMI/ FACTUUR \n" +
                "6023881//ACCW/BE527380 36131209,KREDBEBB/ \n" +
                ":61:1605190519C13047,70NTRFCT61408023551018//1614000208533185 \n" +
                "/OCMT/EUR11918,97//EXCH/1,0947/\n" +
                ":86:SEPA Receipt (CR) \n" +
                "/EREF/NOTPROVIDED//ORDP/ROOSEN LASER NV HOGE MA UW 442 2370 ARENDONK BELGIUM \n" +
                "//REMI/ 030397//ACCW/BE9073305162363 2,KREDBEBB/\n" +
                ":90D:0CHF0,\n" +
                ":90C:9CHF14698,42\n" +
                "-}{5:{CHK:000000000000}}";

        // When
        classUnderTest.parse(new StringReader(swiftMessageText));

        // Then

    }

}