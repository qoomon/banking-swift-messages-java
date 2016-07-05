package com.qoomon.banking.swift.field;

import com.google.common.base.Preconditions;
import com.qoomon.banking.swift.field.notation.SwiftFieldNotation;

import java.util.List;

/**
 * Created by qoomon on 24/06/16.
 */
public class ClosingBalance implements SwiftMTField {

    /**
     * :62F: - Closing Balance (Booked Funds)
     */
    public static final String TAG = "62F";

    /**
     * :62M: - Intermediate Balance
     */
    public static final String TAG_INTERMEDIATE = "62M";

    /**
     * 1!a6!n3!a15d - D/C | Date | Currency | Amount
     */
    public static final SwiftFieldNotation SWIFT_NOTATION = new SwiftFieldNotation("1!a6!n3!a15d");

    private final Type type;

    private final String debitCreditMark;
    private final String date;
    private final String currency;
    private final String amount;

    public ClosingBalance(GeneralMTField field) {
        Preconditions.checkArgument(field.getTag().equals(TAG) || field.getTag().equals(TAG_INTERMEDIATE), "unexpected field tag '" + field.getTag() + "'");
        this.type = field.getTag().equals(TAG) ? Type.CLOSING : Type.INTERMEDIATE;

        List<String> subFields = SWIFT_NOTATION.parse(field.getContent());
        Preconditions.checkArgument(subFields.get(0).equals("")
                || subFields.get(0).equals("C")
                || subFields.get(0).equals("D"));
        this.debitCreditMark = subFields.get(0);
        this.date = Preconditions.checkNotNull(subFields.get(1));
        this.currency = Preconditions.checkNotNull(subFields.get(2));
        this.amount = Preconditions.checkNotNull(subFields.get(3));
    }

    public Type getType() {
        return type;
    }

    public String getDebitCreditMark() {
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
        return type == Type.CLOSING ? TAG : TAG_INTERMEDIATE;
    }

    public enum Type {
        CLOSING,
        INTERMEDIATE
    }
}
