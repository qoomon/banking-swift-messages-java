package com.qoomon.banking.swift.message.submessage.field;

import com.google.common.base.Preconditions;
import com.qoomon.banking.swift.message.submessage.field.notation.SwiftFieldNotation;
import com.qoomon.banking.swift.message.submessage.field.subfield.DebitCreditMark;
import org.joda.money.Money;

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
public class FloorLimitIndicator implements SwiftMTField {

    public static final String FIELD_TAG_34F = "34F";

    public static final SwiftFieldNotation SWIFT_NOTATION = new SwiftFieldNotation("3!a[1!a]15d");

    private final Optional<DebitCreditMark> debitCreditMark;
    private final Money amount;

    public FloorLimitIndicator(DebitCreditMark debitCreditMark, Money amount) {
        this.debitCreditMark = Optional.ofNullable(debitCreditMark);
        this.amount = Preconditions.checkNotNull(amount);
    }

    public static FloorLimitIndicator of(GeneralMTField field) throws ParseException {
        Preconditions.checkArgument(field.getTag().equals(FIELD_TAG_34F), "unexpected field tag '" + field.getTag() + "'");

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
        return FIELD_TAG_34F;
    }
}
