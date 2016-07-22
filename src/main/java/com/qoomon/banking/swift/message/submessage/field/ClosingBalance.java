package com.qoomon.banking.swift.message.submessage.field;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.qoomon.banking.swift.message.submessage.field.subfield.DebitCreditMark;
import com.qoomon.banking.swift.notation.FieldNotationParseException;
import com.qoomon.banking.swift.notation.SwiftDecimalFormatter;
import com.qoomon.banking.swift.notation.SwiftNotation;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * <b>Closing Balance (Booked Funds)</b>
 * <p>
 * <b>Field Tag</b> :62F:
 * <b>Field Tag</b> :62M: - Intermediate Balance
 * </p>
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
public class ClosingBalance implements SwiftField {

    public static final String FIELD_TAG_62F = "62F";
    public static final String FIELD_TAG_62M = "62M";

    public static final SwiftNotation SWIFT_NOTATION = new SwiftNotation("1!a6!n3!a15d");

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyMMdd");

    private final Type type;

    private final DebitCreditMark debitCreditMark;

    private final LocalDate date;

    private final BigMoney amount;


    public ClosingBalance(Type type, DebitCreditMark debitCreditMark, LocalDate date, BigMoney amount) {

        Preconditions.checkArgument(type != null, "type can't be null");
        Preconditions.checkArgument(debitCreditMark != null, "debitCreditMark can't be null");
        Preconditions.checkArgument(date != null, "date can't be null");
        Preconditions.checkArgument(amount != null, "amount can't be null");

        this.type = type;
        this.debitCreditMark = debitCreditMark;
        this.date = date;
        this.amount = amount;
    }

    public static ClosingBalance of(GeneralField field) throws FieldNotationParseException {
        Preconditions.checkArgument(field.getTag().equals(FIELD_TAG_62F) || field.getTag().equals(FIELD_TAG_62M), "unexpected field tag '%s'", field.getTag());
        Type type = field.getTag().equals(FIELD_TAG_62F) ? Type.CLOSING : Type.INTERMEDIATE;

        List<String> subFields = SWIFT_NOTATION.parse(field.getContent());

        DebitCreditMark debitCreditMark = DebitCreditMark.ofFieldValue(subFields.get(0));
        LocalDate date = LocalDate.parse(subFields.get(1), DATE_FORMATTER);
        CurrencyUnit amountCurrency = CurrencyUnit.of(subFields.get(2));
        BigDecimal amountValue = SwiftDecimalFormatter.parse(subFields.get(3));
        BigMoney amount = BigMoney.of(amountCurrency, amountValue);

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

    public BigMoney getAmount() {
        return amount;
    }

    @Override
    public String getTag() {
        return type == Type.CLOSING ? FIELD_TAG_62F : FIELD_TAG_62M;
    }

    @Override
    public String getContent() {
        try {
            return SWIFT_NOTATION.render(Lists.newArrayList(
                    debitCreditMark.toFieldValue(),
                    date.format(DATE_FORMATTER),
                    amount.getCurrencyUnit().getCode(),
                    SwiftDecimalFormatter.format(amount.getAmount())));
        } catch (FieldNotationParseException e) {
            throw new IllegalStateException("Invalid field values within " + getClass().getSimpleName() + " instance", e);
        }
    }

    public enum Type {
        CLOSING,
        INTERMEDIATE
    }
}
