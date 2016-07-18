package com.qoomon.banking.swift.message.submessage.field;

import com.qoomon.banking.swift.message.submessage.field.subfield.DebitCreditMark;
import com.qoomon.banking.swift.message.submessage.field.subfield.TransactionTypeIdentificationCode;
import org.joda.money.CurrencyUnit;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

/**
 * Created by qoomon on 18/07/2016.
 */
public class StatementLineTest {

    @Test
    public void of() throws Exception {

        // Given
        GeneralField generalField = new GeneralField(StatementLine.FIELD_TAG_61, "160130" + "C" + "R" + "123,456" + "NSTO" + "abcdef" + "//xyz" + "\nfoobar");

        // When
        StatementLine field = StatementLine.of(generalField);

        // Then
        assertThat(field).isNotNull();
        assertThat(field.getEntryDate()).isEqualTo(LocalDate.of(2016, 1, 30));
        assertThat(field.getDebitCreditMark()).isEqualByComparingTo(DebitCreditMark.CREDIT);
        assertThat(field.getFundsCode()).contains(CurrencyUnit.EUR.getCurrencyCode().substring(2, 3));
        assertThat(field.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(123.456));
        assertThat(field.getTransactionTypeIdentificationCode()).isEqualTo(TransactionTypeIdentificationCode.of("NSTO"));
        assertThat(field.getReferenceForAccountOwner()).isEqualTo("abcdef");
        assertThat(field.getReferenceForBank()).contains("xyz");
        assertThat(field.getSupplementaryDetails()).contains("foobar");

    }
}