package com.qoomon.banking.swift.field;

import com.google.common.base.Preconditions;
import com.qoomon.banking.swift.field.notation.SwiftFieldNotation;

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
     * 6!n[4!n]2a[1!a]15d1!a3!c
     * 6!n[4!n]2a[1!a]15d1!a3!c16x[//16x][34x] -  Value Date | Entry Date | Debit/Credit Mark | Funds Code | Amount | Transaction Type | Reference for the Account Owner | Reference for the bank | Transaction Description
     * <br>
     * Founds Code: C = credit, RC = Reversal of credit, D = debit, RD = Reversal of debit
     */

    public static final SwiftFieldNotation SWIFT_NOTATION = new SwiftFieldNotation("6!n[4!n]2a[1!a]15d1!a3!c16x[//16x][34x]");

    private final String valueDate;
    private final Optional<String> entryDate;
    private final String debitCreditMark;
    private final Optional<String> foundsCode;
    private final String amount;
    private final String transactionType;
    private final String referenceForAccountOwner;
    private final Optional<String> referenceForBank;
    private final Optional<String> supplementaryDetails;

    public StatementLine(GeneralMTField field) {
        Preconditions.checkArgument(field.getTag().equals(TAG), "unexpected field tag '" + field.getTag() + "'");

        List<String> subFields = SWIFT_NOTATION.parse(field.getContent());
        valueDate = Preconditions.checkNotNull(subFields.get(0));
        entryDate = Optional.ofNullable(subFields.get(1));
        debitCreditMark = Preconditions.checkNotNull(subFields.get(2));
        foundsCode = Optional.ofNullable(subFields.get(3));
        amount = Preconditions.checkNotNull(subFields.get(4));
        transactionType = Preconditions.checkNotNull(subFields.get(5)) + Preconditions.checkNotNull(subFields.get(6));
        referenceForAccountOwner = Preconditions.checkNotNull(subFields.get(7));
        referenceForBank = Optional.ofNullable(subFields.get(8));
        supplementaryDetails = Optional.ofNullable(subFields.get(9));
    }

    public String getValueDate() {
        return valueDate;
    }

    public Optional<String> getEntryDate() {
        return entryDate;
    }

    public String getDebitCreditMark() {
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
