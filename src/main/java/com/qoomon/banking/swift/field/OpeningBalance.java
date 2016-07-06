package com.qoomon.banking.swift.field;

import com.google.common.base.Preconditions;
import com.qoomon.banking.swift.field.notation.SwiftFieldNotation;
import com.qoomon.banking.swift.field.subfield.DebitCreditMark;

import java.util.List;

/**
 * Created by qoomon on 24/06/16.
 */
public class OpeningBalance implements SwiftMTField {

    /**
     * :60F: – Opening Balance
     */
    public static final String TAG = "60F";

    /**
     * :60M: - Intermediate Balance
     */
    public static final String TAG_INTERMEDIATE = "60M";

    /**
     * 1!a6!n3!a15d - D/C | Date | Currency | Amount
     */
    public static final SwiftFieldNotation SWIFT_NOTATION = new SwiftFieldNotation("1!a6!n3!a15d");

    private final Type type;

    private final DebitCreditMark debitCreditMark;
    private final String date;
    private final String currency;
    private final String amount;

    public OpeningBalance(Type type, DebitCreditMark debitCreditMark, String date, String currency, String amount) {
        this.type = Preconditions.checkNotNull(type);
        this.debitCreditMark = Preconditions.checkNotNull(debitCreditMark);
        this.date = Preconditions.checkNotNull(date);
        this.currency = Preconditions.checkNotNull(currency);
        this.amount = Preconditions.checkNotNull(amount);
    }

    public static OpeningBalance of(GeneralMTField field) {
        Preconditions.checkArgument(field.getTag().equals(TAG) || field.getTag().equals(TAG_INTERMEDIATE), "unexpected field tag '" + field.getTag() + "'");
        Type type = field.getTag().equals(TAG) ? Type.OPENING : Type.INTERMEDIATE;

        List<String> subFields = SWIFT_NOTATION.parse(field.getContent());

        DebitCreditMark debitCreditMark = subFields.get(0) != null ? DebitCreditMark.of(subFields.get(0)) : null;
        String date = Preconditions.checkNotNull(subFields.get(1));
        String currency = Preconditions.checkNotNull(subFields.get(2));
        String amount = Preconditions.checkNotNull(subFields.get(3));

        return new OpeningBalance(type, debitCreditMark, date, currency, amount);
    }

    public Type getType() {
        return type;
    }

    public DebitCreditMark getDebitCreditMark() {
        return debitCreditMark;
    }

    public String getDate() {
        return date;
    }

    public String getCurrency() {
        return currency;
    }

    public String getAmount() {
        return amount;
    }

    @Override
    public String getTag() {
        return type == Type.OPENING ? TAG : TAG_INTERMEDIATE;
    }

    public enum Type {
        OPENING,
        INTERMEDIATE
    }


}