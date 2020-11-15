package com.qoomon.banking.swift.submessage.mt103;

import com.google.common.base.Preconditions;
import com.qoomon.banking.swift.submessage.PageSeparator;
import com.qoomon.banking.swift.submessage.field.*;
import org.joda.money.CurrencyUnit;

import java.util.List;
import java.util.Optional;

import static com.qoomon.banking.swift.submessage.field.FieldUtils.swiftTextOf;

/**
 * Created by qoomon on 04/11/20.
 *
 * @see <a href="https://www.paiementor.com/swift-mt103-format-specifications/">https://www.paiementor.com/swift-mt103-format-specifications/</a>
 */
public class MT103Page {

    public static final String MESSAGE_ID_940 = "940";

    /**
     * @see TransactionReferenceNumber#FIELD_TAG_20
     */
    private final TransactionReferenceNumber transactionReferenceNumber;

    /**
     * @see TimeIndicator#FIELD_TAG_13C
     */
    private final Optional<TimeIndicator> timeIndicator;

    /**
     * @see BankOperation#FIELD_TAG_23B
     */
    private final BankOperation bankOperation;


    public MT103Page(
            TransactionReferenceNumber transactionReferenceNumber,
            TimeIndicator timeIndicator,
            BankOperation bankOperation) {

        Preconditions.checkArgument(bankOperation != null, "bankOperation can't be null");

        // ensure matching currency TODO
//        CurrencyUnit statementCurrency = openingBalance.getAmount().getCurrencyUnit();
//        String statementFundsCode = statementCurrency.getCode().substring(2, 3);
//
//        for (TransactionGroup transactionGroup : transactionGroupList) {
//            if (transactionGroup.getStatementLine().getFundsCode().isPresent()){
//                String fundsCode = transactionGroup.getStatementLine().getFundsCode().get();
//                Preconditions.checkArgument(fundsCode.equals(statementFundsCode), "statementLineFundsCode '" + fundsCode + "' does not match statement currency'" + statementCurrency + "'");
//            }
//        }
//
//        if(closingBalance != null){
//            CurrencyUnit currency = closingBalance.getAmount().getCurrencyUnit();
//            Preconditions.checkArgument(currency.equals(statementCurrency), "closingBalanceCurrency '" + currency + "' does not match statement currency'" + statementCurrency + "'");
//        }
//
//        if(closingAvailableBalance != null){
//            CurrencyUnit currency = closingAvailableBalance.getAmount().getCurrencyUnit();
//            Preconditions.checkArgument(currency.equals(statementCurrency), "closingAvailableBalanceCurrency '" + currency + "' does not match statement currency'" + statementCurrency + "'");
//        }
//
//        for (ForwardAvailableBalance forwardAvailableBalance : forwardAvailableBalanceList) {
//            CurrencyUnit currency = forwardAvailableBalance.getAmount().getCurrencyUnit();
//            Preconditions.checkArgument(currency.equals(statementCurrency), "forwardAvailableBalance '" + currency + "' does not match statement currency'" + statementCurrency + "'");
//        }

        this.transactionReferenceNumber = transactionReferenceNumber;
        this.timeIndicator = Optional.ofNullable(timeIndicator);
        this.bankOperation = bankOperation;

    }

    public TransactionReferenceNumber getTransactionReferenceNumber() {
        return transactionReferenceNumber;
    }

    public Optional<TimeIndicator> getTimeIndicator() {
        return timeIndicator;
    }

    public BankOperation getBankOperation() {
        return bankOperation;
    }

    public String getContent() {
        StringBuilder contentBuilder = new StringBuilder();
        contentBuilder.append(swiftTextOf(transactionReferenceNumber)).append("\n");
        if (timeIndicator.isPresent()) {
            contentBuilder.append(swiftTextOf(timeIndicator.get())).append("\n");
        }
        contentBuilder.append(swiftTextOf(bankOperation)).append("\n");
//        contentBuilder.append(swiftTextOf(statementNumber)).append("\n");
//        contentBuilder.append(swiftTextOf(openingBalance)).append("\n");
//        for (TransactionGroup transactionGroup : transactionGroupList) {
//            contentBuilder.append(swiftTextOf(transactionGroup.getStatementLine())).append("\n");
//            if (transactionGroup.getInformationToAccountOwner().isPresent()) {
//                contentBuilder.append(swiftTextOf(transactionGroup.getInformationToAccountOwner().get())).append("\n");
//            }
//        }
//        contentBuilder.append(swiftTextOf(closingBalance)).append("\n");
//        if (closingAvailableBalance.isPresent()) {
//            contentBuilder.append(swiftTextOf(closingAvailableBalance.get())).append("\n");
//        }
//        for (ForwardAvailableBalance forwardAvailableBalance : forwardAvailableBalanceList) {
//            contentBuilder.append(swiftTextOf(forwardAvailableBalance)).append("\n");
//        }
//        if (informationToAccountOwner.isPresent()) {
//            contentBuilder.append(swiftTextOf(informationToAccountOwner.get())).append("\n");
//        }
        contentBuilder.append(PageSeparator.TAG);
        return contentBuilder.toString();
    }
}
