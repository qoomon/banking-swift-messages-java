package com.qoomon.banking.swift.message.submessage.mt942;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.qoomon.banking.swift.message.exception.SwiftMessageParseException;
import com.qoomon.banking.swift.message.submessage.exception.SubMessageParserException;
import com.qoomon.banking.swift.message.submessage.field.*;
import com.qoomon.banking.swift.message.submessage.field.exception.FieldParseException;

import java.io.Reader;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Parser for {@link SwiftMT942}
 */
public class SwiftMT942Reader {

    private final static Set<String> MESSAGE_START_FIELD_TAG_SET = ImmutableSet.of(TransactionReferenceNumber.FIELD_TAG_20);
    private final static Set<String> MESSAGE_END_FIELD_TAG_SET = ImmutableSet.of(PageSeperator.TAG);

    private final SwiftFieldReader fieldReader;

    private GeneralField previousField = null;
    private GeneralField currentField = null;
    private GeneralField nextField = null;


    public SwiftMT942Reader(Reader textReader) {

        Preconditions.checkArgument(textReader != null, "textReader can't be null");

        this.fieldReader = new SwiftFieldReader(textReader);
    }

    public SwiftMT942 readMessage() throws FieldParseException {
        try {
            if (currentField == null) {
                nextField = fieldReader.readField();
            }

            SwiftMT942 message = null;

            // message fields (builder) // TODO create builder
            TransactionReferenceNumber transactionReferenceNumber = null;
            RelatedReference relatedReference = null;
            AccountIdentification accountIdentification = null;
            StatementNumber statementNumber = null;
            FloorLimitIndicator floorLimitIndicatorDebit = null;
            FloorLimitIndicator floorLimitIndicatorCredit = null;
            DateTimeIndicator dateTimeIndicator = null;
            List<TransactionGroup> transactionList = new LinkedList<>();
            TransactionSummary transactionSummaryDebit = null;
            TransactionSummary transactionSummaryCredit = null;
            InformationToAccountOwner informationToAccountOwner = null;

            Set<String> nextValidFieldSet = MESSAGE_START_FIELD_TAG_SET;

            while (message == null && nextField != null) {

                ensureValidNextField(nextField, nextValidFieldSet, fieldReader);

                previousField = currentField;
                currentField = nextField;
                nextField = fieldReader.readField();

                switch (currentField.getTag()) {
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
                        if (floorLimitIndicatorDebit == null) {
                            floorLimitIndicatorDebit = FloorLimitIndicator.of(currentField);
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
                                PageSeperator.TAG);
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
                                PageSeperator.TAG);
                        break;
                    }
                    case TransactionSummary.FIELD_TAG_90D: {
                        transactionSummaryDebit = TransactionSummary.of(currentField);
                        nextValidFieldSet = ImmutableSet.of(
                                TransactionSummary.FIED_TAG_90C,
                                InformationToAccountOwner.FIELD_TAG_86,
                                PageSeperator.TAG);
                        break;
                    }
                    case TransactionSummary.FIED_TAG_90C: {
                        transactionSummaryCredit = TransactionSummary.of(currentField);
                        nextValidFieldSet = ImmutableSet.of(
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
                                    TransactionSummary.FIELD_TAG_90D,
                                    TransactionSummary.FIED_TAG_90C,
                                    InformationToAccountOwner.FIELD_TAG_86);
                        } else {
                            informationToAccountOwner = InformationToAccountOwner.of(currentField);
                            nextValidFieldSet = ImmutableSet.of(PageSeperator.TAG);
                        }
                        break;
                    }
                    case PageSeperator.TAG: {
                        nextValidFieldSet = ImmutableSet.of();
                        break;
                    }
                    default:
                        throw new SubMessageParserException("Parse error: unexpected field '" + currentField.getTag() + "'", fieldReader.getFieldLineNumber());

                }

                // finish message
                if ( MESSAGE_END_FIELD_TAG_SET.contains(currentField.getTag())) {
                    message = new SwiftMT942(
                            transactionReferenceNumber,
                            relatedReference,
                            accountIdentification,
                            statementNumber,
                            floorLimitIndicatorDebit,
                            floorLimitIndicatorCredit,
                            dateTimeIndicator,
                            transactionList,
                            transactionSummaryDebit,
                            transactionSummaryCredit,
                            informationToAccountOwner
                    );
                }
            }

            return message;
        } catch (Exception e) {
            if (e instanceof SubMessageParserException)
                throw (SubMessageParserException) e;
            throw new SubMessageParserException(e.getMessage(), fieldReader.getFieldLineNumber(), e);
        }
    }

    private void ensureValidNextField(GeneralField field, Set<String> expectedFieldTagSet, SwiftFieldReader fieldReader) throws SwiftMessageParseException {
        String fieldTag = field != null ? field.getTag() : null;
        if (!expectedFieldTagSet.contains(fieldTag)) {
            throw new SubMessageParserException("Expected Field '" + expectedFieldTagSet + "', but was '" + fieldTag + "'", fieldReader.getFieldLineNumber());
        }
    }
}
