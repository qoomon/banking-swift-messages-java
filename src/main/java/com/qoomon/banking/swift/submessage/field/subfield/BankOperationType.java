package com.qoomon.banking.swift.submessage.field.subfield;

public enum BankOperationType {

    /**
     * Normal credit transfer
     * This message contains a credit transfer where there is no SWIFT Service Level involved.
     */
    CRED,

    /**
     * Test message
     * This message contains a credit transfer for test purposes.
     */
    CRTS,

    /**
     * SWIFTPay
     * This message contains a credit transfer to be processed according to the SWIFTPay Service Level.
     */
    SPAY,

    /**
     * Priority
     * This message contains a credit transfer to be processed according to the Priority Service Level.
     */
    SPRI,

    /**
     * Standard
     * This message contains a credit transfer to be processed according to the Standard Service Level.
     */
    SSTD;

    public static BankOperationType ofFieldValue(String value) {
        switch (value) {
            case "CRED":
                return CRED;
            case "CRTS":
                return CRTS;
            case "SPAY":
                return SPAY;
            case "SPRI":
                return SPRI;
            case "SSTD":
                return SSTD;
            default:
                throw new IllegalArgumentException("No mapping found for value '" + value + "'");
        }
    }

    public String toFieldValue() {
        switch (this) {
            case CRED:
                return "CRED";
            case CRTS:
                return "CRTS";
            case SPAY:
                return "SPAY";
            case SPRI:
                return "SPRI";
            case SSTD:
                return "SSTD";
            default:
                throw new IllegalStateException("No field value mapping for " + this.name());
        }
    }
}
