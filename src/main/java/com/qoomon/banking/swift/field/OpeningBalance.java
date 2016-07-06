package com.qoomon.banking.swift.field;

import com.google.common.base.Preconditions;
import com.qoomon.banking.swift.field.notation.SwiftFieldNotation;
import com.qoomon.banking.swift.field.subfield.DebitCreditMark;
import org.joda.money.Money;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
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

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyMMdd");

    private final Type type;

    private final DebitCreditMark debitCreditMark;
    private final LocalDate date;
    private final Money amount;

    public OpeningBalance(Type type, DebitCreditMark debitCreditMark, LocalDate date, Money amount) {
        this.type = Preconditions.checkNotNull(type);
        this.debitCreditMark = Preconditions.checkNotNull(debitCreditMark);
        this.date = Preconditions.checkNotNull(date);
        this.amount = Preconditions.checkNotNull(amount);
    }

    public static OpeningBalance of(GeneralMTField field) throws ParseException {
        Preconditions.checkArgument(field.getTag().equals(TAG) || field.getTag().equals(TAG_INTERMEDIATE), "unexpected field tag '" + field.getTag() + "'");
        Type type = field.getTag().equals(TAG) ? Type.OPENING : Type.INTERMEDIATE;

        List<String> subFields = SWIFT_NOTATION.parse(field.getContent());

        DebitCreditMark debitCreditMark = subFields.get(0) != null ? DebitCreditMark.of(subFields.get(0)) : null;
        LocalDate date = LocalDate.parse(subFields.get(1), DATE_FORMATTER);
        String amountCurrency = subFields.get(2);
        String amountValue = subFields.get(3);
        Money amount = Money.parse(amountCurrency + amountValue.replaceFirst(",","."));

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

    public Money getAmount() {
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