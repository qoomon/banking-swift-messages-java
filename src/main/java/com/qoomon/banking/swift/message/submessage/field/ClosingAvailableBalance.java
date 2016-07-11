package com.qoomon.banking.swift.message.submessage.field;

import com.google.common.base.Preconditions;
import com.qoomon.banking.swift.message.submessage.field.notation.SwiftFieldNotation;
import com.qoomon.banking.swift.message.submessage.field.subfield.DebitCreditMark;
import org.joda.money.Money;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * <b>Closing Available Balance (Available Funds)</b>
 * <p>
 * <b>Field Tag</b> :64:
 * <p>
 * <b>Format</b> 1!a6!n3!a15d
 * <p>
 * <b>SubFields</b>
 * <pre>
 * 1: 1!a - Debit/Credit Mark - 'D' = Debit, 'C' Credit
 * 2: 6!n - Entry Date - Format 'YYMMDD'
 * 3: 3!a - Currency - Three Digit Code
 * 4: 15d - Amount
 * </pre>
 */
public class ClosingAvailableBalance implements SwiftField {

    public static final String FIELD_TAG_64 = "64";

    public static final SwiftFieldNotation SWIFT_NOTATION = new SwiftFieldNotation("1!a6!n3!a15d");

    private static final DateTimeFormatter ENTRY_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyMMdd");

    private final DebitCreditMark debitCreditMark;
    private final LocalDate entryDate;
    private final Money amount;

    public ClosingAvailableBalance(DebitCreditMark debitCreditMark, LocalDate entryDate, Money amount) {

        Preconditions.checkArgument(debitCreditMark != null, "debitCreditMark can't be null");
        Preconditions.checkArgument(entryDate != null, "entryDate can't be null");
        Preconditions.checkArgument(amount != null, "amount can't be null");

        this.debitCreditMark = debitCreditMark;
        this.entryDate = entryDate;
        this.amount = amount;
    }

    public static ClosingAvailableBalance of(GeneralField field) throws ParseException {
        Preconditions.checkArgument(field.getTag().equals(FIELD_TAG_64), "unexpected field tag '%s'",field.getTag());

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
        return FIELD_TAG_64;
    }
}
