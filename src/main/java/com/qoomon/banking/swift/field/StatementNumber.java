package com.qoomon.banking.swift.field;

import com.google.common.base.Preconditions;
import com.qoomon.banking.swift.field.notation.SwiftFieldNotation;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;

/**
 * Created by qoomon on 24/06/16.
 */
public class StatementNumber implements SwiftMTField {
    /**
     * :28C: – Statement Number/Sequence Number
     */
    public static final String TAG = "28C";

    /**
     * 5n[/5n] -  Statement Number | Sequence Number
     * <br>
     * Founds Code: C = credit, RC = Reversal of credit, D = debit, RD = Reversal of debit
     */
    public static final SwiftFieldNotation SWIFT_NOTATION = new SwiftFieldNotation("5n[/5n]");

    private final String value;
    private final Optional<String> sequenceNumber;

    public StatementNumber(String value, String sequenceNumber) {
        this.value = Preconditions.checkNotNull(value);
        this.sequenceNumber = Optional.ofNullable(sequenceNumber);
    }

    public static StatementNumber of(GeneralMTField field) throws ParseException {
        Preconditions.checkArgument(field.getTag().equals(TAG), "unexpected field tag '" + field.getTag() + "'");

        List<String> subFields = SWIFT_NOTATION.parse(field.getContent());

        String value = subFields.get(0);
        String sequenceNumber = subFields.get(1);

        return new StatementNumber(value, sequenceNumber);
    }

    public String getValue() {
        return value;
    }

    public Optional<String> getSequenceNumber() {
        return sequenceNumber;
    }

    @Override
    public String getTag() {
        return TAG;
    }
}
