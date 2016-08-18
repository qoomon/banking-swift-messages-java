package com.qoomon.banking.swift.submessage.field.subfield;

/**
 * Created by qoomon on 05/07/16.
 */
public enum DebitCreditMark {

    DEBIT,
    CREDIT;

    public static DebitCreditMark ofFieldValue(String value) {
        switch (value) {
            case "D":
                return DEBIT;
            case "C":
                return CREDIT;
            default:
                throw new IllegalArgumentException("No mapping found for value '" + value + "'");
        }
    }

    public String toFieldValue() {
        switch (this) {
            case DEBIT:
                return "D";
            case CREDIT:
                return "C";
            default:
                throw new IllegalStateException("No field value mapping for " + this.name());
        }
    }

    /**
     * Return sign factor for mark.
     * @return -1 for negative sign or +1 for positive sign
     */
    public int sign() {
        if (this == DebitCreditMark.DEBIT) {
            return -1;
        }

        if (this == DebitCreditMark.CREDIT) {
            return 1;
        }

        throw new IllegalAccessError("Unmapped sign for mark: " + this.name());
    }
}
