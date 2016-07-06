package com.qoomon.banking.swift.mt.mt942;

import com.google.common.collect.ImmutableSet;
import com.qoomon.banking.swift.field.*;
import com.qoomon.banking.swift.group.TransactionGroup;
import com.qoomon.banking.swift.mt.exception.SwiftMTParserException;

import java.io.Reader;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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

        Set<String> currentValidFieldSet = ImmutableSet.of(TransactionReferenceNumber.FIELD_TAG_20);

        GeneralMTField previousField = null;
        for (GeneralMTField currentField : fieldList) {
            Set<String> nextValidFieldSet;

            currentFieldNumber++;

            String currentFieldTag = currentField.getTag();
            switch (currentFieldTag) {
                case TransactionReferenceNumber.FIELD_TAG_20: {
                    transactionReferenceNumber = TransactionReferenceNumber.of(currentField);
                    nextValidFieldSet = ImmutableSet.of(RelatedReference.TAG, AccountIdentification.FIELD_TAG_25);
                    break;
                }
                case AccountIdentification.FIELD_TAG_25: {
                    accountIdentification = AccountIdentification.of(currentField);
                    nextValidFieldSet = ImmutableSet.of(StatementNumber.FIELD_TAG_28C);
                    break;
                }
                case StatementNumber.FIELD_TAG_28C: {
                    statementNumber = StatementNumber.of(currentField);
                    nextValidFieldSet = ImmutableSet.of(FloorLimitIndicator.FIELD_TAG_34F);
                    break;
                }
                case FloorLimitIndicator.FIELD_TAG_34F: {
                    if (floorLimitIndicatorDebitCredit == null) {
                        floorLimitIndicatorDebitCredit = FloorLimitIndicator.of(currentField);
                        nextValidFieldSet = ImmutableSet.of(FloorLimitIndicator.FIELD_TAG_34F, DateTimeIndicator.FIELD_TAG_13D);
                    } else {
                        floorLimitIndicatorCredit = FloorLimitIndicator.of(currentField);
                        nextValidFieldSet = ImmutableSet.of(DateTimeIndicator.FIELD_TAG_13D);
                    }
                    break;
                }
                case DateTimeIndicator.FIELD_TAG_13D: {
                    dateTimeIndicator = DateTimeIndicator.of(currentField);
                    nextValidFieldSet = ImmutableSet.of(StatementLine.FIELD_TAG_61, TransactionSummary.FIELD_TAG_90D, TransactionSummary.FIED_TAG_90C, InformationToAccountOwner.FIELD_TAG_86);
                    break;
                }
                case StatementLine.FIELD_TAG_61: {
                    StatementLine statementLine = StatementLine.of(currentField);
                    transactionList.add(new TransactionGroup(statementLine, null));
                    nextValidFieldSet = ImmutableSet.of(StatementLine.FIELD_TAG_61, TransactionSummary.FIELD_TAG_90D, TransactionSummary.FIED_TAG_90C, InformationToAccountOwner.FIELD_TAG_86);
                    break;
                }
                case TransactionSummary.FIELD_TAG_90D: {
                    transactionSummaryDebit = TransactionSummary.of(currentField);
                    nextValidFieldSet = ImmutableSet.of(TransactionSummary.FIED_TAG_90C, InformationToAccountOwner.FIELD_TAG_86);
                    break;
                }
                case TransactionSummary.FIED_TAG_90C: {
                    transactionSummaryCredit = TransactionSummary.of(currentField);
                    nextValidFieldSet = ImmutableSet.of(InformationToAccountOwner.FIELD_TAG_86);
                    break;
                }
                case InformationToAccountOwner.FIELD_TAG_86: {
                    if (previousField != null && previousField.getTag().equals(StatementLine.FIELD_TAG_61)) {
                        // amend transaction with transactionInformationToAccountOwner
                        int lastTransactionIndex = transactionList.size() - 1;
                        TransactionGroup lastTransaction = transactionList.get(lastTransactionIndex);
                        InformationToAccountOwner transactionInformationToAccountOwner = InformationToAccountOwner.of(currentField);

                        TransactionGroup updatedTransaction = new TransactionGroup(lastTransaction.getStatementLine(), transactionInformationToAccountOwner);
                        transactionList.set(lastTransactionIndex, updatedTransaction);

                        nextValidFieldSet = ImmutableSet.of(StatementLine.FIELD_TAG_61, TransactionSummary.FIELD_TAG_90D, TransactionSummary.FIED_TAG_90C, InformationToAccountOwner.FIELD_TAG_86);
                    } else {
                        informationToAccountOwner = InformationToAccountOwner.of(currentField);
                        nextValidFieldSet = ImmutableSet.of();
                    }
                    break;
                }
                case SwiftMTFieldParser.SEPARATOR_FIELD_TAG: {
                    // see below at finish message
                    nextValidFieldSet = ImmutableSet.of(TransactionReferenceNumber.FIELD_TAG_20);
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
