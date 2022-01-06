package com.qoomon.banking.swift.submessage.field;

import com.qoomon.banking.swift.submessage.field.subfield.DebitCreditMark;
import com.qoomon.banking.swift.submessage.field.subfield.DebitCreditType;
import com.qoomon.banking.swift.submessage.field.subfield.TransactionTypeIdentificationCode;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.*;

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
        assertThat(field.getTag()).isEqualTo(generalField.getTag());
        assertThat(field.getContent()).isEqualTo(generalField.getContent());
        assertThat(field.getDebitCreditType()).isEqualTo(DebitCreditType.REGULAR);
        assertThat(field.getSignedAmount()).isEqualTo(new BigDecimal("123.456"));
    }

    @Test
    public void getSignedAmount_WHEN_regular_debit_transaction_THEN_return_negative_amount() throws Exception {
        // Given
        BigDecimal amount = BigDecimal.ONE;
        StatementLine classUnderTest = new StatementLine(LocalDate.now(),
                LocalDate.now(),
                DebitCreditType.REGULAR,
                DebitCreditMark.DEBIT,
                amount,
                null,
                TransactionTypeIdentificationCode.of("NWAR"),
                "123456",
                null,
                null);

        // When
        BigDecimal signedAmount = classUnderTest.getSignedAmount();

        // Then
        Assertions.assertThat(signedAmount).isEqualTo(amount.negate());
    }

    @Test
    public void getSignedAmount_WHEN_regular_credit_transaction_THEN_return_positive_amount() throws Exception {
        // Given
        BigDecimal amount = BigDecimal.ONE;
        StatementLine classUnderTest = new StatementLine(LocalDate.now(),
                LocalDate.now(),
                DebitCreditType.REGULAR,
                DebitCreditMark.CREDIT,
                amount,
                null,
                TransactionTypeIdentificationCode.of("NWAR"),
                "123456",
                null,
                null);

        // When
        BigDecimal signedAmount = classUnderTest.getSignedAmount();

        // Then
        Assertions.assertThat(signedAmount).isEqualTo(amount);
    }

    @Test
    public void getSignedAmount_WHEN_reversal_debit_transaction_THEN_return_positive_amount() throws Exception {
        // Given
        BigDecimal amount = BigDecimal.ONE;
        StatementLine classUnderTest = new StatementLine(LocalDate.now(),
                LocalDate.now(),
                DebitCreditType.REVERSAL,
                DebitCreditMark.DEBIT,
                amount,
                null,
                TransactionTypeIdentificationCode.of("NWAR"),
                "123456",
                null,
                null);

        // When
        BigDecimal signedAmount = classUnderTest.getSignedAmount();

        // Then
        Assertions.assertThat(signedAmount).isEqualTo(amount);
    }

    @Test
    public void getSignedAmount_WHEN_reversal_credit_transaction_THEN_return_negative_amount() throws Exception {
        // Given
        BigDecimal amount = BigDecimal.ONE;
        StatementLine classUnderTest = new StatementLine(LocalDate.now(),
                LocalDate.now(),
                DebitCreditType.REVERSAL,
                DebitCreditMark.CREDIT,
                amount,
                null,
                TransactionTypeIdentificationCode.of("NWAR"),
                "123456",
                null,
                null);

        // When
        BigDecimal signedAmount = classUnderTest.getSignedAmount();

        // Then
        Assertions.assertThat(signedAmount).isEqualTo(amount.negate());
    }
}