package com.qoomon.banking.swift.field;

import com.google.common.base.Preconditions;
import com.qoomon.banking.swift.field.notation.SwiftFieldNotation;
import com.qoomon.banking.swift.field.subfield.DebitCreditMark;
import org.joda.money.Money;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

/**
 * Created by qoomon on 24/06/16.
 */
public class ClosingAvailableBalance implements SwiftMTField {
    /**
     * :64: – Closing Available Balance (Available Funds)
     */
    public static final String TAG = "64";

    /**
     * 1!a6!n3!a15d - Debit/Credit | Entry Date | Currency | Amount
     */
    public static final SwiftFieldNotation SWIFT_NOTATION = new SwiftFieldNotation("1!a6!n3!a15d");

    private static final DateTimeFormatter ENTRY_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyMMdd");

    private final DebitCreditMark debitCreditMark;
    private final LocalDate entryDate;
    private final Money amount;

    public ClosingAvailableBalance(DebitCreditMark debitCreditMark, LocalDate entryDate, Money amount) {
        this.debitCreditMark = Preconditions.checkNotNull(debitCreditMark);
        this.entryDate = Preconditions.checkNotNull(entryDate);
        this.amount = Preconditions.checkNotNull(amount);
    }

    public static ClosingAvailableBalance of(GeneralMTField field) throws ParseException {
        Preconditions.checkArgument(field.getTag().equals(TAG), "unexpected field tag '" + field.getTag() + "'");

        List<String> subFields = SWIFT_NOTATION.parse(field.getContent());

        DebitCreditMark debitCreditMark = DebitCreditMark.of(subFields.get(0));
        LocalDate entryDate = LocalDate.parse(subFields.get(1), ENTRY_DATE_FORMATTER);
        String amountCurrency = subFields.get(2);
        String amountValue = subFields.get(3);
        Money amount = Money.parse(amountCurrency + amountValue.replaceFirst(",", "."));

        return new ClosingAvailableBalance(debitCreditMark, entryDate, amount);
    }

    public DebitCreditMark getDebitCreditMark() {
        return debitCreditMark;
    }

    public LocalDate getEntryDate() {
        return entryDate;
    }

    public Money getAmount() {
        return amount;
    }

    @Override
    public String getTag() {
        return TAG;
    }
}
