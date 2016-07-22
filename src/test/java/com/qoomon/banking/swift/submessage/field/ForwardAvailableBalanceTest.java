package com.qoomon.banking.swift.submessage.field;

import com.qoomon.banking.swift.submessage.field.subfield.DebitCreditMark;
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
    public void of_SHOULD_parse_valid_block() throws Exception {
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

}