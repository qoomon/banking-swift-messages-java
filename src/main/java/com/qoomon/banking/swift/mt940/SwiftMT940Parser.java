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

        List<MTField> fieldList = swiftMTParser.parse(mt940TextReader);

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
        for (MTField field : fieldList) {
            fieldNumber++;

            switch (field.getTag()) {
                case TransactionReferenceNumber.TAG_20: {
                    String content = field.getContent();
                    transactionReferenceNumber = new TransactionReferenceNumber(content);
                    break;
                }
                case RelatedReference.TAG_21: {
                    String content = field.getContent();
                    relatedReference = new RelatedReference(content);
                    break;
                }
                case AccountIdentification.TAG_25: {
                    String content = field.getContent();
                    accountIdentification = new AccountIdentification(content);
                    break;
                }
                case StatementNumber.TAG_28C: {
                    String content = field.getContent();
                    statementNumber = new StatementNumber(content);
                    break;
                }
                case OpeningBalance.TAG_60F:
                case OpeningBalance.TAG_60M: {
                    OpeningBalance.Type type = field.getTag().equals(OpeningBalance.TAG_60F)
                            ? OpeningBalance.Type.OPENING : OpeningBalance.Type.INTERMEDIATE;
                    List<String> subFields = OpeningBalance.SWIFT_NOTATION.parse(field.getContent());
                    String debitCreditMark = subFields.get(0);
                    String date = subFields.get(1);
                    String currency = subFields.get(2);
                    String amount = subFields.get(3);
                    openingBalance = new OpeningBalance(type,
                            debitCreditMark,
                            date,
                            currency,
                            amount);
                    break;
                }

                case StatementLine.TAG_61: {
                    Transaction transaction = new Transaction();
                    transactionList.add(transaction);
                    break;
                }
                case ClosingBalance.TAG_62F:
                case ClosingBalance.TAG_62M: {
                    ClosingBalance.Type type = field.getTag().equals(OpeningBalance.TAG_60F)
                            ? ClosingBalance.Type.OPENING : ClosingBalance.Type.INTERMEDIATE;
                    List<String> subFields = ClosingBalance.SWIFT_NOTATION.parse(field.getContent());
                    String debitCreditMark = subFields.get(0);
                    String date = subFields.get(1);
                    String currency = subFields.get(2);
                    String amount = subFields.get(3);
                    closingBalance = new ClosingBalance(type,
                            debitCreditMark,
                            date,
                            currency,
                            amount);
                    break;
                }

                case ClosingAvailableBalance.TAG_64: {
                    String content = field.getContent();
                    closingAvailableBalance = new ClosingAvailableBalance(content);
                    break;
                }
                case ForwardAvailableBalance.TAG_65: {
                    String content = field.getContent();
                    ForwardAvailableBalance forwardAvailableBalance = new ForwardAvailableBalance(content);
                    forwardAvailableBalanceList.add(forwardAvailableBalance);
                    break;
                }
                case InformationToAccountOwner.TAG_86: {
                    String content = field.getContent();
                    informationToAccountOwner = new InformationToAccountOwner(content);
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
