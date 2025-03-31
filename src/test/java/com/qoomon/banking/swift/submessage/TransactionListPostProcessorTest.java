package com.qoomon.banking.swift.submessage;

import com.google.common.collect.Lists;
import com.qoomon.banking.swift.submessage.field.GeneralField;
import com.qoomon.banking.swift.submessage.field.StatementLine;
import com.qoomon.banking.swift.submessage.field.TransactionGroup;
import com.qoomon.banking.swift.submessage.field.subfield.EntryDateResolutionStrategy;
import org.assertj.core.api.SoftAssertions;
import org.junit.Test;

import java.time.LocalDate;
import java.time.MonthDay;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TransactionListPostProcessorTest {

    @Test
    public void adjustEntryDates_WHEN_adjusting_transactions_SHOULD_return_modified_transactions() throws Exception {
        LocalDate statementDate = LocalDate.parse("2003-09-01");
        EntryDateResolutionStrategy strategy = mock(EntryDateResolutionStrategy.class);
        when(strategy.resolve(MonthDay.of(8, 30), statementDate)).thenReturn(LocalDate.parse("2003-08-30"));
        when(strategy.resolve(MonthDay.of(1, 1), statementDate)).thenReturn(LocalDate.parse("2004-01-01"));

        TransactionListPostProcessor classUnderTest = new TransactionListPostProcessor(strategy);

        List<TransactionGroup> transactions = Lists.newArrayList(
                new TransactionGroup(StatementLine.of(new GeneralField(StatementLine.FIELD_TAG_61, "030901" + "0830" + "CR123,45NSTOabcdef//xyz")), null),
                new TransactionGroup(StatementLine.of(new GeneralField(StatementLine.FIELD_TAG_61, "030901" + "0101" + "CR123,45NSTOabcdef//xyz")), null)
        );

        List<TransactionGroup> adjustedTransactions = classUnderTest.adjustEntryDates(transactions, statementDate);
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(adjustedTransactions).hasSize(2);
        softly.assertThat(adjustedTransactions.get(0).getStatementLine().getEntryDate()).isEqualTo(LocalDate.parse("2003-08-30"));
        softly.assertThat(adjustedTransactions.get(1).getStatementLine().getEntryDate()).isEqualTo(LocalDate.parse("2004-01-01"));
        softly.assertThat(adjustedTransactions.get(0)).withFailMessage("Post processor should not modify input").isNotSameAs(transactions.get(0));
        softly.assertThat(adjustedTransactions.get(1)).withFailMessage("Post processor should not modify input").isNotSameAs(transactions.get(1));
        softly.assertAll();
    }
}