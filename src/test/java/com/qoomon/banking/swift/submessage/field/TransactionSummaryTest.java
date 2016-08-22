package com.qoomon.banking.swift.submessage.field;

import com.qoomon.banking.swift.submessage.field.subfield.DebitCreditMark;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.junit.Test;

import static org.assertj.core.api.AssertionsForClassTypes.*;

/**
 * Created by qoomon on 22/07/16.
 */
public class TransactionSummaryTest {

    @Test
    public void of() throws Exception {
        // Given
        GeneralField generalField = new GeneralField(TransactionSummary.FIELD_TAG_90C, "12EUR123,");

        // When
        TransactionSummary field = TransactionSummary.of(generalField);

        // Then
        assertThat(field.getTag()).isEqualTo(generalField.getTag());
        assertThat(field.getContent()).isEqualTo(generalField.getContent());
    }

    @Test
    public void getSignedAmount_WHEN_debit_transaction_THEN_return_negative_amount() throws Exception {
        // Given
        BigMoney amount = BigMoney.of(CurrencyUnit.EUR, 1);
        TransactionSummary classUnderTest = new TransactionSummary(
                DebitCreditMark.DEBIT,
                1,
                amount);

        // When
        BigMoney signedAmount = classUnderTest.getSignedAmount();

        // Then
        assertThat(signedAmount).isEqualTo(amount.negated());
    }

    @Test
    public void getSignedAmount_WHEN_credit_transaction_THEN_return_positive_amount() throws Exception {
        // Given
        BigMoney amount = BigMoney.of(CurrencyUnit.EUR, 1);
        TransactionSummary classUnderTest = new TransactionSummary(
                DebitCreditMark.CREDIT,
                1,
                amount);

        // When
        BigMoney signedAmount = classUnderTest.getSignedAmount();

        // Then
        assertThat(signedAmount).isEqualTo(amount);
    }

}