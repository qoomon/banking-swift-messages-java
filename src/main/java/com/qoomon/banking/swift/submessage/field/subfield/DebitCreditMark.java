package com.qoomon.banking.swift.submessage.field.subfield;

/**
 * Created by qoomon on 05/07/16.
 */
public enum DebitCreditMark {

    DEBIT,
    REVERSAL_DEBIT,
    CREDIT,
    REVERSAL_CREDIT;

    public static DebitCreditMark ofFieldValue(String value) {
        switch (value) {
            case "D":
                return DEBIT;
            case "C":
                return CREDIT;
            case "RC":
                return REVERSAL_DEBIT;
            case "RD":
                return REVERSAL_CREDIT;
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
            case REVERSAL_DEBIT:
                return "RD";
            case REVERSAL_CREDIT:
                return "RC";
            default:
                throw new IllegalStateException("No field value mapping for " + this.name());
        }
    }

    /**
     * Return sign factor for mark.
     * @return -1 for negative sign or +1 for positive sign
     */
    public int sign() {
        if (this == DebitCreditMark.DEBIT || this == DebitCreditMark.REVERSAL_CREDIT) {
            return -1;
        }

        if (this == DebitCreditMark.CREDIT || this == DebitCreditMark.REVERSAL_DEBIT) {
            return 1;
        }

        throw new IllegalAccessError("Unmapped sign for mark: " + this.name());
    }

}
