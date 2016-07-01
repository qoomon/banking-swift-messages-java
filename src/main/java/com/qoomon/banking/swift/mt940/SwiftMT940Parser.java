package com.qoomon.banking.swift.mt940;

import com.google.common.collect.ImmutableSet;
import com.qoomon.banking.swift.field.*;

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
        List<Transaction> transactionList = new LinkedList<>();
        ClosingBalance closingBalance = null;
        ClosingAvailableBalance closingAvailableBalance = null;
        List<ForwardAvailableBalance> forwardAvailableBalanceList = new LinkedList<>();
        InformationToAccountOwner informationToAccountOwner = null;

        int currentFieldNumber = 0;

        //TODO ensure fieldList right field order
        Set<String> currentValidFieldSet = ImmutableSet.of(TransactionReferenceNumber.TAG);

        GeneralMTField previousField = null;
        for (GeneralMTField currentField : fieldList) {
            Set<String> nextValidFieldSet;

            currentFieldNumber++;


            String currentFieldTag = currentField.getTag();
            switch (currentFieldTag) {
                case TransactionReferenceNumber.TAG: {
                    transactionReferenceNumber = new TransactionReferenceNumber(currentField);
                    nextValidFieldSet = ImmutableSet.of(RelatedReference.TAG, AccountIdentification.TAG);
                    break;
                }
                case RelatedReference.TAG: {
                    relatedReference = new RelatedReference(currentField);
                    nextValidFieldSet = ImmutableSet.of(AccountIdentification.TAG);
                    break;
                }
                case AccountIdentification.TAG: {
                    accountIdentification = new AccountIdentification(currentField);
                    nextValidFieldSet = ImmutableSet.of(StatementNumber.TAG);
                    break;
                }
                case StatementNumber.TAG: {
                    statementNumber = new StatementNumber(currentField);
                    nextValidFieldSet = ImmutableSet.of(OpeningBalance.TAG, OpeningBalance.TAG_INTERMEDIATE);
                    break;
                }
                case OpeningBalance.TAG:
                case OpeningBalance.TAG_INTERMEDIATE: {
                    openingBalance = new OpeningBalance(currentField);
                    nextValidFieldSet = ImmutableSet.of(StatementLine.TAG, ClosingBalance.TAG, ClosingBalance.TAG_INTERMEDIATE);
                    break;
                }
                case StatementLine.TAG: {
                    StatementLine statementLine = new StatementLine(currentField);
                    transactionList.add(new Transaction(statementLine, null));
                    nextValidFieldSet = ImmutableSet.of(InformationToAccountOwner.TAG, ClosingBalance.TAG, ClosingBalance.TAG_INTERMEDIATE);
                    break;
                }
                case ClosingBalance.TAG:
                case ClosingBalance.TAG_INTERMEDIATE: {
                    closingBalance = new ClosingBalance(currentField);
                    nextValidFieldSet = ImmutableSet.of(ClosingAvailableBalance.TAG, ForwardAvailableBalance.TAG, ClosingBalance.TAG_INTERMEDIATE);
                    break;
                }
                case ClosingAvailableBalance.TAG: {
                    closingAvailableBalance = new ClosingAvailableBalance(currentField);
                    nextValidFieldSet = ImmutableSet.of(ForwardAvailableBalance.TAG, ClosingBalance.TAG_INTERMEDIATE);
                    break;
                }
                case ForwardAvailableBalance.TAG: {
                    ForwardAvailableBalance forwardAvailableBalance = new ForwardAvailableBalance(currentField);
                    forwardAvailableBalanceList.add(forwardAvailableBalance);
                    nextValidFieldSet = ImmutableSet.of(ClosingBalance.TAG_INTERMEDIATE);
                    break;
                }
                case InformationToAccountOwner.TAG: {
                    if (previousField != null && previousField.getTag().equals(StatementLine.TAG)) {
                        // amend transaction with transactionInformationToAccountOwner
                        int lastTransactionIndex = transactionList.size() - 1;
                        Transaction lastTransaction = transactionList.get(lastTransactionIndex);
                        InformationToAccountOwner transactionInformationToAccountOwner = new InformationToAccountOwner(currentField);

                        Transaction updatedTransaction = new Transaction(lastTransaction.getStatementLine(), transactionInformationToAccountOwner);
                        transactionList.set(lastTransactionIndex, updatedTransaction);
                        
                        nextValidFieldSet = ImmutableSet.of(StatementLine.TAG, ClosingBalance.TAG, ClosingBalance.TAG_INTERMEDIATE);
                    } else {
                        informationToAccountOwner = new InformationToAccountOwner(currentField);
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
                    throw new SwiftMT940ParserException("Parse error: unexpected field", currentFieldNumber, currentFieldTag);

            }

            if (!currentValidFieldSet.contains(currentFieldTag)) {
                throw new SwiftMTFieldParserException("Parse error: unexpected order of field " + currentFieldTag, currentFieldNumber);
            }

            // handle finishing message
            if (fieldList.size() == currentFieldNumber // last field
                    || currentFieldTag.equals(SwiftMTFieldParser.SEPARATOR_FIELD_TAG)) {

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
