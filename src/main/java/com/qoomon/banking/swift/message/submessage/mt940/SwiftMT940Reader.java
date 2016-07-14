package com.qoomon.banking.swift.message.submessage.mt940;

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
 * Parser for {@link SwiftMT940}
 */
public class SwiftMT940Reader {

    private static final Set<String> MESSAGE_START_FIELD_TAG_SET = ImmutableSet.of(TransactionReferenceNumber.FIELD_TAG_20);
    private static final Set<String> MESSAGE_END_FIELD_TAG_SET = ImmutableSet.of(PageSeperator.TAG);

    private final SwiftFieldReader fieldReader;

    private GeneralField currentField = null;
    private GeneralField nextField = null;


    public SwiftMT940Reader(Reader textReader) {

        Preconditions.checkArgument(textReader != null, "textReader can't be null");

        this.fieldReader = new SwiftFieldReader(textReader);
    }

    public SwiftMT940 readMessage() throws SwiftMessageParseException {
        try {
            if (currentField == null) {
                nextField = fieldReader.readField();
            }

            SwiftMT940 message = null;

            // message fields
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

            Set<String> nextValidFieldSet = MESSAGE_START_FIELD_TAG_SET;

            while (message == null && nextField != null) {

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
                        nextValidFieldSet = ImmutableSet.of();
                        break;
                    }
                    default:
                        throw new SubMessageParserException("Parse error: unexpected field '" + currentField.getTag() + "'", fieldReader.getFieldLineNumber());
                }

                // finish message
                if (MESSAGE_END_FIELD_TAG_SET.contains(currentField.getTag())) {
                    message = new SwiftMT940(
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
                    );
                }
            }

            return message;
        } catch (SwiftMessageParseException e) {
            throw e;
        } catch (Exception e) {
            throw new SwiftMessageParseException(e.getMessage(), fieldReader.getFieldLineNumber(), e);
        }
    }

    private void ensureValidNextField(GeneralField field, Set<String> expectedFieldTagSet, SwiftFieldReader fieldReader) throws SwiftMessageParseException {
        String fieldTag = field != null ? field.getTag() : null;
        if (!expectedFieldTagSet.contains(fieldTag)) {
            throw new SubMessageParserException("Expected Field '" + expectedFieldTagSet + "', but was '" + fieldTag + "'", fieldReader.getFieldLineNumber());
        }
    }
}
