package com.qoomon.banking.swift.group;

import com.google.common.base.Preconditions;
import com.qoomon.banking.swift.field.InformationToAccountOwner;
import com.qoomon.banking.swift.field.StatementLine;

import java.util.Optional;

/**
 * Created by qoomon on 27/06/16.
 */
public class TransactionGroup {

    /**
     * @see StatementLine#FIELD_TAG_61
     */
    private final StatementLine statementLine;

    /**
     * @see InformationToAccountOwner#FIELD_TAG_86
     */
    private final Optional<InformationToAccountOwner> informationToAccountOwner;

    public TransactionGroup(StatementLine statementLine, InformationToAccountOwner informationToAccountOwner) {
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
