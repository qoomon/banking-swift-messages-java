package com.qoomon.banking.swift.field;

import com.google.common.base.Preconditions;
import com.qoomon.banking.swift.field.notation.SwiftFieldNotation;
import com.qoomon.banking.swift.field.subfield.DebitCreditMark;

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

    private final DebitCreditMark debitCreditMark;
    private final String entryDate;
    private final String currency;
    private final String amount;

    public ClosingAvailableBalance(DebitCreditMark debitCreditMark, String entryDate, String currency, String amount) {
        this.debitCreditMark = Preconditions.checkNotNull(debitCreditMark);
        this.entryDate = Preconditions.checkNotNull(entryDate);
        this.currency = Preconditions.checkNotNull(currency);
        this.amount = Preconditions.checkNotNull(amount);
    }

    public static ClosingAvailableBalance of(GeneralMTField field) {
        Preconditions.checkArgument(field.getTag().equals(TAG), "unexpected field tag '" + field.getTag() + "'");

        List<String> subFields = SWIFT_NOTATION.parse(field.getContent());

        DebitCreditMark debitCreditMark = subFields.get(0) != null ? DebitCreditMark.of(subFields.get(0)) : null;
        String entryDate = subFields.get(1);
        String currency = subFields.get(2);
        String amount = subFields.get(3);

        return new ClosingAvailableBalance(debitCreditMark, entryDate, currency, amount);
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
