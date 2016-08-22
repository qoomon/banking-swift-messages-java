package com.qoomon.banking.swift.submessage.field;

import com.qoomon.banking.swift.submessage.field.subfield.DebitCreditMark;
import org.assertj.core.api.Assertions;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForInterfaceTypes.*;


/**
 * Created by qoomon on 22/07/16.
 */
public class ClosingAvailableBalanceTest {


    @Test
    public void of() throws Exception {
        // Given
        GeneralField generalField = new GeneralField(ClosingAvailableBalance.FIELD_TAG_64, "D160717EUR2233,");

        // When
        ClosingAvailableBalance field = ClosingAvailableBalance.of(generalField);

        // Then
        assertThat(field.getTag()).isEqualTo(generalField.getTag());
        assertThat(field.getContent()).isEqualTo(generalField.getContent());
    }

    @Test
    public void getSignedAmount_WHEN_debit_transaction_THEN_return_negative_amount() throws Exception {
        // Given
        BigMoney amount = BigMoney.of(CurrencyUnit.EUR, 1);
        ClosingAvailableBalance classUnderTest = new ClosingAvailableBalance(LocalDate.now(),
                DebitCreditMark.DEBIT,
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
        ClosingAvailableBalance classUnderTest = new ClosingAvailableBalance(LocalDate.now(),
                DebitCreditMark.CREDIT,
                amount);

        // When
        BigMoney signedAmount = classUnderTest.getSignedAmount();

        // Then
        assertThat(signedAmount).isEqualTo(amount);
    }

}