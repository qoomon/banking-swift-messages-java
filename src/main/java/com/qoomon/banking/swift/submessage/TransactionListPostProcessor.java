package com.qoomon.banking.swift.submessage;

import com.qoomon.banking.swift.submessage.field.StatementLine;
import com.qoomon.banking.swift.submessage.field.TransactionGroup;
import com.qoomon.banking.swift.submessage.field.subfield.EntryDateResolutionStrategy;

import java.time.LocalDate;
import java.time.MonthDay;
import java.util.List;
import java.util.stream.Collectors;

public class TransactionListPostProcessor {
    private final EntryDateResolutionStrategy entryDateResolutionStrategy;

    public TransactionListPostProcessor(EntryDateResolutionStrategy entryDateResolutionStrategy) {
        this.entryDateResolutionStrategy = entryDateResolutionStrategy;
    }

    public List<TransactionGroup> adjustEntryDates(List<TransactionGroup> transactions, LocalDate statementDate) {
        return transactions.stream().map(tx -> {
            StatementLine line = tx.getStatementLine();
            return new TransactionGroup(new StatementLine(
                    line.getValueDate(),
                    entryDateResolutionStrategy.resolve(MonthDay.from(line.getEntryDate()), statementDate),
                    line.getDebitCreditType(),
                    line.getDebitCreditMark(),
                    line.getAmount(),
                    line.getFundsCode().orElse(null),
                    line.getTransactionTypeIdentificationCode(),
                    line.getReferenceForAccountOwner(),
                    line.getReferenceForBank().orElse(null),
                    line.getSupplementaryDetails().orElse(null)
            ), tx.getInformationToAccountOwner().orElse(null));
        }).collect(Collectors.toList());
    }
}
