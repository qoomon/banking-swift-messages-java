package com.qoomon.banking.swift.submessage.mt942;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.qoomon.banking.swift.message.exception.SwiftMessageParseException;
import com.qoomon.banking.swift.submessage.exception.PageParserException;
import com.qoomon.banking.swift.submessage.field.*;

import java.io.Reader;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Parser for {@link MT942Page}
 */
public class MT942PageReader {

    private static final Set<String> MESSAGE_START_FIELD_TAG_SET = ImmutableSet.of(TransactionReferenceNumber.FIELD_TAG_20);

    private static final Set<String> MESSAGE_END_FIELD_TAG_SET = ImmutableSet.of(PageSeparator.TAG);

    private final SwiftFieldReader fieldReader;

    private GeneralField currentField = null;

    private GeneralField nextField = null;


    public MT942PageReader(Reader textReader) {

        Preconditions.checkArgument(textReader != null, "textReader can't be null");

        this.fieldReader = new SwiftFieldReader(textReader);
    }

    public List<MT942Page> readAll() throws SwiftMessageParseException {
        List<MT942Page> result = new LinkedList<>();
        MT942Page page;
        while ((page = read()) != null) {
            result.add(page);
        }
        return result;
    }

    public MT942Page read() throws SwiftMessageParseException {
        try {
            if (currentField == null) {
                nextField = fieldReader.readField();
            }

            MT942Page page = null;

            // message fields
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

            while (page == null && nextField != null) {

                ensureValidNextField(nextField, nextValidFieldSet, fieldReader);

                GeneralField previousField = currentField;
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
                                TransactionSummary.FIELD_TAG_90C,
                                InformationToAccountOwner.FIELD_TAG_86,
                                PageSeparator.TAG);
                        break;
                    }
                    case StatementLine.FIELD_TAG_61: {
                        StatementLine statementLine = StatementLine.of(currentField);
                        transactionList.add(new TransactionGroup(statementLine, null));
                        nextValidFieldSet = ImmutableSet.of(
                                StatementLine.FIELD_TAG_61,
                                TransactionSummary.FIELD_TAG_90D,
                                TransactionSummary.FIELD_TAG_90C,
                                InformationToAccountOwner.FIELD_TAG_86,
                                PageSeparator.TAG);
                        break;
                    }
                    case TransactionSummary.FIELD_TAG_90D: {
                        transactionSummaryDebit = TransactionSummary.of(currentField);
                        nextValidFieldSet = ImmutableSet.of(
                                TransactionSummary.FIELD_TAG_90C,
                                InformationToAccountOwner.FIELD_TAG_86,
                                PageSeparator.TAG);
                        break;
                    }
                    case TransactionSummary.FIELD_TAG_90C: {
                        transactionSummaryCredit = TransactionSummary.of(currentField);
                        nextValidFieldSet = ImmutableSet.of(
                                InformationToAccountOwner.FIELD_TAG_86,
                                PageSeparator.TAG);
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
                                    TransactionSummary.FIELD_TAG_90C,
                                    InformationToAccountOwner.FIELD_TAG_86);
                        } else {
                            informationToAccountOwner = InformationToAccountOwner.of(currentField);
                            nextValidFieldSet = ImmutableSet.of(PageSeparator.TAG);
                        }
                        break;
                    }
                    case PageSeparator.TAG: {
                        nextValidFieldSet = ImmutableSet.of();
                        break;
                    }
                    default:
                        throw new PageParserException("Parse error: unexpected field '" + currentField.getTag() + "'", fieldReader.getFieldLineNumber());

                }

                // finish message
                if (MESSAGE_END_FIELD_TAG_SET.contains(currentField.getTag())) {
                    page = new MT942Page(
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
                } else if (nextField == null) {
                    throw new PageParserException("Unfinished page. Missing page delimiter " + MESSAGE_END_FIELD_TAG_SET, fieldReader.getFieldLineNumber());
                }
            }

            return page;
        } catch (SwiftMessageParseException e) {
            throw e;
        } catch (Exception e) {
            throw new SwiftMessageParseException(e.getMessage(), fieldReader.getFieldLineNumber(), e);
        }
    }

    private void ensureValidNextField(GeneralField field, Set<String> expectedFieldTagSet, SwiftFieldReader fieldReader) throws SwiftMessageParseException {
        String fieldTag = field != null ? field.getTag() : null;
        if (!expectedFieldTagSet.contains(fieldTag)) {
            throw new PageParserException("Expected Field '" + expectedFieldTagSet + "', but was '" + fieldTag + "'", fieldReader.getFieldLineNumber());
        }
    }
}
