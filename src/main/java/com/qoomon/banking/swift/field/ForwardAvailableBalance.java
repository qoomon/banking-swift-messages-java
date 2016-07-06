package com.qoomon.banking.swift.field;

import com.google.common.base.Preconditions;
import com.qoomon.banking.swift.field.notation.SwiftFieldNotation;
import com.qoomon.banking.swift.field.subfield.DebitCreditMark;

import java.util.List;

/**
 * Created by qoomon on 24/06/16.
 */
public class ForwardAvailableBalance implements SwiftMTField {
    /**
     * :65: – Forward Available Balance
     */
    public static final String TAG = "65";

    /**
     * 1!a6!n3!a15d - Debit/Credit | Entry date | Currency | Amount
     */
    public static final SwiftFieldNotation SWIFT_NOTATION = new SwiftFieldNotation("1!a6!n3!a15d");

    private final DebitCreditMark debitCreditMark;
    private final String entryDate;
    private final String currency;
    private final String amount;

    public ForwardAvailableBalance(DebitCreditMark debitCreditMark, String entryDate, String currency, String amount) {
        this.debitCreditMark = Preconditions.checkNotNull(debitCreditMark);
        this.entryDate = Preconditions.checkNotNull(entryDate);
        this.currency = Preconditions.checkNotNull(currency);
        this.amount = Preconditions.checkNotNull(amount);
    }

    public static ForwardAvailableBalance of(GeneralMTField field) {
        Preconditions.checkArgument(field.getTag().equals(TAG), "unexpected field tag '" + field.getTag() + "'");

        List<String> subFields = SWIFT_NOTATION.parse(field.getContent());

        String currency = subFields.get(0);
        String entryDate = subFields.get(1);
        DebitCreditMark debitCreditMark = subFields.get(2) != null ? DebitCreditMark.of(subFields.get(2)) : null;
        String amount = subFields.get(3);

        return new ForwardAvailableBalance(debitCreditMark, entryDate, currency, amount);
    }

    public DebitCreditMark getDebitCreditMark() {
        return debitCreditMark;
    }

    public String getEntryDate() {
        return entryDate;
    }

    public String getCurrency() {
        return currency;
    }

    public String getAmount() {
        return amount;
    }

    @Override
    public String getTag() {
        return TAG;
    }
}
