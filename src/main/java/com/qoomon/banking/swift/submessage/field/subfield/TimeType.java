package com.qoomon.banking.swift.submessage.field.subfield;

public enum TimeType {

    /**
     * CLS Time
     * The time by which the funding payment must be credited, with confirmation, to the CLS Bank's account at the central bank, expressed in Central European Time (CET).
     */
    CLSTIME,

    /**
     * Receive Time
     * The time at which a TARGET2 payment was credited at the receiving central bank, expressed in Central European Time (CET).
     */
    RNCTIME,

    /**
     * Send Time
     * The time at which a TARGET2 payment was debited at the sending central bank, expressed in Central European Time (CET).
     */
    SNDTIME
}
