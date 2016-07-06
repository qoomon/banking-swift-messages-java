package com.qoomon.banking.swift.field;

import com.google.common.base.Preconditions;
import com.qoomon.banking.swift.field.notation.SwiftFieldNotation;
import com.qoomon.banking.swift.field.subfield.DebitCreditMark;
import org.joda.money.Money;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;

/**
 * Created by qoomon on 04/07/16.
 */
public class FloorLimitIndicator implements SwiftMTField {


    /**
     * :34F: - Floor limit indicator debit/credit
     */
    public static final String TAG = "34F";

    /**
     * 3!a[1!a]15d - Currency | Debit/Credit | Amount
     */
    public static final SwiftFieldNotation SWIFT_NOTATION = new SwiftFieldNotation("3!a[1!a]15d");

    private final Optional<DebitCreditMark> debitCreditMark;
    private final Money amount;

    public FloorLimitIndicator(DebitCreditMark debitCreditMark, Money amount) {
        this.debitCreditMark = Optional.ofNullable(debitCreditMark);
        this.amount = Preconditions.checkNotNull(amount);
    }

    public static FloorLimitIndicator of(GeneralMTField field) throws ParseException {
        Preconditions.checkArgument(field.getTag().equals(TAG), "unexpected field tag '" + field.getTag() + "'");

        List<String> subFields = SWIFT_NOTATION.parse(field.getContent());

        String amountCurrency = subFields.get(0);
        DebitCreditMark debitCreditMark = subFields.get(1) != null ? DebitCreditMark.of(subFields.get(1)) : null;
        String amountValue = subFields.get(2);
        Money amount = Money.parse(amountCurrency + amountValue.replaceFirst(",", "."));

        return new FloorLimitIndicator(debitCreditMark, amount);
    }


    public Optional<DebitCreditMark> getDebitCreditMark() {
        return debitCreditMark;
    }

    public Money getAmount() {
        return amount;
    }

    @Override
    public String getTag() {
        return TAG;
    }
}
