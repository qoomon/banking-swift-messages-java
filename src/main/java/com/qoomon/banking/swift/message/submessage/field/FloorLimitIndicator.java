package com.qoomon.banking.swift.message.submessage.field;

import com.google.common.base.Preconditions;
import com.qoomon.banking.swift.message.submessage.field.notation.SwiftFieldNotation;
import com.qoomon.banking.swift.message.submessage.field.subfield.DebitCreditMark;
import org.joda.money.BigMoney;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;

/**
 * <b>Floor Limit Indicator Debit/Credit</b>
 * <p>
 * <b>Field Tag</b> :34F:
 * <p>
 * <b>Format</b> 3!a[1!a]15d
 * <p>
 * <b>SubFields</b>
 * <pre>
 * 1: 3!a   - Currency - Three Digit Code
 * 2: [1!a] - Debit/Credit Mark - 'D' = Debit, 'C' Credit
 * 3: 15d   - Amount
 * </pre>
 */
public class FloorLimitIndicator implements SwiftField {

    public static final String FIELD_TAG_34F = "34F";

    public static final SwiftFieldNotation SWIFT_NOTATION = new SwiftFieldNotation("3!a[1!a]15d");

    private final Optional<DebitCreditMark> debitCreditMark;
    private final BigMoney amount;

    public FloorLimitIndicator(DebitCreditMark debitCreditMark, BigMoney amount) {

        Preconditions.checkArgument(amount != null, "amount can't be null");

        this.debitCreditMark = Optional.ofNullable(debitCreditMark);
        this.amount = amount;
    }

    public static FloorLimitIndicator of(GeneralField field) throws ParseException {
        Preconditions.checkArgument(field.getTag().equals(FIELD_TAG_34F), "unexpected field tag '%s'", field.getTag());

        List<String> subFields = SWIFT_NOTATION.parse(field.getContent());

        String amountCurrency = subFields.get(0);
        DebitCreditMark debitCreditMark = subFields.get(1) != null ? DebitCreditMark.of(subFields.get(1)) : null;
        String amountValue = subFields.get(2);
        BigMoney amount = BigMoney.parse(amountCurrency + amountValue.replaceFirst(",", "."));

        return new FloorLimitIndicator(debitCreditMark, amount);
    }


    public Optional<DebitCreditMark> getDebitCreditMark() {
        return debitCreditMark;
    }

    public BigMoney getAmount() {
        return amount;
    }

    @Override
    public String getTag() {
        return FIELD_TAG_34F;
    }
}
