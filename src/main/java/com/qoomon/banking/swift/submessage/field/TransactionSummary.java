package com.qoomon.banking.swift.submessage.field;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.qoomon.banking.swift.notation.FieldNotationParseException;
import com.qoomon.banking.swift.notation.SwiftDecimalFormatter;
import com.qoomon.banking.swift.notation.SwiftNotation;
import com.qoomon.banking.swift.submessage.field.subfield.DebitCreditMark;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;

import java.math.BigDecimal;
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
 * <b>Example</b>
 * <pre>
 * 4USD5782,64
 * </pre>
 */
public class TransactionSummary implements SwiftField {

    public static final String FIELD_TAG_90D = "90D";

    public static final String FIELD_TAG_90C = "90C";

    public static final SwiftNotation SWIFT_NOTATION = new SwiftNotation("5n3!a15d");

    private final com.qoomon.banking.swift.submessage.field.subfield.DebitCreditMark type;

    private final int transactionCount;

    private final BigMoney amount;


    public TransactionSummary(DebitCreditMark type, int transactionCount, BigMoney amount) {

        Preconditions.checkArgument(type != null, "type can't be null");
        Preconditions.checkArgument(transactionCount >= 0, "transaction count can't be negative. was: %s", transactionCount);
        Preconditions.checkArgument(amount != null, "amount can't be null");
        Preconditions.checkArgument(amount.isPositiveOrZero(), "amount can't be negative");

        this.type = type;
        this.transactionCount = transactionCount;
        this.amount = amount;
    }

    public static TransactionSummary of(GeneralField field) throws FieldNotationParseException {
        Preconditions.checkArgument(field.getTag().equals(FIELD_TAG_90D) || field.getTag().equals(FIELD_TAG_90C), "unexpected field tag '%s'", field.getTag());
        DebitCreditMark type = field.getTag().equals(FIELD_TAG_90D) ? DebitCreditMark.DEBIT : DebitCreditMark.CREDIT;

        List<String> subFields = SWIFT_NOTATION.parse(field.getContent());

        int transactionCount = Integer.parseInt(subFields.get(0));
        CurrencyUnit amountCurrency = CurrencyUnit.of(subFields.get(1));
        BigDecimal amountValue = SwiftDecimalFormatter.parse(subFields.get(2));
        BigMoney amount = BigMoney.of(amountCurrency, amountValue);

        return new TransactionSummary(type, transactionCount, amount);
    }

    public DebitCreditMark getType() {
        return type;
    }

    public int getTransactionCount() {
        return transactionCount;
    }

    public BigMoney getAmount() {
        return amount;
    }

    public BigMoney getSignedAmount() {
        if(getType().sign() < -1) {
            return amount.negated();
        }
        return amount;
    }

    @Override
    public String getTag() {
        return type == DebitCreditMark.DEBIT ? FIELD_TAG_90D : FIELD_TAG_90C;
    }

    @Override
    public String getContent() {
        try {
            return SWIFT_NOTATION.render(Lists.newArrayList(
                    String.valueOf(transactionCount),
                    amount.getCurrencyUnit().getCode(),
                    SwiftDecimalFormatter.format(amount.getAmount())
            ));
        } catch (FieldNotationParseException e) {
            throw new IllegalStateException("Invalid field values within " + getClass().getSimpleName() + " instance", e);
        }
    }


}
