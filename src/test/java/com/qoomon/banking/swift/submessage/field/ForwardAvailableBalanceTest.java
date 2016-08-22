package com.qoomon.banking.swift.submessage.field;

import com.qoomon.banking.swift.submessage.field.subfield.DebitCreditMark;
import org.assertj.core.api.Assertions;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.junit.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

/**
 * Created by qoomon on 18/07/2016.
 */
public class ForwardAvailableBalanceTest {

    @Test
    public void of() throws Exception {
        // Given
        GeneralField generalField = new GeneralField(ForwardAvailableBalance.FIELD_TAG_65, "D" + "160130" + "EUR" + "123,456");

        // When
        ForwardAvailableBalance field = ForwardAvailableBalance.of(generalField);

        // Then
        assertThat(field).isNotNull();
        assertThat(field.getDebitCreditMark()).isEqualTo(DebitCreditMark.DEBIT);
        assertThat(field.getEntryDate()).isEqualTo(LocalDate.of(2016, 1, 30));
        assertThat(field.getAmount()).isEqualByComparingTo(BigMoney.of(CurrencyUnit.EUR, 123.456));

    }

    @Test
    public void getSignedAmount_WHEN_debit_transaction_THEN_return_negative_amount() throws Exception {
        // Given
        BigMoney amount = BigMoney.of(CurrencyUnit.EUR, 1);
        ForwardAvailableBalance classUnderTest = new ForwardAvailableBalance(LocalDate.now(),
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
        ForwardAvailableBalance classUnderTest = new ForwardAvailableBalance(LocalDate.now(),
                DebitCreditMark.CREDIT,
                amount);

        // When
        BigMoney signedAmount = classUnderTest.getSignedAmount();

        // Then
        assertThat(signedAmount).isEqualTo(amount);
    }

}