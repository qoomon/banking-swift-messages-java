package com.qoomon.banking.swift.message.submessage.field;

import com.google.common.base.Preconditions;
import com.qoomon.banking.swift.message.submessage.field.notation.SwiftFieldNotation;
import com.qoomon.banking.swift.message.submessage.field.subfield.DebitCreditMark;
import com.qoomon.banking.swift.message.submessage.field.subfield.TransactionTypeIdentificationCode;

import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * <b>Statement Line</b>
 * <p>
 * <b>Field Tag</b> :61:
 * <p>
 * <b>Format</b> 6!n[4!n]2a[1!a]15d1!a3!c16x[//16x][34x]
 * <p>
 * <b>SubFields</b>
 * <pre>
 * 1: 6!n     - Value Date - Format 'YYMMDD'
 * 2: [4!n]   - Entry Date - Format 'MMDD'
 * 3: 2a      - Capital Code - 'D' = Debit, 'RD' = Reversal of Debit, 'C' = Credit, 'RC' = Reversal of Credit,
 * 4: [1!a]   - Debit/Credit Mark - 'D' = Debit, 'C' Credit
 * 5: 15d     - Amount
 * 6: 1!a3!c  - Transaction Type Identification Code {@link TransactionTypeIdentificationCode}
 * 7: 16x     - Reference for the Account Owner
 * 8: [//16x] - Reference for the Bank
 * 9: [34x]   - Transaction Description
 * </pre>
 */
public class StatementLine implements SwiftMTField {

    public static final String FIELD_TAG_61 = "61";

    public static final SwiftFieldNotation SWIFT_NOTATION = new SwiftFieldNotation("6!n[4!n]2a[1!a]15d1!a3!c16x[//16x][34x]");

    private static final DateTimeFormatter VALUE_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyMMdd");
    private static final DateTimeFormatter ENTRY_DATE_FORMATTER = DateTimeFormatter.ofPattern("MMdd");

    private final LocalDate valueDate;
    private final LocalDate entryDate;
    private final DebitCreditMark debitCreditMark;
    private final Optional<String> capitalCode;
    private final BigDecimal amount;
    private final TransactionTypeIdentificationCode transactionTypeIdentificationCode;
    private final String referenceForAccountOwner;
    private final Optional<String> referenceForBank;
    private final Optional<String> supplementaryDetails;

    public StatementLine(LocalDate valueDate,
                         LocalDate entryDate,
                         DebitCreditMark debitCreditMark,
                         String capitalCode,
                         BigDecimal amount,
                         TransactionTypeIdentificationCode transactionTypeIdentificationCode,
                         String referenceForAccountOwner,
                         String referenceForBank,
                         String supplementaryDetails) {
        this.valueDate = Preconditions.checkNotNull(valueDate);
        this.entryDate = entryDate != null ? entryDate : valueDate;
        this.debitCreditMark = Preconditions.checkNotNull(debitCreditMark);
        this.capitalCode = Optional.ofNullable(capitalCode);
        this.amount = Preconditions.checkNotNull(amount);
        this.transactionTypeIdentificationCode = Preconditions.checkNotNull(transactionTypeIdentificationCode);
        this.referenceForAccountOwner = Preconditions.checkNotNull(referenceForAccountOwner);
        this.referenceForBank = Optional.ofNullable(referenceForBank);
        this.supplementaryDetails = Optional.ofNullable(supplementaryDetails);
    }

    public static StatementLine of(GeneralMTField field) throws ParseException {
        Preconditions.checkArgument(field.getTag().equals(FIELD_TAG_61), "unexpected field tag '" + field.getTag() + "'");

        List<String> subFields = SWIFT_NOTATION.parse(field.getContent());

        LocalDate valueDate = LocalDate.parse(subFields.get(0), VALUE_DATE_FORMATTER);
        LocalDate entryDate = subFields.get(1) == null ? null : MonthDay.parse(subFields.get(1), ENTRY_DATE_FORMATTER).atYear(valueDate.getYear());
        DebitCreditMark debitCreditMark = DebitCreditMark.of(subFields.get(2));
        String foundsCode = subFields.get(3);
        BigDecimal amount = new BigDecimal(subFields.get(4).replaceFirst(",", "."));
        TransactionTypeIdentificationCode transactionTypeIdentificationCode = TransactionTypeIdentificationCode.parse(subFields.get(5) + subFields.get(6));
        String referenceForAccountOwner = subFields.get(7);
        String referenceForBank = subFields.get(8);
        String supplementaryDetails = subFields.get(9);

        return new StatementLine(
                valueDate,
                entryDate,
                debitCreditMark,
                foundsCode,
                amount,
                transactionTypeIdentificationCode,
                referenceForAccountOwner,
                referenceForBank,
                supplementaryDetails);
    }

    public LocalDate getValueDate() {
        return valueDate;
    }

    public LocalDate getEntryDate() {
        return entryDate;
    }

    public DebitCreditMark getDebitCreditMark() {
        return debitCreditMark;
    }

    public Optional<String> getCapitalCode() {
        return capitalCode;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public TransactionTypeIdentificationCode getTransactionTypeIdentificationCode() {
        return transactionTypeIdentificationCode;
    }

    public String getReferenceForAccountOwner() {
        return referenceForAccountOwner;
    }

    public Optional<String> getReferenceForBank() {
        return referenceForBank;
    }

    public Optional<String> getSupplementaryDetails() {
        return supplementaryDetails;
    }

    @Override
    public String getTag() {
        return FIELD_TAG_61;
    }
}
