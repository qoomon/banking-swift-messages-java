package com.qoomon.banking.swift.mt.mt940;

import com.google.common.collect.ImmutableSet;
import com.qoomon.banking.swift.field.*;
import com.qoomon.banking.swift.group.TransactionGroup;
import com.qoomon.banking.swift.mt.exception.SwiftMTParserException;

import java.io.Reader;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Parser for {@link SwiftMT940}
 */
public class SwiftMT940Parser {

    private final SwiftMTFieldParser swiftMTParser = new SwiftMTFieldParser();


    public List<SwiftMT940> parse(Reader mt940TextReader) {

        List<SwiftMT940> result = new LinkedList<>();

        List<GeneralMTField> fieldList = swiftMTParser.parse(mt940TextReader);

        TransactionReferenceNumber transactionReferenceNumber = null;
        RelatedReference relatedReference = null;
        AccountIdentification accountIdentification = null;
        StatementNumber statementNumber = null;
        OpeningBalance openingBalance = null;
        List<TransactionGroup> transactionList = new LinkedList<>();
        ClosingBalance closingBalance = null;
        ClosingAvailableBalance closingAvailableBalance = null;
        List<ForwardAvailableBalance> forwardAvailableBalanceList = new LinkedList<>();
        InformationToAccountOwner informationToAccountOwner = null;

        int currentFieldNumber = 0;

        Set<String> currentValidFieldSet = ImmutableSet.of(TransactionReferenceNumber.TAG);

        GeneralMTField previousField = null;
        for (GeneralMTField currentField : fieldList) {
            Set<String> nextValidFieldSet;

            currentFieldNumber++;

            switch (currentField.getTag()) {
                case TransactionReferenceNumber.TAG: {
                    transactionReferenceNumber = TransactionReferenceNumber.of(currentField);
                    nextValidFieldSet = ImmutableSet.of(RelatedReference.TAG, AccountIdentification.TAG);
                    break;
                }
                case RelatedReference.TAG: {
                    relatedReference = RelatedReference.of(currentField);
                    nextValidFieldSet = ImmutableSet.of(AccountIdentification.TAG);
                    break;
                }
                case AccountIdentification.TAG: {
                    accountIdentification = AccountIdentification.of(currentField);
                    nextValidFieldSet = ImmutableSet.of(StatementNumber.TAG);
                    break;
                }
                case StatementNumber.TAG: {
                    statementNumber = StatementNumber.of(currentField);
                    nextValidFieldSet = ImmutableSet.of(OpeningBalance.TAG, OpeningBalance.TAG_INTERMEDIATE);
                    break;
                }
                case OpeningBalance.TAG:
                case OpeningBalance.TAG_INTERMEDIATE: {
                    openingBalance = OpeningBalance.of(currentField);
                    nextValidFieldSet = ImmutableSet.of(StatementLine.TAG, ClosingBalance.TAG, ClosingBalance.TAG_INTERMEDIATE);
                    break;
                }
                case StatementLine.TAG: {
                    StatementLine statementLine = StatementLine.of(currentField);
                    transactionList.add(new TransactionGroup(statementLine, null));
                    nextValidFieldSet = ImmutableSet.of(InformationToAccountOwner.TAG, ClosingBalance.TAG, ClosingBalance.TAG_INTERMEDIATE);
                    break;
                }
                case ClosingBalance.TAG:
                case ClosingBalance.TAG_INTERMEDIATE: {
                    closingBalance = ClosingBalance.of(currentField);
                    nextValidFieldSet = ImmutableSet.of(ClosingAvailableBalance.TAG, ForwardAvailableBalance.TAG, InformationToAccountOwner.TAG);
                    break;
                }
                case ClosingAvailableBalance.TAG: {
                    closingAvailableBalance = ClosingAvailableBalance.of(currentField);
                    nextValidFieldSet = ImmutableSet.of(ForwardAvailableBalance.TAG, ClosingBalance.TAG_INTERMEDIATE);
                    break;
                }
                case ForwardAvailableBalance.TAG: {
                    ForwardAvailableBalance forwardAvailableBalance = ForwardAvailableBalance.of(currentField);
                    forwardAvailableBalanceList.add(forwardAvailableBalance);
                    nextValidFieldSet = ImmutableSet.of(ClosingBalance.TAG_INTERMEDIATE);
                    break;
                }
                case InformationToAccountOwner.TAG: {
                    if (previousField != null && previousField.getTag().equals(StatementLine.TAG)) {
                        // amend transaction with transactionInformationToAccountOwner
                        int lastTransactionIndex = transactionList.size() - 1;
                        TransactionGroup lastTransaction = transactionList.get(lastTransactionIndex);
                        InformationToAccountOwner transactionInformationToAccountOwner = InformationToAccountOwner.of(currentField);

                        TransactionGroup updatedTransaction = new TransactionGroup(lastTransaction.getStatementLine(), transactionInformationToAccountOwner);
                        transactionList.set(lastTransactionIndex, updatedTransaction);

                        nextValidFieldSet = ImmutableSet.of(StatementLine.TAG, ClosingBalance.TAG, ClosingBalance.TAG_INTERMEDIATE);
                    } else {
                        informationToAccountOwner = InformationToAccountOwner.of(currentField);
                        nextValidFieldSet = ImmutableSet.of();
                    }
                    break;
                }
                case SwiftMTFieldParser.SEPARATOR_FIELD_TAG: {
                    // see below at finish message
                    nextValidFieldSet = ImmutableSet.of(TransactionReferenceNumber.TAG);
                    break;
                }
                default:
                    throw new SwiftMTParserException("Parse error: unexpected field", currentFieldNumber, currentField.getTag());
            }

            if (!currentValidFieldSet.contains(currentField.getTag())) {
                if (previousField == null) {
                    throw new SwiftMTFieldParserException("Parse error: field " + currentField.getTag() + " is not allowed as first field", currentFieldNumber);
                } else {
                    throw new SwiftMTFieldParserException("Parse error: field " + currentField.getTag() + " not allowed after field " + previousField.getTag(), currentFieldNumber);
                }
            }

            // handle finishing message
            if (fieldList.size() == currentFieldNumber // last field
                    || currentField.getTag().equals(SwiftMTFieldParser.SEPARATOR_FIELD_TAG)) {

                result.add(new SwiftMT940(
                        transactionReferenceNumber,
                        relatedReference,
                        accountIdentification,
                        statementNumber,
                        openingBalance,
                        transactionList,
                        closingBalance,
                        closingAvailableBalance,
                        forwardAvailableBalanceList,
                        informationToAccountOwner
                ));
            }

            // prepare for next iteration
            previousField = currentField;
            currentValidFieldSet = nextValidFieldSet;
        }

        return result;

    }
}
