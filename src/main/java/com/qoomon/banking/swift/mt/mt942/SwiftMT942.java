package com.qoomon.banking.swift.mt.mt942;

import com.google.common.base.Preconditions;
import com.qoomon.banking.swift.field.*;
import com.qoomon.banking.swift.group.TransactionGroup;

import java.util.List;
import java.util.Optional;

/**
 * TODO adjust from mt940 to mt492
 */
public class SwiftMT942 {

    /**
     * @see TransactionReferenceNumber#FIELD_TAG_20
     */
    private final TransactionReferenceNumber transactionReferenceNumber;

    /**
     * @see RelatedReference#TAG
     */
    private final Optional<RelatedReference> relatedReference;

    /**
     * @see AccountIdentification#FIELD_TAG_25
     */
    private final AccountIdentification accountIdentification;

    /**
     * @see StatementNumber#FIELD_TAG_28C
     */
    private final StatementNumber statementNumber;

    /**
     * @see FloorLimitIndicator#FIELD_TAG_34F
     */
    private final FloorLimitIndicator floorLimitIndicatorDebitCredit;

    /**
     * @see FloorLimitIndicator#FIELD_TAG_34F
     */
    private final Optional<FloorLimitIndicator> floorLimitIndicatorCredit;

    /**
     * @see DateTimeIndicator#FIELD_TAG_13D
     */
    private final DateTimeIndicator dateTimeIndicator;

    /**
     * @see StatementLine#FIELD_TAG_61
     * @see InformationToAccountOwner#FIELD_TAG_86
     */
    private final List<TransactionGroup> transactionList;

    /**
     * @see InformationToAccountOwner#FIELD_TAG_86
     */
    private final Optional<InformationToAccountOwner> informationToAccountOwner;

    /**
     * @see TransactionSummary#FIELD_TAG_90D
     */
    private final Optional<TransactionSummary> transactionSummaryDebit;

    /**
     * @see TransactionSummary#FIED_TAG_90C
     */
    private final Optional<TransactionSummary> transactionSummaryCredit;


    public SwiftMT942(
            TransactionReferenceNumber transactionReferenceNumber,
            RelatedReference relatedReference,
            AccountIdentification accountIdentification,
            StatementNumber statementNumber,
            FloorLimitIndicator floorLimitIndicatorDebitCredit,
            FloorLimitIndicator floorLimitIndicatorCredit,
            DateTimeIndicator dateTimeIndicator,
            List<TransactionGroup> transactionList,
            TransactionSummary transactionSummaryDebit,
            TransactionSummary transactionSummaryCredit,
            InformationToAccountOwner informationToAccountOwner) {
        this.transactionReferenceNumber = Preconditions.checkNotNull(transactionReferenceNumber);
        this.relatedReference = Optional.ofNullable(relatedReference);
        this.accountIdentification = Preconditions.checkNotNull(accountIdentification);
        this.statementNumber = Preconditions.checkNotNull(statementNumber);
        this.floorLimitIndicatorDebitCredit = Preconditions.checkNotNull(floorLimitIndicatorDebitCredit);
        this.floorLimitIndicatorCredit =  Optional.ofNullable(floorLimitIndicatorCredit);
        this.dateTimeIndicator = Preconditions.checkNotNull(dateTimeIndicator);
        this.transactionList =  Preconditions.checkNotNull(transactionList);
        this.transactionSummaryDebit =  Optional.ofNullable(transactionSummaryDebit);
        this.transactionSummaryCredit =  Optional.ofNullable(transactionSummaryCredit);
        this.informationToAccountOwner =  Optional.ofNullable(informationToAccountOwner);
    }

    public TransactionReferenceNumber getTransactionReferenceNumber() {
        return transactionReferenceNumber;
    }

    public Optional<RelatedReference> getRelatedReference() {
        return relatedReference;
    }

    public AccountIdentification getAccountIdentification() {
        return accountIdentification;
    }

    public StatementNumber getStatementNumber() {
        return statementNumber;
    }

    public FloorLimitIndicator getFloorLimitIndicatorDebitCredit() {
        return floorLimitIndicatorDebitCredit;
    }

    public Optional<FloorLimitIndicator> getFloorLimitIndicatorCredit() {
        return floorLimitIndicatorCredit;
    }

    public DateTimeIndicator getDateTimeIndicator() {
        return dateTimeIndicator;
    }

    public List<TransactionGroup> getTransactionList() {
        return transactionList;
    }

    public Optional<InformationToAccountOwner> getInformationToAccountOwner() {
        return informationToAccountOwner;
    }

    public Optional<TransactionSummary> getTransactionSummaryDebit() {
        return transactionSummaryDebit;
    }

    public Optional<TransactionSummary> getTransactionSummaryCredit() {
        return transactionSummaryCredit;
    }
}
