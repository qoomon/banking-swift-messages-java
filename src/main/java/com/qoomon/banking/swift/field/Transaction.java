package com.qoomon.banking.swift.field;

import java.util.Optional;

/**
 * Created by qoomon on 27/06/16.
 */
public class Transaction {

    /**
     * @see StatementLine#TAG_61
     */
    private StatementLine statementLine;

    /**
     * @see InformationToAccountOwner#TAG_86
     */
    private Optional<InformationToAccountOwner> informationToAccountOwner;
}
