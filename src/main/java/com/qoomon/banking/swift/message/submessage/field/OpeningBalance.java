package com.qoomon.banking.swift.message.submessage.field;

import com.google.common.base.Preconditions;
import com.qoomon.banking.swift.message.submessage.field.notation.SwiftFieldNotation;
import com.qoomon.banking.swift.message.submessage.field.subfield.DebitCreditMark;
import org.joda.money.BigMoney;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * <b>Opening Balance</b>
 * <p>
 * <b>Field Tag</b> :60F:
 * <b>Field Tag</b> :60M: - Intermediate Balance
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
public class OpeningBalance implements SwiftField {

    public static final String FIELD_TAG_60F = "60F";
    public static final String FIELD_TAG_60M = "60M";

    public static final SwiftFieldNotation SWIFT_NOTATION = new SwiftFieldNotation("1!a6!n3!a15d");

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyMMdd");

    private final Type type;

    private final DebitCreditMark debitCreditMark;
    private final LocalDate date;
    private final BigMoney amount;

    public OpeningBalance(Type type, DebitCreditMark debitCreditMark, LocalDate date, BigMoney amount) {

        Preconditions.checkArgument(type != null, "type can't be null");
        Preconditions.checkArgument(debitCreditMark != null, "debitCreditMark can't be null");
        Preconditions.checkArgument(date != null, "date can't be null");
        Preconditions.checkArgument(amount != null, "amount can't be null");

        this.type = type;
        this.debitCreditMark = debitCreditMark;
        this.date = date;
        this.amount = amount;
    }

    public static OpeningBalance of(GeneralField field) throws ParseException {
        Preconditions.checkArgument(field.getTag().equals(FIELD_TAG_60F) || field.getTag().equals(FIELD_TAG_60M), "unexpected field tag '%s'", field.getTag());
        Type type = field.getTag().equals(FIELD_TAG_60F) ? Type.OPENING : Type.INTERMEDIATE;

        List<String> subFields = SWIFT_NOTATION.parse(field.getContent());

        DebitCreditMark debitCreditMark = subFields.get(0) != null ? DebitCreditMark.of(subFields.get(0)) : null;
        LocalDate date = LocalDate.parse(subFields.get(1), DATE_FORMATTER);
        String amountCurrency = subFields.get(2);
        String amountValue = subFields.get(3);
        BigMoney amount = BigMoney.parse(amountCurrency + amountValue.replaceFirst(",", "."));

        return new OpeningBalance(type, debitCreditMark, date, amount);
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

    public BigMoney getAmount() {
        return amount;
    }

    @Override
    public String getTag() {
        return type == Type.OPENING ? FIELD_TAG_60F : FIELD_TAG_60M;
    }

    public enum Type {
        OPENING,
        INTERMEDIATE
    }


}