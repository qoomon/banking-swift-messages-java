package com.qoomon.banking.swift.message.submessage.mt942;

import com.google.common.collect.ImmutableSet;
import com.qoomon.banking.swift.message.submessage.field.*;
import com.qoomon.banking.swift.message.submessage.field.TransactionGroup;
import com.qoomon.banking.swift.message.submessage.exception.SwiftMTParserException;
import com.qoomon.banking.swift.message.submessage.field.exception.SwiftMTFieldParseException;

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

    public List<SwiftMT942> parse(Reader mt940TextReader) throws SwiftMTFieldParseException {

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
            try {
                Set<String> nextValidFieldSet;

                currentFieldNumber++;

                String currentFieldTag = currentField.getTag();
                switch (currentFieldTag) {
                    case TransactionReferenceNumber.FIELD_TAG_20: {
                        transactionReferenceNumber = TransactionReferenceNumber.of(currentField);
                        nextValidFieldSet = ImmutableSet.of(
                                RelatedReference.FIELD_TAG_21,
                                AccountIdentification.FIELD_TAG_25);
                        break;
                    }
                    case AccountIdentification.FIELD_TAG_25: {
                        accountIdentification = AccountIdentification.of(currentField);
                        nextValidFieldSet = ImmutableSet.of(
                                StatementNumber.FIELD_TAG_28C);
                        break;
                    }
                    case StatementNumber.FIELD_TAG_28C: {
                        statementNumber = StatementNumber.of(currentField);
                        nextValidFieldSet = ImmutableSet.of(
                                FloorLimitIndicator.FIELD_TAG_34F);
                        break;
                    }
                    case FloorLimitIndicator.FIELD_TAG_34F: {
                        if (floorLimitIndicatorDebitCredit == null) {
                            floorLimitIndicatorDebitCredit = FloorLimitIndicator.of(currentField);
                            nextValidFieldSet = ImmutableSet.of(
                                    FloorLimitIndicator.FIELD_TAG_34F,
                                    DateTimeIndicator.FIELD_TAG_13D);
                        } else {
                            floorLimitIndicatorCredit = FloorLimitIndicator.of(currentField);
                            nextValidFieldSet = ImmutableSet.of(DateTimeIndicator.FIELD_TAG_13D);
                        }
                        break;
                    }
                    case DateTimeIndicator.FIELD_TAG_13D: {
                        dateTimeIndicator = DateTimeIndicator.of(currentField);
                        nextValidFieldSet = ImmutableSet.of(
                                StatementLine.FIELD_TAG_61,
                                TransactionSummary.FIELD_TAG_90D,
                                TransactionSummary.FIED_TAG_90C,
                                InformationToAccountOwner.FIELD_TAG_86,
                                SwiftMTFieldParser.SEPARATOR_FIELD_TAG);
                        break;
                    }
                    case StatementLine.FIELD_TAG_61: {
                        StatementLine statementLine = StatementLine.of(currentField);
                        transactionList.add(new TransactionGroup(statementLine, null));
                        nextValidFieldSet = ImmutableSet.of(
                                StatementLine.FIELD_TAG_61,
                                TransactionSummary.FIELD_TAG_90D,
                                TransactionSummary.FIED_TAG_90C,
                                InformationToAccountOwner.FIELD_TAG_86,
                                SwiftMTFieldParser.SEPARATOR_FIELD_TAG);
                        break;
                    }
                    case TransactionSummary.FIELD_TAG_90D: {
                        transactionSummaryDebit = TransactionSummary.of(currentField);
                        nextValidFieldSet = ImmutableSet.of(
                                TransactionSummary.FIED_TAG_90C,
                                InformationToAccountOwner.FIELD_TAG_86,
                                SwiftMTFieldParser.SEPARATOR_FIELD_TAG);
                        break;
                    }
                    case TransactionSummary.FIED_TAG_90C: {
                        transactionSummaryCredit = TransactionSummary.of(currentField);
                        nextValidFieldSet = ImmutableSet.of(
                                InformationToAccountOwner.FIELD_TAG_86,
                                SwiftMTFieldParser.SEPARATOR_FIELD_TAG);
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

                            nextValidFieldSet = ImmutableSet.of(
                                    StatementLine.FIELD_TAG_61,
                                    TransactionSummary.FIELD_TAG_90D,
                                    TransactionSummary.FIED_TAG_90C,
                                    InformationToAccountOwner.FIELD_TAG_86);
                        } else {
                            informationToAccountOwner = InformationToAccountOwner.of(currentField);
                            nextValidFieldSet = ImmutableSet.of(
                                    SwiftMTFieldParser.SEPARATOR_FIELD_TAG);
                        }
                        break;
                    }
                    case SwiftMTFieldParser.SEPARATOR_FIELD_TAG: {
                        // see below at finish message
                        nextValidFieldSet = ImmutableSet.of(
                                TransactionReferenceNumber.FIELD_TAG_20);
                        break;
                    }
                    default:
                        throw new SwiftMTParserException("Unexpected field", currentFieldNumber, currentFieldTag);

                }

                if (!currentValidFieldSet.contains(currentField.getTag())) {
                    if (previousField == null) {
                        throw new SwiftMTParserException("Field " + currentField.getTag() + " is not allowed as first field", currentFieldNumber, currentField.getTag());
                    } else {
                        throw new SwiftMTParserException("Field " + currentField.getTag() + " is not allowed after field " + previousField.getTag(), currentFieldNumber, currentField.getTag());
                    }
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

                    transactionReferenceNumber = null;
                    relatedReference = null;
                    accountIdentification = null;
                    statementNumber = null;
                    floorLimitIndicatorDebitCredit = null;
                    floorLimitIndicatorCredit = null;
                    dateTimeIndicator = null;
                    transactionList = new LinkedList<>();
                    transactionSummaryDebit = null;
                    transactionSummaryCredit = null;
                    informationToAccountOwner = null;
                }

                // prepare for next iteration
                previousField = currentField;
                currentValidFieldSet = nextValidFieldSet;
            } catch (ParseException parseException) {
                throw new SwiftMTParserException("Subfield parse error", currentFieldNumber, currentField.getTag(), parseException);
            }
        }

        return result;

    }
}
