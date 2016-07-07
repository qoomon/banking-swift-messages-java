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
 * <b>Closing Balance (Booked Funds)</b>
 * <p>
 * <b>Field Tag</b> :62F:
 * <b>Field Tag</b> :62M: - Intermediate Balance
 * <p>
 * <b>Format</b> 1!a6!n3!a15d
 * <p>
 * <b>SubFields</b>
 * <pre>
 * 1: 1!a - Debit/Credit Mark - 'D' = Debit, 'C' Credit
 * 2: 6!n - Date - Format 'YYMMDD'
 * 3: 6!n - Currency - Three Digit Code
 * 4: 15d - Amount
 * </pre>
 */
public class ClosingBalance implements SwiftMTField {

    public static final String FIELD_TAG_62F = "62F";
    public static final String FIELD_TAG_62M = "62M";

    public static final SwiftFieldNotation SWIFT_NOTATION = new SwiftFieldNotation("1!a6!n3!a15d");

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyMMdd");

    private final Type type;
    private final DebitCreditMark debitCreditMark;
    private final LocalDate date;
    private final Money amount;

    public ClosingBalance(Type type, DebitCreditMark debitCreditMark, LocalDate date, Money amount) {
        this.type = Preconditions.checkNotNull(type);
        this.debitCreditMark = Preconditions.checkNotNull(debitCreditMark);
        this.date = Preconditions.checkNotNull(date);
        this.amount = Preconditions.checkNotNull(amount);
    }

    public static ClosingBalance of(GeneralMTField field) throws ParseException {
        Preconditions.checkArgument(field.getTag().equals(FIELD_TAG_62F) || field.getTag().equals(FIELD_TAG_62M), "unexpected field tag '" + field.getTag() + "'");
        Type type = field.getTag().equals(FIELD_TAG_62F) ? Type.CLOSING : Type.INTERMEDIATE;

        List<String> subFields = SWIFT_NOTATION.parse(field.getContent());

        DebitCreditMark debitCreditMark = DebitCreditMark.of(subFields.get(0));
        LocalDate date = LocalDate.parse(subFields.get(1), DATE_FORMATTER);
        String amountCurrency = subFields.get(2);
        String amountValue = subFields.get(3);
        Money amount = Money.parse(amountCurrency + amountValue.replaceFirst(",", "."));

        return new ClosingBalance(type, debitCreditMark, date, amount);
    }

    public Type getType() {
        return type;
    }

    public DebitCreditMark getDebitCreditMark() {
        return debitCreditMark;
    }

    public LocalDate getDate() {
        return date;
    }

    public Money getAmount() {
        return amount;
    }

    @Override
    public String getTag() {
        return type == Type.CLOSING ? FIELD_TAG_62F : FIELD_TAG_62M;
    }

    public enum Type {
        CLOSING,
        INTERMEDIATE
    }
}
