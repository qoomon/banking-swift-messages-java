package com.qoomon.banking.swift.submessage.field;

import com.qoomon.banking.swift.submessage.field.subfield.DebitCreditMark;
import org.assertj.core.api.Assertions;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.junit.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Created by qoomon on 22/08/16.
 */
public class FloorLimitIndicatorTest {

    @Test
    public void of() throws Exception {
        // Given

        GeneralField generalField = new GeneralField(FloorLimitIndicator.FIELD_TAG_34F, "EUR123,");

        // When
        FloorLimitIndicator field = FloorLimitIndicator.of(generalField);

        // Then
        assertThat(field.getTag()).isEqualTo(generalField.getTag());
        assertThat(field.getContent()).isEqualTo(generalField.getContent());
    }

    @Test
    public void getSignedAmount_SHOULD_not_be_present_if_credit_debit_mark_ist_set() throws Exception {
        // Given
        DebitCreditMark debitCreditMark = null;
        FloorLimitIndicator classUnderTest = new FloorLimitIndicator(
                debitCreditMark,
                BigMoney.zero(CurrencyUnit.EUR));

        // When
        Optional<BigMoney> signedAmount = classUnderTest.getSignedAmount();

        // Then
        assertThat(signedAmount).isNotPresent();
    }

    @Test
    public void getSignedAmount_WHEN_debit_transaction_THEN_return_negative_amount() throws Exception {
        // Given
        BigMoney amount = BigMoney.of(CurrencyUnit.EUR, 1);
        FloorLimitIndicator classUnderTest = new FloorLimitIndicator(
                DebitCreditMark.DEBIT,
                amount);

        // When
        Optional<BigMoney> signedAmount = classUnderTest.getSignedAmount();

        // Then
        assertThat(signedAmount).contains(amount.negated());
    }

    @Test
    public void getSignedAmount_WHEN_credit_transaction_THEN_return_positive_amount() throws Exception {
        // Given
        BigMoney amount = BigMoney.of(CurrencyUnit.EUR, 1);
        FloorLimitIndicator classUnderTest = new FloorLimitIndicator(
                DebitCreditMark.CREDIT,
                amount);

        // When
        Optional<BigMoney> signedAmount = classUnderTest.getSignedAmount();

        // Then
        assertThat(signedAmount).contains(amount);
    }

}