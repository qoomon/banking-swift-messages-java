package com.qoomon.banking.swift.submessage.mt942;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.qoomon.banking.swift.message.exception.SwiftMessageParseException;
import com.qoomon.banking.swift.submessage.PageReader;
import com.qoomon.banking.swift.submessage.PageSeparator;
import com.qoomon.banking.swift.submessage.exception.PageParserException;
import com.qoomon.banking.swift.submessage.field.*;
import com.qoomon.banking.swift.submessage.field.subfield.DebitCreditMark;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;

import java.io.Reader;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static com.qoomon.banking.swift.submessage.field.subfield.DebitCreditMark.CREDIT;
import static com.qoomon.banking.swift.submessage.field.subfield.DebitCreditMark.DEBIT;

/**
 * Parser for {@link MT942Page}
 */
public class MT942PageReader extends PageReader<MT942Page> {

    private final SwiftFieldReader fieldReader;


    public MT942PageReader(Reader textReader) {

        Preconditions.checkArgument(textReader != null, "textReader can't be null");

        this.fieldReader = new SwiftFieldReader(textReader);
    }

    @Override
    public MT942Page read() throws SwiftMessageParseException {
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

        try {
            Set<String> nextValidFieldSet = ImmutableSet.of(TransactionReferenceNumber.FIELD_TAG_20);
            GeneralField currentField = null;
            while (true) {
                GeneralField previousField = currentField;
                currentField = fieldReader.readField();
                if (currentField == null && previousField == null) {
                    return null;
                }

                ensureValidField(currentField, nextValidFieldSet, fieldReader);
                if (currentField.getTag().equals(PageSeparator.TAG)) {
                    break;
                }

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
                                FloorLimitIndicator.FIELD_TAG_34F);
                        break;
                    }
                    case FloorLimitIndicator.FIELD_TAG_34F: {
                        FloorLimitIndicator floorLimitIndicator = FloorLimitIndicator.of(currentField);
                        DebitCreditMark debitCreditMark = floorLimitIndicator.getDebitCreditMark().orElse(null);

                        // second occurrence of field 34F
                        if (floorLimitIndicatorDebit != null) {
                            if (debitCreditMark != CREDIT) {
                                throw new PageParserException(
                                        "Expected Field '" + DateTimeIndicator.FIELD_TAG_13D + " (second occurrence) with CREDIT mark'," +
                                                " but mark was '" + (debitCreditMark) + "'", fieldReader.getFieldLineNumber());
                            }
                        }

                        if (debitCreditMark != null) {
                            CurrencyUnit currencyUnit = floorLimitIndicator.getAmount().getCurrencyUnit();
                            switch (debitCreditMark) {
                                case DEBIT: {
                                    floorLimitIndicatorDebit = floorLimitIndicator;
                                    // preset optional credit floor indicator
                                    floorLimitIndicatorCredit = new FloorLimitIndicator(CREDIT,
                                            BigMoney.zero(currencyUnit));
                                    nextValidFieldSet = ImmutableSet.of(
                                            FloorLimitIndicator.FIELD_TAG_34F,
                                            DateTimeIndicator.FIELD_TAG_13D);
                                    break;
                                }
                                case CREDIT: {
                                    floorLimitIndicatorCredit = floorLimitIndicator;
                                    // handle missing debit floor indicator
                                    if (floorLimitIndicatorDebit == null) {
                                        floorLimitIndicatorDebit = new FloorLimitIndicator(DEBIT,
                                                BigMoney.zero(currencyUnit));
                                    }
                                    nextValidFieldSet = ImmutableSet.of(
                                            DateTimeIndicator.FIELD_TAG_13D);
                                    break;
                                }
                                default:
                                    throw new IllegalStateException("Unexpected value: " + debitCreditMark);
                            }
                        } else {
                            floorLimitIndicatorDebit = floorLimitIndicator;
                            floorLimitIndicatorCredit = floorLimitIndicator;
                            nextValidFieldSet = ImmutableSet.of(
                                    DateTimeIndicator.FIELD_TAG_13D);
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
            }

            return new MT942Page(
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
        } catch (
                Exception e) {
            throw new SwiftMessageParseException(e.getMessage(), fieldReader.getFieldLineNumber(), e);
        }

    }
}
