package com.qoomon.banking.swift.submessage.field;

import com.qoomon.banking.swift.submessage.field.subfield.DebitCreditMark;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.junit.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

/**
 * Created by qoomon on 22/07/16.
 */
public class OpeningBalanceTest {

    @Test
    public void of() throws Exception {
        // Given
        GeneralField generalField = new GeneralField(OpeningBalance.FIELD_TAG_60F, "D160717EUR123,");

        // When
        OpeningBalance field = OpeningBalance.of(generalField);

        // Then
        assertThat(field.getTag()).isEqualTo(generalField.getTag());
        assertThat(field.getContent()).isEqualTo(generalField.getContent());
    }

    @Test
    public void getSignedAmount_WHEN_debit_transaction_THEN_return_negative_amount() throws Exception {
        // Given
        BigMoney amount = BigMoney.of(CurrencyUnit.EUR, 1);
        OpeningBalance classUnderTest = new OpeningBalance(OpeningBalance.Type.OPENING, LocalDate.now(),
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
        OpeningBalance classUnderTest = new OpeningBalance(OpeningBalance.Type.OPENING, LocalDate.now(),
                DebitCreditMark.CREDIT,
                amount);

        // When
        BigMoney signedAmount = classUnderTest.getSignedAmount();

        // Then
        assertThat(signedAmount).isEqualTo(amount);
    }

}