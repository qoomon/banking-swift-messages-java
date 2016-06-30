package com.qoomon.banking.swift.field;

import java.util.regex.Pattern;

/**
 * Statement Line Format: 6!n[4!n]2a[1!a]15d1!a3!c16x[//16x]
 * <p>
 * a = alphabetic, A through Z, upper case only - [A-Z]
 * n = numeric digits, 0 through 9 only - [0-9]
 */
public class StatmentLineParser {

    public StatementLine parse(String fieldText) {

        Pattern pattern = Pattern.compile(
                "(?<ValueDate>[0-9]{6})" +
                "(?<EntryDate>[0-9]{4})?" +
                "(?<DebitCreditMark>[A-Z]{2})" +
                "(?<FundsCode>[A-Z]{1})?" +
                "(?<Amount>[A-Z]{0,15})" +
                "(?<TransactionTypeIdentificationCode>[A-Z]{1})" +
                "(?<debitCreditMark>[A-Z]{3})" +
                "(?<debitCreditMark>[A-Z]{16})" +
                "(?<debitCreditMark>//[A-Z]{16})?"
        );

        return new StatementLine();
    }
}
