package com.qoomon.banking.swift.mt940;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.qoomon.banking.swift.field.*;

import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by qoomon on 27/06/16.
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

        int fieldNumber = 0;

        //TODO ensure fieldList right field order

        GeneralMTField previousField = null;
        for (GeneralMTField currentField : fieldList) {
            fieldNumber++;

            switch (currentField.getTag()) {
                case TransactionReferenceNumber.TAG: {
                    transactionReferenceNumber = new TransactionReferenceNumber(currentField);
                    break;
                }
                case RelatedReference.TAG: {
                    relatedReference = new RelatedReference(currentField);
                    break;
                }
                case AccountIdentification.TAG: {
                    accountIdentification = new AccountIdentification(currentField);
                    break;
                }
                case StatementNumber.TAG: {
                    statementNumber = new StatementNumber(currentField);
                    break;
                }
                case OpeningBalance.TAG_INTERMEDIATE:
                case OpeningBalance.TAG: {
                    openingBalance = new OpeningBalance(currentField);
                    break;
                }
                case StatementLine.TAG: {
                    StatementLine statementLine = new StatementLine(currentField);
                    transactionList.add(new Transaction(statementLine, null));
                    break;
                }
                case ClosingBalance.TAG_INTERMEDIATE:
                case ClosingBalance.TAG: {
                    closingBalance = new ClosingBalance(currentField);
                    break;
                }
                case ClosingAvailableBalance.TAG: {
                    closingAvailableBalance = new ClosingAvailableBalance(currentField);
                    break;
                }
                case ForwardAvailableBalance.TAG: {
                    ForwardAvailableBalance forwardAvailableBalance = new ForwardAvailableBalance(currentField);
                    forwardAvailableBalanceList.add(forwardAvailableBalance);
                    break;
                }
                case InformationToAccountOwner.TAG: {
                    if (previousField.getTag().equals(StatementLine.TAG)) {
                        int lastTransactionIndex = transactionList.size() - 1;
                        Transaction lastTransaction = transactionList.get(lastTransactionIndex);
                        InformationToAccountOwner transactionInformationToAccountOwner = new InformationToAccountOwner(currentField);
                        Transaction updatedTransaction = new Transaction(lastTransaction.getStatementLine(), transactionInformationToAccountOwner);
                        transactionList.set(lastTransactionIndex, updatedTransaction);
                    } else {
                        informationToAccountOwner = new InformationToAccountOwner(currentField);
                    }
                    break;
                }
                case SwiftMTFieldParser.SEPARATOR_FIELD_TAG: {
                    // see below at finish message
                    break;
                }
                default:
                    new SwiftMT940ParserException("Parse error: unexpected field", fieldNumber, currentField.getTag());

            }

            // finish message
            if (fieldList.size() == fieldNumber // last field
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

            previousField = currentField;
        }

        return result;

    }
}
