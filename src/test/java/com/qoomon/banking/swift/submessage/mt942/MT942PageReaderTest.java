package com.qoomon.banking.swift.submessage.mt942;


import com.google.common.base.Throwables;
import com.google.common.io.Resources;
import com.qoomon.banking.TestUtils;
import com.qoomon.banking.swift.message.exception.SwiftMessageParseException;
import com.qoomon.banking.swift.submessage.field.FloorLimitIndicator;
import com.qoomon.banking.swift.submessage.mt940.MT940PageReader;
import org.assertj.core.api.SoftAssertions;
import org.joda.money.BigMoney;
import org.junit.Test;

import java.io.FileReader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static com.qoomon.banking.swift.submessage.field.subfield.DebitCreditMark.CREDIT;
import static com.qoomon.banking.swift.submessage.field.subfield.DebitCreditMark.DEBIT;
import static org.assertj.core.api.Assertions.*;
import static org.joda.money.CurrencyUnit.USD;

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
                "-info\n" +
                ":61:0312091211D880,FTRFBPHP/081203/0003//59512112915002\n" +
                ":86:singleline info\n" +
                ":61:0312091211D880,FTRFBPHP/081203/0003//59512112915002\n" +
                ":90D:75475USD123,\n" +
                ":90C:75475USD123,\n" +
                ":86:multiline summary\n" +
                "summary\n" +
                "-";

        MT942PageReader classUnderTest = new MT942PageReader(new StringReader(mt942MessageText));

        // When
        List<MT942Page> pageList = TestUtils.collectUntilNull(classUnderTest::read);

        // Then
        assertThat(pageList).hasSize(1);
        MT942Page MT942Page = pageList.get(0);
        assertThat(MT942Page.getTransactionGroupList()).hasSize(3);
        assertThat(MT942Page.getStatementNumber().getStatementNumber()).isEqualTo("1");
        assertThat(MT942Page.getStatementNumber().getSequenceNumber()).contains("1");
    }

    @Test
    public void parse_WHEN_parse_valid_file_SHOULD_adjust_transactions_entry_dates_year_according_to_date_time_indicator() throws Exception {

        // Given
        String mt942MessageText = "" +
                ":20:02761\n" +
                ":25:6-9412771\n" +
                ":28C:1/1\n" +
                ":34F:USD123,\n" +
                ":13D:0303012359+0500\n" +
                ":61:0302280127D880,FTRFBPHP/081203/0003//59512112915002\n" +
                ":86:same year\n" +
                ":61:0312011120D880,FTRFBPHP/081203/0003//59512112915002\n" +
                ":86:previous year\n" +
                ":90D:75475USD123,\n" +
                ":90C:75475USD123,\n" +
                "-";

        MT942PageReader classUnderTest = new MT942PageReader(new StringReader(mt942MessageText));

        // When
        List<MT942Page> pageList = TestUtils.collectUntilNull(classUnderTest::read);

        // Then
        MT942Page MT942Page = pageList.get(0);
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(MT942Page.getTransactionGroupList().get(0).getStatementLine().getEntryDate()).isEqualTo(LocalDate.parse("2003-01-27"));
        softly.assertThat(MT942Page.getTransactionGroupList().get(1).getStatementLine().getEntryDate()).isEqualTo(LocalDate.parse("2002-11-20"));
        softly.assertAll();
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
                "-info\n" +
                ":61:0312091211D880,FTRFBPHP/081203/0003//59512112915002\n" +
                ":86:singleline info\n" +
                ":61:0312091211D880,FTRFBPHP/081203/0003//59512112915002\n" +
                ":90D:75475USD123,\n" +
                ":90C:75475USD123,\n" +
                ":86:multiline summary\n" +
                "summary\n" +
                "-";
        MT942PageReader pageReader = new MT942PageReader(new StringReader(contentInput));
        MT942Page classUnderTest = TestUtils.collectUntilNull(pageReader::read).get(0);

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

        MT942PageReader classUnderTest = new MT942PageReader(new StringReader(mt942MessageText));

        // When
        Throwable exception = catchThrowable(classUnderTest::read);

        // Then
        assertThat(exception).isInstanceOf(SwiftMessageParseException.class).hasRootCauseInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void parse_WHEN_unfinished_page_detected_THROW_exception() throws Exception {

        // Given
        String mt942MessageText = ":20:02618\n";

        MT940PageReader classUnderTest = new MT940PageReader(new StringReader(mt942MessageText));

        // When
        Throwable exception = catchThrowable(classUnderTest::read);

        // Then
        assertThat(exception).isInstanceOf(SwiftMessageParseException.class);

    }


    @Test
    public void parse_WHEN_parse_many_valid_file_RETURN_message() throws Exception {

        // Given
        URL mt942_valid_folder = Resources.getResource("submessage/mt942_valid");
        Stream<Path> files = Files.walk(Paths.get(mt942_valid_folder.toURI())).filter(Files::isRegularFile);

        // When
        final int[] errors = {0};
        files.forEach(filePath -> {
            try {
                MT942PageReader classUnderTest = new MT942PageReader(new FileReader(filePath.toFile()));
                List<MT942Page> pageList = TestUtils.collectUntilNull(classUnderTest::read);
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

    @Test
    public void parse_WHEN_parse_without_specific_floor_mark_RETURN_message() throws Exception {

        // Given
        String mt942MessageText = "" +
                ":20:02761\n" +
                ":25:6-9412771\n" +
                ":28C:1/1\n" +
                ":34F:USD123,\n" +
                ":13D:0001032359+0500\n" +
                ":61:0312091211D880,FTRFBPHP/081203/0003//59512112915002\n" +
                ":86:multiline info\n" +
                "-info\n" +
                ":61:0312091211D880,FTRFBPHP/081203/0003//59512112915002\n" +
                ":86:singleline info\n" +
                ":61:0312091211D880,FTRFBPHP/081203/0003//59512112915002\n" +
                ":90D:75475USD123,\n" +
                ":90C:75475USD123,\n" +
                ":86:multiline summary\n" +
                "summary\n" +
                "-";

        MT942PageReader classUnderTest = new MT942PageReader(new StringReader(mt942MessageText));

        // When
        List<MT942Page> pageList = TestUtils.collectUntilNull(classUnderTest::read);

        // Then
        assertThat(pageList).hasSize(1);
        MT942Page MT942Page = pageList.get(0);
        assertThat(MT942Page.getTransactionGroupList()).hasSize(3);
        assertThat(MT942Page.getStatementNumber().getStatementNumber()).isEqualTo("1");
        assertThat(MT942Page.getStatementNumber().getSequenceNumber()).contains("1");

        FloorLimitIndicator debitFloorLimitIndicator = MT942Page.getFloorLimitIndicatorDebit();
        assertThat(debitFloorLimitIndicator.getDebitCreditMark()).isEmpty();
        assertThat(debitFloorLimitIndicator.getAmount()).isEqualTo(BigMoney.of(USD, new BigDecimal("123")));

        FloorLimitIndicator creditFloorLimitIndicator = MT942Page.getFloorLimitIndicatorCredit();
        assertThat(creditFloorLimitIndicator.getDebitCreditMark()).isEmpty();
        assertThat(creditFloorLimitIndicator.getAmount()).isEqualTo(BigMoney.of(USD, new BigDecimal("123")));
    }

    @Test
    public void parse_WHEN_parse_with_debit_floor_but_not_credit_floor_RETURN_message() throws Exception {

        // Given
        String mt942MessageText = "" +
                ":20:02761\n" +
                ":25:6-9412771\n" +
                ":28C:1/1\n" +
                ":34F:USDD123,\n" +
                ":13D:0001032359+0500\n" +
                ":61:0312091211D880,FTRFBPHP/081203/0003//59512112915002\n" +
                ":86:multiline info\n" +
                "-info\n" +
                ":61:0312091211D880,FTRFBPHP/081203/0003//59512112915002\n" +
                ":86:singleline info\n" +
                ":61:0312091211D880,FTRFBPHP/081203/0003//59512112915002\n" +
                ":90D:75475USD123,\n" +
                ":90C:75475USD123,\n" +
                ":86:multiline summary\n" +
                "summary\n" +
                "-";

        MT942PageReader classUnderTest = new MT942PageReader(new StringReader(mt942MessageText));

        // When
        List<MT942Page> pageList = TestUtils.collectUntilNull(classUnderTest::read);

        // Then
        assertThat(pageList).hasSize(1);
        MT942Page MT942Page = pageList.get(0);
        assertThat(MT942Page.getTransactionGroupList()).hasSize(3);
        assertThat(MT942Page.getStatementNumber().getStatementNumber()).isEqualTo("1");
        assertThat(MT942Page.getStatementNumber().getSequenceNumber()).contains("1");

        FloorLimitIndicator debitFloorLimitIndicator = MT942Page.getFloorLimitIndicatorDebit();
        assertThat(debitFloorLimitIndicator.getDebitCreditMark()).hasValue(DEBIT);
        assertThat(debitFloorLimitIndicator.getAmount()).isEqualTo(BigMoney.of(USD, new BigDecimal("123")));

        FloorLimitIndicator creditFloorLimitIndicator = MT942Page.getFloorLimitIndicatorCredit();
        assertThat(creditFloorLimitIndicator.getDebitCreditMark()).hasValue(CREDIT);
        assertThat(creditFloorLimitIndicator.getAmount()).isEqualTo(BigMoney.zero(USD));
    }

    @Test
    public void parse_WHEN_parse_with_debit_floor_snd_credit_floor_RETURN_message() throws Exception {

        // Given
        String mt942MessageText = "" +
                ":20:02761\n" +
                ":25:6-9412771\n" +
                ":28C:1/1\n" +
                ":34F:USDD123,\n" +
                ":34F:USDC789,\n" +
                ":13D:0001032359+0500\n" +
                ":61:0312091211D880,FTRFBPHP/081203/0003//59512112915002\n" +
                ":86:multiline info\n" +
                "-info\n" +
                ":61:0312091211D880,FTRFBPHP/081203/0003//59512112915002\n" +
                ":86:singleline info\n" +
                ":61:0312091211D880,FTRFBPHP/081203/0003//59512112915002\n" +
                ":90D:75475USD123,\n" +
                ":90C:75475USD123,\n" +
                ":86:multiline summary\n" +
                "summary\n" +
                "-";

        MT942PageReader classUnderTest = new MT942PageReader(new StringReader(mt942MessageText));

        // When
        List<MT942Page> pageList = TestUtils.collectUntilNull(classUnderTest::read);

        // Then
        assertThat(pageList).hasSize(1);
        MT942Page MT942Page = pageList.get(0);
        assertThat(MT942Page.getTransactionGroupList()).hasSize(3);
        assertThat(MT942Page.getStatementNumber().getStatementNumber()).isEqualTo("1");
        assertThat(MT942Page.getStatementNumber().getSequenceNumber()).contains("1");

        FloorLimitIndicator debitFloorLimitIndicator = MT942Page.getFloorLimitIndicatorDebit();
        assertThat(debitFloorLimitIndicator.getDebitCreditMark()).hasValue(DEBIT);
        assertThat(debitFloorLimitIndicator.getAmount()).isEqualTo(BigMoney.of(USD, new BigDecimal("123")));

        FloorLimitIndicator creditFloorLimitIndicator = MT942Page.getFloorLimitIndicatorCredit();
        assertThat(creditFloorLimitIndicator.getDebitCreditMark()).hasValue(CREDIT);
        assertThat(creditFloorLimitIndicator.getAmount()).isEqualTo(BigMoney.of(USD, new BigDecimal("789")));
    }

    @Test
    public void parse_WHEN_parse_with_missing_debit_floor_but_credit_floor_RETURN_message() throws Exception {

        // Given
        String mt942MessageText = "" +
                ":20:02761\n" +
                ":25:6-9412771\n" +
                ":28C:1/1\n" +
                ":34F:USDC123,\n" +
                ":13D:0001032359+0500\n" +
                ":61:0312091211D880,FTRFBPHP/081203/0003//59512112915002\n" +
                ":86:multiline info\n" +
                "-info\n" +
                ":61:0312091211D880,FTRFBPHP/081203/0003//59512112915002\n" +
                ":86:singleline info\n" +
                ":61:0312091211D880,FTRFBPHP/081203/0003//59512112915002\n" +
                ":90D:75475USD123,\n" +
                ":90C:75475USD123,\n" +
                ":86:multiline summary\n" +
                "summary\n" +
                "-";

        MT942PageReader classUnderTest = new MT942PageReader(new StringReader(mt942MessageText));

        // When
        List<MT942Page> pageList = TestUtils.collectUntilNull(classUnderTest::read);

        // Then
        assertThat(pageList).hasSize(1);
        MT942Page MT942Page = pageList.get(0);
        assertThat(MT942Page.getTransactionGroupList()).hasSize(3);
        assertThat(MT942Page.getStatementNumber().getStatementNumber()).isEqualTo("1");
        assertThat(MT942Page.getStatementNumber().getSequenceNumber()).contains("1");

        FloorLimitIndicator debitFloorLimitIndicator = MT942Page.getFloorLimitIndicatorDebit();
        assertThat(debitFloorLimitIndicator.getDebitCreditMark()).hasValue(DEBIT);
        assertThat(debitFloorLimitIndicator.getAmount()).isEqualTo(BigMoney.zero(USD));

        FloorLimitIndicator creditFloorLimitIndicator = MT942Page.getFloorLimitIndicatorCredit();
        assertThat(creditFloorLimitIndicator.getDebitCreditMark()).hasValue(CREDIT);
        assertThat(creditFloorLimitIndicator.getAmount()).isEqualTo(BigMoney.of(USD, new BigDecimal("123")));
    }

}
