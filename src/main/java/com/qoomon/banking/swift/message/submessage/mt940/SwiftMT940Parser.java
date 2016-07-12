package com.qoomon.banking.swift.message.submessage.mt940;

import com.google.common.collect.ImmutableSet;
import com.qoomon.banking.swift.message.submessage.exception.SubMessageParserException;
import com.qoomon.banking.swift.message.submessage.field.*;
import com.qoomon.banking.swift.message.submessage.field.exception.FieldParseException;

import java.io.Reader;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Parser for {@link SwiftMT940}
 */
public class SwiftMT940Parser {


    public List<SwiftMT940> parse(Reader textReader) throws FieldParseException {

        List<SwiftMT940> result = new LinkedList<>();

        boolean buildMessageInProgress = false;
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

        Set<String> currentValidFieldSet = ImmutableSet.of(TransactionReferenceNumber.FIELD_TAG_20);

        SwiftFieldReader swiftFieldReader = new SwiftFieldReader(textReader);
        GeneralField previousField = null;
        GeneralField currentField;
        while ((currentField = swiftFieldReader.readField()) != null) {
            buildMessageInProgress = true;
            try {
                Set<String> nextValidFieldSet;

                switch (currentField.getTag()) {
                    case TransactionReferenceNumber.FIELD_TAG_20: {
                        transactionReferenceNumber = TransactionReferenceNumber.of(currentField);
                        nextValidFieldSet = ImmutableSet.of(
                                RelatedReference.FIELD_TAG_21,
                                AccountIdentification.FIELD_TAG_25);
                        break;
                    }
                    case RelatedReference.FIELD_TAG_21: {
                        relatedReference = RelatedReference.of(currentField);
                        nextValidFieldSet = ImmutableSet.of(
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
                                OpeningBalance.FIELD_TAG_60F,
                                OpeningBalance.FIELD_TAG_60M);
                        break;
                    }
                    case OpeningBalance.FIELD_TAG_60F:
                    case OpeningBalance.FIELD_TAG_60M: {
                        openingBalance = OpeningBalance.of(currentField);
                        nextValidFieldSet = ImmutableSet.of(
                                StatementLine.FIELD_TAG_61,
                                ClosingBalance.FIELD_TAG_62F,
                                ClosingBalance.FIELD_TAG_62M);
                        break;
                    }
                    case StatementLine.FIELD_TAG_61: {
                        StatementLine statementLine = StatementLine.of(currentField);
                        transactionList.add(new TransactionGroup(statementLine, null));
                        nextValidFieldSet = ImmutableSet.of(
                                InformationToAccountOwner.FIELD_TAG_86,
                                StatementLine.FIELD_TAG_61,
                                ClosingBalance.FIELD_TAG_62F,
                                ClosingBalance.FIELD_TAG_62M);
                        break;
                    }
                    case ClosingBalance.FIELD_TAG_62F:
                    case ClosingBalance.FIELD_TAG_62M: {
                        closingBalance = ClosingBalance.of(currentField);
                        nextValidFieldSet = ImmutableSet.of(
                                ClosingAvailableBalance.FIELD_TAG_64,
                                ForwardAvailableBalance.FIELD_TAG_65,
                                InformationToAccountOwner.FIELD_TAG_86,
                                PageSeperator.TAG);
                        break;
                    }
                    case ClosingAvailableBalance.FIELD_TAG_64: {
                        closingAvailableBalance = ClosingAvailableBalance.of(currentField);
                        nextValidFieldSet = ImmutableSet.of(
                                ForwardAvailableBalance.FIELD_TAG_65,
                                InformationToAccountOwner.FIELD_TAG_86,
                                PageSeperator.TAG);
                        break;
                    }
                    case ForwardAvailableBalance.FIELD_TAG_65: {
                        ForwardAvailableBalance forwardAvailableBalance = ForwardAvailableBalance.of(currentField);
                        forwardAvailableBalanceList.add(forwardAvailableBalance);
                        nextValidFieldSet = ImmutableSet.of(
                                ForwardAvailableBalance.FIELD_TAG_65,
                                InformationToAccountOwner.FIELD_TAG_86,
                                PageSeperator.TAG);
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
                                    ClosingBalance.FIELD_TAG_62F,
                                    ClosingBalance.FIELD_TAG_62M);
                        } else {
                            informationToAccountOwner = InformationToAccountOwner.of(currentField);
                            nextValidFieldSet = ImmutableSet.of(PageSeperator.TAG);
                        }
                        break;
                    }
                    case PageSeperator.TAG: {
                        // see below at finish message
                        nextValidFieldSet = ImmutableSet.of(
                                TransactionReferenceNumber.FIELD_TAG_20);
                        break;
                    }
                    default:
                        throw new SubMessageParserException("Parse error: unexpected field", swiftFieldReader.getFieldLineNumber(), currentField.getTag());
                }

                if (!currentValidFieldSet.contains(currentField.getTag())) {
                    if (previousField == null) {
                        throw new SubMessageParserException("Field " + currentField.getTag() + " is not allowed as first field", swiftFieldReader.getFieldLineNumber(), currentField.getTag());
                    } else {
                        throw new SubMessageParserException("Field " + currentField.getTag() + " is not allowed after field " + previousField.getTag(), swiftFieldReader.getFieldLineNumber(), currentField.getTag());
                    }
                }

                // handle finishing message
                if (currentField.getTag().equals(PageSeperator.TAG)) {
                    buildMessageInProgress = false;
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

                    transactionReferenceNumber = null;
                    relatedReference = null;
                    accountIdentification = null;
                    statementNumber = null;
                    openingBalance = null;
                    transactionList = new LinkedList<>();
                    closingBalance = null;
                    closingAvailableBalance = null;
                    forwardAvailableBalanceList = new LinkedList<>();
                    informationToAccountOwner = null;
                }

                // prepare for next iteration
                previousField = currentField;
                currentValidFieldSet = nextValidFieldSet;

            } catch (Exception parseException) {
                throw new SubMessageParserException("Subfield parse error", swiftFieldReader.getFieldLineNumber(), currentField.getTag(), parseException);
            }
        }

        if (buildMessageInProgress) {
            throw new SubMessageParserException("Unfinished Message", swiftFieldReader.getFieldLineNumber(), "n/a");
        }

        return result;

    }
}
