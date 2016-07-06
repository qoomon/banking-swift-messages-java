package com.qoomon.banking.swift.field;

import com.google.common.base.Preconditions;
import com.qoomon.banking.swift.field.notation.SwiftFieldNotation;
import com.qoomon.banking.swift.field.subfield.DebitCreditMark;

import java.util.List;
import java.util.Optional;

/**
 * Created by qoomon on 24/06/16.
 */
public class StatementLine implements SwiftMTField {

    /**
     * :61: – Statement Line
     */
    public static final String TAG = "61";

    /**
     * 6!n[4!n]2a[1!a]15d1!a3!c16x[//16x][34x] -  Value Date | Entry Date | Debit/Credit Mark | Funds Code | Amount | Transaction Type | Reference for the Account Owner | Reference for the bank | Transaction Description
     * <br>
     * Founds Code: C = credit, RC = Reversal of credit, D = debit, RD = Reversal of debit
     */
    public static final SwiftFieldNotation SWIFT_NOTATION = new SwiftFieldNotation("6!n[4!n]2a[1!a]15d1!a3!c16x[//16x][34x]");

    private final String valueDate;
    private final Optional<String> entryDate;
    private final DebitCreditMark debitCreditMark;
    private final Optional<String> foundsCode;
    private final String amount;
    private final String transactionType;
    private final String referenceForAccountOwner;
    private final Optional<String> referenceForBank;
    private final Optional<String> supplementaryDetails;

    public StatementLine(String valueDate,
                         String entryDate,
                         DebitCreditMark debitCreditMark,
                         String foundsCode,
                         String amount,
                         String transactionType,
                         String referenceForAccountOwner,
                         String referenceForBank,
                         String supplementaryDetails) {
        this.valueDate = Preconditions.checkNotNull(valueDate);
        this.entryDate = Optional.ofNullable(entryDate);
        this.debitCreditMark = Preconditions.checkNotNull(debitCreditMark);
        this.foundsCode = Optional.ofNullable(foundsCode);
        this.amount = Preconditions.checkNotNull(amount);
        this.transactionType = Preconditions.checkNotNull(transactionType);
        this.referenceForAccountOwner = Preconditions.checkNotNull(referenceForAccountOwner);
        this.referenceForBank = Optional.ofNullable(referenceForBank);
        this.supplementaryDetails = Optional.ofNullable(supplementaryDetails);
    }

    public static StatementLine of(GeneralMTField field) {
        Preconditions.checkArgument(field.getTag().equals(TAG), "unexpected field tag '" + field.getTag() + "'");

        List<String> subFields = SWIFT_NOTATION.parse(field.getContent());

        String valueDate = subFields.get(0);
        String entryDate = subFields.get(1);
        DebitCreditMark debitCreditMark = subFields.get(2) != null ? DebitCreditMark.of(subFields.get(2)) : null;
        String foundsCode = subFields.get(3);
        String amount = subFields.get(4);
        String transactionType = subFields.get(5) + subFields.get(6);
        String referenceForAccountOwner = subFields.get(7);
        String referenceForBank = subFields.get(8);
        String supplementaryDetails = subFields.get(9);

        return new StatementLine(
                valueDate,
                entryDate,
                debitCreditMark,
                foundsCode,
                amount,
                transactionType,
                referenceForAccountOwner,
                referenceForBank,
                supplementaryDetails);
    }

    public String getValueDate() {
        return valueDate;
    }

    public Optional<String> getEntryDate() {
        return entryDate;
    }

    public DebitCreditMark getDebitCreditMark() {
        return debitCreditMark;
    }

    public Optional<String> getFoundsCode() {
        return foundsCode;
    }

    public String getAmount() {
        return amount;
    }

    public String getTransactionType() {
        return transactionType;
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
        return TAG;
    }
}
