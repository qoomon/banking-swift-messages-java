package com.qoomon.banking.swift.message.submessage.field;

import com.google.common.base.Preconditions;
import com.qoomon.banking.swift.message.submessage.field.notation.SwiftFieldNotation;
import org.joda.money.Money;

import java.text.ParseException;
import java.util.List;

/**
 * <b>Transaction Summary</b>
 * <p>
 * <b>Field Tag</b> :90D: - Debit
 * <b>Field Tag</b> :90C: - Credit
 * <p>
 * <b>Format</b> 5n3!a15d
 * <p>
 * <b>SubFields</b>
 * <pre>
 * 1: 5n - Transaction Count
 * 2: 3!a - Currency - Three Digit Code
 * 4: 15d - Amount
 * </pre>
 * <p>
 * <b>Example</b>
 * <pre>
 * 4USD5782,64
 * </pre>
 */
public class TransactionSummary implements SwiftField {

    public static final String FIELD_TAG_90D = "90D";
    public static final String FIED_TAG_90C = "90C";

    public static final SwiftFieldNotation SWIFT_NOTATION = new SwiftFieldNotation("5n3!a15d");

    private final Type type;
    private final int transactionCount;
    private final Money amount;

    public TransactionSummary(Type type, int transactionCount, Money amount) {

        Preconditions.checkArgument(type != null, "type can't be null");
        Preconditions.checkArgument(transactionCount >= 0, "transaction count can't be negative. was: " + transactionCount);
        Preconditions.checkArgument(amount != null, "amount can't be null");

        this.type = type;
        this.transactionCount = transactionCount;
        this.amount = amount;
    }

    public static TransactionSummary of(GeneralField field) throws ParseException {
        Preconditions.checkArgument(field.getTag().equals(FIELD_TAG_90D) || field.getTag().equals(FIED_TAG_90C), "unexpected field tag '" + field.getTag() + "'");
        Type type = field.getTag().equals(FIELD_TAG_90D) ? Type.DEBIT : Type.CREDIT;

        List<String> subFields = SWIFT_NOTATION.parse(field.getContent());

        int transactionCount = Integer.parseInt(subFields.get(0));
        String amountCurrency = subFields.get(1);
        String amountValue = subFields.get(2);
        Money amount = Money.parse(amountCurrency + amountValue.replace(",", "."));

        return new TransactionSummary(type, transactionCount, amount);
    }

    public Type getType() {
        return type;
    }

    public int getTransactionCount() {
        return transactionCount;
    }

    public Money getAmount() {
        return amount;
    }

    @Override
    public String getTag() {
        return type == Type.DEBIT ? FIELD_TAG_90D : FIED_TAG_90C;
    }

    public enum Type {
        CREDIT,
        DEBIT
    }
}
