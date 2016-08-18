package com.qoomon.banking.swift.submessage.field;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.qoomon.banking.swift.notation.FieldNotationParseException;
import com.qoomon.banking.swift.notation.SwiftDecimalFormatter;
import com.qoomon.banking.swift.notation.SwiftNotation;
import com.qoomon.banking.swift.submessage.field.subfield.DebitCreditMark;
import com.qoomon.banking.swift.submessage.field.subfield.DebitCreditType;
import com.qoomon.banking.swift.submessage.field.subfield.TransactionTypeIdentificationCode;

import java.math.BigDecimal;
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
 * <b>Format</b> 6!n[4!n]2a[1!a]15d1!a3!c16x[//16x][BR34x]
 * <p>
 * <b>SubFields</b>
 * <pre>
 *  1: 6!n     - Value Date - Format 'YYMMDD'
 *  2: [4!n]   - Entry Date - Format 'MMDD'
 *  3: 2a      - Capital Code - 'D' = Debit, 'RD' = Reversal of Debit, 'C' = Credit, 'RC' = Reversal of Credit,
 *  4: [1!a]   - Funds Code (3rd character of the currency code)
 *  5: 15d     - Amount
 *  6: 1!a     - Transaction Type Identification Code {@link TransactionTypeIdentificationCode}
 *  7: 3!c         belongs to Transaction Type Identification Code
 *  8: 16x     - Reference for the Account Owner
 *  9: [//16x] - Reference for the Bank
 * 10: [BR34x]   - Transaction Description
 * </pre>
 */
public class StatementLine implements SwiftField {

    public static final String FIELD_TAG_61 = "61";

    public static final SwiftNotation SWIFT_NOTATION = new SwiftNotation("6!n[4!n]2a[1!a]15d1!a3!c16x[//16x][BR34x]");

    private static final DateTimeFormatter VALUE_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyMMdd");

    private static final DateTimeFormatter ENTRY_DATE_FORMATTER = DateTimeFormatter.ofPattern("MMdd");

    private final LocalDate valueDate;

    private final LocalDate entryDate;

    private final DebitCreditType debitCreditType;

    private final DebitCreditMark debitCreditMark;

    private final Optional<String> fundsCode;

    private final BigDecimal amount;

    private final TransactionTypeIdentificationCode transactionTypeIdentificationCode;

    private final String referenceForAccountOwner;

    private final Optional<String> referenceForBank;

    private final Optional<String> supplementaryDetails;


    public StatementLine(LocalDate valueDate,
                         LocalDate entryDate,
                         DebitCreditType debitCreditType,
                         DebitCreditMark debitCreditMark,
                         BigDecimal amount,
                         String fundsCode,
                         TransactionTypeIdentificationCode transactionTypeIdentificationCode,
                         String referenceForAccountOwner,
                         String referenceForBank,
                         String supplementaryDetails) {

        Preconditions.checkArgument(valueDate != null, "valueDate can't be null");
        Preconditions.checkArgument(debitCreditType != null, "debitCreditType can't be null");
        Preconditions.checkArgument(debitCreditMark != null, "debitCreditMark can't be null");
        Preconditions.checkArgument(amount != null, "amount can't be null");
        Preconditions.checkArgument(amount.compareTo(BigDecimal.ZERO) >= 0, "amount can't be negative");
        Preconditions.checkArgument(transactionTypeIdentificationCode != null, "transactionTypeIdentificationCode can't be null");
        Preconditions.checkArgument(referenceForAccountOwner != null, "referenceForAccountOwner can't be null");

        this.valueDate = valueDate;
        this.entryDate = entryDate != null ? entryDate : valueDate;
        this.debitCreditType = debitCreditType;
        this.debitCreditMark = debitCreditMark;
        this.fundsCode = Optional.ofNullable(fundsCode);
        this.amount = amount;
        this.transactionTypeIdentificationCode = transactionTypeIdentificationCode;
        this.referenceForAccountOwner = referenceForAccountOwner;
        this.referenceForBank = Optional.ofNullable(referenceForBank);
        this.supplementaryDetails = Optional.ofNullable(supplementaryDetails);
    }

    public static StatementLine of(GeneralField field) throws FieldNotationParseException {

        Preconditions.checkArgument(field.getTag().equals(FIELD_TAG_61), "unexpected field tag '%s'", field.getTag());

        List<String> subFields = SWIFT_NOTATION.parse(field.getContent());

        LocalDate valueDate = LocalDate.parse(subFields.get(0), VALUE_DATE_FORMATTER);
        LocalDate entryDate = null;
        // calculate entry date
        if (subFields.get(1) != null) {
            MonthDay entryMonthDay = MonthDay.parse(subFields.get(1), ENTRY_DATE_FORMATTER);
            // calculate entry year
            int entryYear = entryMonthDay.getMonthValue() >= valueDate.getMonthValue()
                    ? valueDate.getYear()
                    : valueDate.getYear() + 1;
            entryDate = entryMonthDay.atYear(entryYear);
        }
        DebitCreditType debitCreditType;
        DebitCreditMark debitCreditMark;
        String foundsCode;
        //// due to ambiguous format notation fo field 3 & 4 (2a[1!a]) it need some extra logic
        // if field 3 starts with 'R' it is a two letter mark 'RC' or 'RD' and everything is fine
        String capitalCode = subFields.get(2);
        if (capitalCode.startsWith("R")) {
            debitCreditType = DebitCreditType.REVERSAL;
            debitCreditMark = DebitCreditMark.ofFieldValue(capitalCode.substring(1, 2));
            foundsCode = subFields.get(3);
        }
        // if field 3 does not start with 'R' it is a one letter mark 'C' or 'D'
        // in this case optional field 4 can be part of field 3
        else {
            debitCreditType = DebitCreditType.REGULAR;
            debitCreditMark = DebitCreditMark.ofFieldValue(capitalCode.substring(0, 1));
            foundsCode = capitalCode.length() > 1 ? capitalCode.substring(1, 2) : null;
            // ensure 'Funds Code' field is unset
            if (subFields.get(3) != null) {
                throw new IllegalStateException("Field " + FIELD_TAG_61 + ": Founds Code already set");
            }
        }

        BigDecimal amount = SwiftDecimalFormatter.parse(subFields.get(4));
        TransactionTypeIdentificationCode transactionTypeIdentificationCode = TransactionTypeIdentificationCode.of(subFields.get(5) + subFields.get(6));
        String referenceForAccountOwner = subFields.get(7);
        String referenceForBank = subFields.get(8);
        String supplementaryDetails = subFields.get(9);

        return new StatementLine(
                valueDate,
                entryDate,
                debitCreditType,
                debitCreditMark,
                amount,
                foundsCode,
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

    public DebitCreditType getDebitCreditType() {
        return debitCreditType;
    }

    public DebitCreditMark getDebitCreditMark() {
        return debitCreditMark;
    }

    public Optional<String> getFundsCode() {
        return fundsCode;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getSignedAmount() {
        BigDecimal signedAmount = amount;
        if (getDebitCreditMark().sign() < 0) {
            signedAmount = signedAmount.negate();
        }
        if (getDebitCreditType() == DebitCreditType.REVERSAL) {
            signedAmount = signedAmount.negate();
        }
        return signedAmount;
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

    @Override
    public String getContent() {
        try {
            return SWIFT_NOTATION.render(Lists.newArrayList(
                    VALUE_DATE_FORMATTER.format(valueDate),
                    valueDate.equals(entryDate) ? null : ENTRY_DATE_FORMATTER.format(entryDate),
                    debitCreditMark.toFieldValue(),
                    fundsCode.orElse(null),
                    SwiftDecimalFormatter.format(amount),
                    transactionTypeIdentificationCode.getType().name(),
                    transactionTypeIdentificationCode.getCode(),
                    referenceForAccountOwner,
                    referenceForBank.orElse(null),
                    supplementaryDetails.orElse(null)
            ));
        } catch (FieldNotationParseException e) {
            throw new IllegalStateException("Invalid field values within " + getClass().getSimpleName() + " instance", e);
        }
    }
}