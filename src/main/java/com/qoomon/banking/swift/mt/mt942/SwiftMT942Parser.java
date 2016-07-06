package com.qoomon.banking.swift.mt.mt942;

import com.google.common.collect.ImmutableSet;
import com.qoomon.banking.swift.field.*;
import com.qoomon.banking.swift.group.TransactionGroup;
import com.qoomon.banking.swift.mt.exception.SwiftMTParserException;

import java.io.Reader;
import java.text.ParseException;
import java.util.*;

/**
 * Parser for {@link SwiftMT942}
 */
public class SwiftMT942Parser {

    private final SwiftMTFieldParser swiftMTParser = new SwiftMTFieldParser();

    public List<SwiftMT942> parse(Reader mt940TextReader) throws ParseException {

        List<SwiftMT942> result = new LinkedList<>();

        List<GeneralMTField> fieldList = swiftMTParser.parse(mt940TextReader);

        TransactionReferenceNumber transactionReferenceNumber = null;
        RelatedReference relatedReference = null;
        AccountIdentification accountIdentification = null;
        StatementNumber statementNumber = null;
        FloorLimitIndicator floorLimitIndicatorDebitCredit = null;
        FloorLimitIndicator floorLimitIndicatorCredit = null;
        DateTimeIndicator dateTimeIndicator = null;
        List<TransactionGroup> transactionList = new LinkedList<>();
        TransactionSummary transactionSummaryDebit = null;
        TransactionSummary transactionSummaryCredit = null;
        InformationToAccountOwner informationToAccountOwner = null;

        int currentFieldNumber = 0;

        Set<String> currentValidFieldSet = ImmutableSet.of(TransactionReferenceNumber.TAG);

        GeneralMTField previousField = null;
        for (GeneralMTField currentField : fieldList) {
            Set<String> nextValidFieldSet;

            currentFieldNumber++;

            String currentFieldTag = currentField.getTag();
            switch (currentFieldTag) {
                case TransactionReferenceNumber.TAG: {
                    transactionReferenceNumber = TransactionReferenceNumber.of(currentField);
                    nextValidFieldSet = ImmutableSet.of(RelatedReference.TAG, AccountIdentification.TAG);
                    break;
                }
                case AccountIdentification.TAG: {
                    accountIdentification = AccountIdentification.of(currentField);
                    nextValidFieldSet = ImmutableSet.of(StatementNumber.TAG);
                    break;
                }
                case StatementNumber.TAG: {
                    statementNumber = StatementNumber.of(currentField);
                    nextValidFieldSet = ImmutableSet.of(FloorLimitIndicator.TAG);
                    break;
                }
                case FloorLimitIndicator.TAG: {
                    if (floorLimitIndicatorDebitCredit == null) {
                        floorLimitIndicatorDebitCredit = FloorLimitIndicator.of(currentField);
                        nextValidFieldSet = ImmutableSet.of(FloorLimitIndicator.TAG, DateTimeIndicator.TAG);
                    } else {
                        floorLimitIndicatorCredit = FloorLimitIndicator.of(currentField);
                        nextValidFieldSet = ImmutableSet.of(DateTimeIndicator.TAG);
                    }
                    break;
                }
                case DateTimeIndicator.TAG: {
                    dateTimeIndicator = DateTimeIndicator.of(currentField);
                    nextValidFieldSet = ImmutableSet.of(StatementLine.TAG, TransactionSummary.TAG_DEBIT, TransactionSummary.TAG_CREDIT, InformationToAccountOwner.TAG);
                    break;
                }
                case StatementLine.TAG: {
                    StatementLine statementLine = StatementLine.of(currentField);
                    transactionList.add(new TransactionGroup(statementLine, null));
                    nextValidFieldSet = ImmutableSet.of(StatementLine.TAG, TransactionSummary.TAG_DEBIT, TransactionSummary.TAG_CREDIT, InformationToAccountOwner.TAG);
                    break;
                }
                case TransactionSummary.TAG_DEBIT: {
                    transactionSummaryDebit = TransactionSummary.of(currentField);
                    nextValidFieldSet = ImmutableSet.of(TransactionSummary.TAG_CREDIT, InformationToAccountOwner.TAG);
                    break;
                }
                case TransactionSummary.TAG_CREDIT: {
                    transactionSummaryCredit = TransactionSummary.of(currentField);
                    nextValidFieldSet = ImmutableSet.of(InformationToAccountOwner.TAG);
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

                        nextValidFieldSet = ImmutableSet.of(StatementLine.TAG, TransactionSummary.TAG_DEBIT, TransactionSummary.TAG_CREDIT, InformationToAccountOwner.TAG);
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
                    throw new SwiftMTParserException("Parse error: unexpected field", currentFieldNumber, currentFieldTag);

            }

            if (!currentValidFieldSet.contains(currentFieldTag)) {
                throw new SwiftMTFieldParserException("Parse error: unexpected order of field " + currentFieldTag, currentFieldNumber);
            }

            // handle finishing message
            if (fieldList.size() == currentFieldNumber // last field
                    || currentFieldTag.equals(SwiftMTFieldParser.SEPARATOR_FIELD_TAG)) {

                result.add(new SwiftMT942(
                        transactionReferenceNumber,
                        relatedReference,
                        accountIdentification,
                        statementNumber,
                        floorLimitIndicatorDebitCredit,
                        floorLimitIndicatorCredit,
                        dateTimeIndicator,
                        transactionList,
                        transactionSummaryDebit,
                        transactionSummaryCredit,
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
