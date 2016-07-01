package com.qoomon.banking.swift.field;

import java.util.Optional;

/**
 * Created by qoomon on 27/06/16.
 */
public class Transaction {

    /**
     * @see StatementLine#TAG
     */
    private StatementLine statementLine;

    /**
     * @see InformationToAccountOwner#TAG
     */
    private Optional<InformationToAccountOwner> informationToAccountOwner;

    public Transaction(GeneralMTField field) {

    }
}
