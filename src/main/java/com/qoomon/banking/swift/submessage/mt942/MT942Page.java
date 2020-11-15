package com.qoomon.banking.swift.submessage.mt942;

import com.google.common.base.Preconditions;
import com.qoomon.banking.swift.submessage.PageSeparator;
import com.qoomon.banking.swift.submessage.field.*;
import com.qoomon.banking.swift.submessage.field.subfield.DebitCreditMark;
import org.joda.money.CurrencyUnit;

import java.util.List;
import java.util.Optional;

import static com.qoomon.banking.swift.submessage.field.FieldUtils.swiftTextOf;
import static com.qoomon.banking.swift.submessage.field.subfield.DebitCreditMark.CREDIT;
import static com.qoomon.banking.swift.submessage.field.subfield.DebitCreditMark.DEBIT;

/**
 *
 */
public class MT942Page {

    public static final String MESSAGE_ID_942 = "942";

    /**
     * @see TransactionReferenceNumber#FIELD_TAG_20
     */
    private final TransactionReferenceNumber transactionReferenceNumber;

    /**
     * @see RelatedReference#FIELD_TAG_21
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
    private final FloorLimitIndicator floorLimitIndicatorDebit;

    /**
     * @see FloorLimitIndicator#FIELD_TAG_34F
     */
    private final FloorLimitIndicator floorLimitIndicatorCredit;

    /**
     * @see DateTimeIndicator#FIELD_TAG_13D
     */
    private final DateTimeIndicator dateTimeIndicator;

    /**
     * @see StatementLine#FIELD_TAG_61
     * @see InformationToAccountOwner#FIELD_TAG_86
     */
    private final List<TransactionGroup> transactionGroupList;

    /**
     * @see TransactionSummary#FIELD_TAG_90D
     */
    private final Optional<TransactionSummary> transactionSummaryDebit;

    /**
     * @see TransactionSummary#FIELD_TAG_90C
     */
    private final Optional<TransactionSummary> transactionSummaryCredit;

    /**
     * @see InformationToAccountOwner#FIELD_TAG_86
     */
    private final Optional<InformationToAccountOwner> informationToAccountOwner;


    public MT942Page(
            TransactionReferenceNumber transactionReferenceNumber,
            RelatedReference relatedReference,
            AccountIdentification accountIdentification,
            StatementNumber statementNumber,
            FloorLimitIndicator floorLimitIndicatorDebit,
            FloorLimitIndicator floorLimitIndicatorCredit,
            DateTimeIndicator dateTimeIndicator,
            List<TransactionGroup> transactionGroupList,
            TransactionSummary transactionSummaryDebit,
            TransactionSummary transactionSummaryCredit,
            InformationToAccountOwner informationToAccountOwner) {

        Preconditions.checkArgument(transactionReferenceNumber != null, "transactionReferenceNumber can't be null");
        Preconditions.checkArgument(accountIdentification != null, "accountIdentification can't be null");
        Preconditions.checkArgument(statementNumber != null, "statementNumber can't be null");

        {
            Preconditions.checkArgument(floorLimitIndicatorDebit != null, "floorLimitIndicatorDebit can't be null");
            DebitCreditMark debitMark = floorLimitIndicatorDebit.getDebitCreditMark().orElse(null);
            Preconditions.checkArgument(debitMark == null || debitMark == DEBIT,
                    "floorLimitIndicatorDebit mark can't be " + debitMark);

            Preconditions.checkArgument(floorLimitIndicatorCredit != null, "floorLimitIndicatorCredit can't be null");
            DebitCreditMark creditMark = floorLimitIndicatorCredit.getDebitCreditMark().orElse(null);
            Preconditions.checkArgument(creditMark == null || creditMark == CREDIT,
                    "floorLimitIndicatorCredit mark can't be " + creditMark);

            Preconditions.checkArgument((debitMark == null) == (creditMark == null),
                        "floorLimitIndicatorDebit and floorLimitIndicatorCredit marks need to be both blank or DEBIT and CREDIT");

            if (debitMark == null) {
                Preconditions.checkArgument(floorLimitIndicatorDebit.getAmount().equals(floorLimitIndicatorCredit.getAmount()),
                        "floorLimitIndicatorDebit and floorLimitIndicatorCredit amounts needs to be equal, if marks are blank");
            }
        }

        Preconditions.checkArgument(dateTimeIndicator != null, "dateTimeIndicator can't be null");
        Preconditions.checkArgument(transactionGroupList != null, "transactionGroupList can't be null");

        // ensure matching currencies
        CurrencyUnit statementCurrency = floorLimitIndicatorDebit.getAmount().getCurrencyUnit();
        String statementFundsCode = statementCurrency.getCode().substring(2, 3);

        {
            // check floorLimitIndicatorCredit currency
            CurrencyUnit currency = floorLimitIndicatorCredit.getAmount().getCurrencyUnit();
            Preconditions.checkArgument(currency.equals(statementCurrency), "floorLimitCreditCurrency '" + currency + "' does not match statement currency'" + statementCurrency + "'");
        }

        for (TransactionGroup transactionGroup : transactionGroupList) {
            if (transactionGroup.getStatementLine().getFundsCode().isPresent()) {
                String fundsCode = transactionGroup.getStatementLine().getFundsCode().get();
                Preconditions.checkArgument(fundsCode.equals(statementFundsCode), "statementLineFundsCode '" + fundsCode + "' does not match statement currency'" + statementCurrency + "'");
            }
        }

        if (transactionSummaryDebit != null) {
            CurrencyUnit currency = transactionSummaryDebit.getAmount().getCurrencyUnit();
            Preconditions.checkArgument(currency.equals(statementCurrency), "transactionSummaryDebitCurrency '" + currency + "' does not match statement currency'" + statementCurrency + "'");
        }

        if (transactionSummaryCredit != null) {
            CurrencyUnit currency = transactionSummaryCredit.getAmount().getCurrencyUnit();
            Preconditions.checkArgument(currency.equals(statementCurrency), "transactionSummaryCreditCurrency '" + currency + "' does not match statement currency'" + statementCurrency + "'");
        }

        this.transactionReferenceNumber = transactionReferenceNumber;
        this.relatedReference = Optional.ofNullable(relatedReference);
        this.accountIdentification = accountIdentification;
        this.statementNumber = statementNumber;
        this.floorLimitIndicatorDebit = floorLimitIndicatorDebit;
        this.floorLimitIndicatorCredit = floorLimitIndicatorCredit;
        this.dateTimeIndicator = dateTimeIndicator;
        this.transactionGroupList = transactionGroupList;
        this.transactionSummaryDebit = Optional.ofNullable(transactionSummaryDebit);
        this.transactionSummaryCredit = Optional.ofNullable(transactionSummaryCredit);
        this.informationToAccountOwner = Optional.ofNullable(informationToAccountOwner);
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

    public FloorLimitIndicator getFloorLimitIndicatorDebit() {
        return floorLimitIndicatorDebit;
    }

    public FloorLimitIndicator getFloorLimitIndicatorCredit() {
        return floorLimitIndicatorCredit;
    }

    public DateTimeIndicator getDateTimeIndicator() {
        return dateTimeIndicator;
    }

    public List<TransactionGroup> getTransactionGroupList() {
        return transactionGroupList;
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

    public String getContent() {
        StringBuilder contentBuilder = new StringBuilder();
        contentBuilder.append(swiftTextOf(transactionReferenceNumber)).append("\n");
        relatedReference.ifPresent(field -> contentBuilder.append(swiftTextOf(field)).append("\n"));
        contentBuilder.append(swiftTextOf(accountIdentification)).append("\n");
        contentBuilder.append(swiftTextOf(statementNumber)).append("\n");
        contentBuilder.append(swiftTextOf(floorLimitIndicatorDebit)).append("\n");
        if (!floorLimitIndicatorCredit.equals(floorLimitIndicatorDebit)) {
            contentBuilder.append(swiftTextOf(floorLimitIndicatorCredit)).append("\n");
        }
        contentBuilder.append(swiftTextOf(dateTimeIndicator)).append("\n");
        for (TransactionGroup transactionGroup : transactionGroupList) {
            contentBuilder.append(swiftTextOf(transactionGroup.getStatementLine())).append("\n");
            if (transactionGroup.getInformationToAccountOwner().isPresent()) {
                contentBuilder.append(swiftTextOf(transactionGroup.getInformationToAccountOwner().get())).append("\n");
            }
        }
        transactionSummaryDebit.ifPresent(field -> contentBuilder.append(swiftTextOf(field)).append("\n"));
        transactionSummaryCredit.ifPresent(field -> contentBuilder.append(swiftTextOf(field)).append("\n"));
        informationToAccountOwner.ifPresent(field -> contentBuilder.append(swiftTextOf(field)).append("\n"));
        contentBuilder.append(PageSeparator.TAG);
        return contentBuilder.toString();
    }
}
