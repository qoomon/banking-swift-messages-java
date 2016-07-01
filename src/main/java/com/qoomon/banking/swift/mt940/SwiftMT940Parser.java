package com.qoomon.banking.swift.mt940;

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
        for (GeneralMTField field : fieldList) {
            fieldNumber++;

            switch (field.getTag()) {
                case TransactionReferenceNumber.TAG: {
                    transactionReferenceNumber = new TransactionReferenceNumber(field);
                    break;
                }
                case RelatedReference.TAG: {
                    relatedReference = new RelatedReference(field);
                    break;
                }
                case AccountIdentification.TAG: {
                    accountIdentification = new AccountIdentification(field);
                    break;
                }
                case StatementNumber.TAG: {
                    statementNumber = new StatementNumber(field);
                    break;
                }
                case OpeningBalance.TAG_INTERMEDIATE:
                case OpeningBalance.TAG: {
                    openingBalance = new OpeningBalance(field);
                    break;
                }
                case StatementLine.TAG: {
                    StatementLine statementLine = new StatementLine(field);
                    // TODO add optional following new InformationToAccountOwner(field);
                    transactionList.add(new Transaction(statementLine, null));
                    break;
                }
                case ClosingBalance.TAG_INTERMEDIATE:
                case ClosingBalance.TAG: {
                    closingBalance = new ClosingBalance(field);
                    break;
                }
                case ClosingAvailableBalance.TAG: {
                    closingAvailableBalance = new ClosingAvailableBalance(field);
                    break;
                }
                case ForwardAvailableBalance.TAG: {
                    ForwardAvailableBalance forwardAvailableBalance = new ForwardAvailableBalance(field);
                    forwardAvailableBalanceList.add(forwardAvailableBalance);
                    break;
                }
                case InformationToAccountOwner.TAG: {
                    informationToAccountOwner = new InformationToAccountOwner(field);
                    break;
                }
                case SwiftMTFieldParser.SEPARATOR_FIELD_TAG: {
                    // handled below
                    break;
                }
                default:
                    new SwiftMT940ParserException("Parse error: unexpected field field", fieldNumber, field.getTag());

            }

            // finish message
            if (field.getTag().equals(SwiftMTFieldParser.SEPARATOR_FIELD_TAG) || fieldList.size() == fieldNumber) {
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

        }

        return result;

    }
}
