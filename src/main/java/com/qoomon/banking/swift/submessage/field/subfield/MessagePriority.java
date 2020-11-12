package com.qoomon.banking.swift.submessage.field.subfield;

/**
 * U = Urgent, N = Normal, S = System
 */
public enum MessagePriority {

    URGENT,
    NORMAL,
    SYSTEM;

    public static MessagePriority of(String value) {
        switch (value) {
            case "URGENT":
            case "U":
                return URGENT;
            case "NORMAL":
            case "N":
            case "":
                return NORMAL;
            case "SYSTEM":
            case "S":
                return SYSTEM;
            default:
                throw new IllegalArgumentException("No mapping found for value '" + value + "'");
        }
    }

    public String asText(){
        switch (this) {
            case URGENT:
                return "U";
            case NORMAL:
                return "N";
            case SYSTEM:
                return "S";
            default:
                throw new IllegalArgumentException("No mapping found for value '" + this + "'");
        }
    }
}
