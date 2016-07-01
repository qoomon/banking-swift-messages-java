package com.qoomon.banking.swift.mt940;

import com.google.common.base.Preconditions;
import com.qoomon.banking.swift.field.InformationToAccountOwner;
import com.qoomon.banking.swift.field.StatementLine;

import java.util.Optional;

/**
 * Created by qoomon on 27/06/16.
 */
public class Transaction {

    /**
     * @see StatementLine#TAG
     */
    private final StatementLine statementLine;

    /**
     * @see InformationToAccountOwner#TAG
     */
    private final Optional<InformationToAccountOwner> informationToAccountOwner;

    public Transaction(StatementLine statementLine, InformationToAccountOwner informationToAccountOwner) {
        this.statementLine = Preconditions.checkNotNull(statementLine);
        this.informationToAccountOwner = Optional.ofNullable(informationToAccountOwner);
    }

    public StatementLine getStatementLine() {
        return statementLine;
    }

    public Optional<InformationToAccountOwner> getInformationToAccountOwner() {
        return informationToAccountOwner;
    }

}
