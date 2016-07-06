package com.qoomon.banking.swift.field;

import com.google.common.base.Preconditions;
import com.qoomon.banking.swift.field.notation.SwiftFieldNotation;

import java.util.List;

/**
 * Created by qoomon on 24/06/16.
 */
public class TransactionSummary implements SwiftMTField {

    /**
     * :90D: - Closing Balance (Booked Funds)
     */
    public static final String TAG_DEBIT = "90D";

    /**
     * :90C: - Intermediate Balance
     */
    public static final String TAG_CREDIT = "90C";

    /**
     * 5n3!a15d - TransactionGroup Count | Currency | Amount - e.g. 4PLN5782,64
     */
    public static final SwiftFieldNotation SWIFT_NOTATION = new SwiftFieldNotation("5n3!a15d");

    private final Type type;
    private final int transactionCount;
    private final String currency;
    private final String amount;

    public TransactionSummary(Type type, int transactionCount, String currency, String amount) {
        this.type = type;
        this.transactionCount = transactionCount;
        this.currency = currency;
        this.amount = amount;
    }

    public static TransactionSummary of(GeneralMTField field) {
        Preconditions.checkArgument(field.getTag().equals(TAG_DEBIT) || field.getTag().equals(TAG_CREDIT), "unexpected field tag '" + field.getTag() + "'");
        Type type = field.getTag().equals(TAG_DEBIT) ? Type.DEBIT : Type.CREDIT;

        List<String> subFields = SWIFT_NOTATION.parse(field.getContent());
        Preconditions.checkNotNull(subFields.get(0));
        int transactionCount = Integer.parseInt(subFields.get(0));
        String currency = Preconditions.checkNotNull(subFields.get(1));
        String amount = Preconditions.checkNotNull(subFields.get(2));

        return new TransactionSummary(type, transactionCount, currency, amount);
    }

    public Type getType() {
        return type;
    }

    public int getTransactionCount() {
        return transactionCount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getAmount() {
        return amount;
    }

    @Override
    public String getTag() {
        return type == Type.DEBIT ? TAG_DEBIT : TAG_CREDIT;
    }

    public enum Type {
        CREDIT,
        DEBIT
    }
}
