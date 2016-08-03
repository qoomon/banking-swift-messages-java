package com.qoomon.banking.swift.notation;

import com.google.common.base.Preconditions;

import java.util.Optional;

/**
 * Created by qoomon on 29/07/16.
 */
public class FieldNotation {

    public static final String FIXED_LENGTH_SIGN = "!";
    public static final String RANGE_LENGTH_SIGN = "-";
    public static final String MULTILINE_LENGTH_SIGN = "*";

    private final Boolean optional;
    private final Optional<String> prefix;
    private final String charSet;
    private final Integer length0;
    private final Optional<Integer> length1;
    private final Optional<String> lengthSign;

    public FieldNotation(Boolean optional, String prefix, String charSet, Integer length0, Integer length1, String lengthSign) {

        Preconditions.checkArgument(optional != null, "optional can't be null");
        Preconditions.checkArgument(charSet != null, "charSet can't be null");
        Preconditions.checkArgument(length0 != null, "length0 can't be null");

        this.optional = optional;
        this.prefix = Optional.ofNullable(prefix);
        this.charSet = charSet;
        this.length0 = length0;
        this.length1 = Optional.ofNullable(length1);
        this.lengthSign = Optional.ofNullable(lengthSign);

        if (!this.lengthSign.isPresent()) {
            Preconditions.checkArgument(!this.length1.isPresent(), "Missing field length sign between field lengths : '%s'", this);
        } else switch (this.lengthSign.get()) {
            case FIXED_LENGTH_SIGN:
                Preconditions.checkArgument(!this.length1.isPresent(), "Unexpected field length after fixed length sign %s : '%s'", FIXED_LENGTH_SIGN, this);
                break;
            case RANGE_LENGTH_SIGN:
                Preconditions.checkArgument(this.length1.isPresent(), "Missing field length after range length sign %s : '%s'", RANGE_LENGTH_SIGN, this);
                break;
            case MULTILINE_LENGTH_SIGN:
                Preconditions.checkArgument(this.length1.isPresent(), "Missing field length after multiline length sign %s : '%s'", MULTILINE_LENGTH_SIGN, this);
                break;
            default:
                Preconditions.checkArgument(false, "Unknown length sign : '" + this.toString() + "'");
                break;
        }
    }

    public Boolean isOptional() {
        return optional;
    }

    public Integer getLength0() {
        return length0;
    }

    public Optional<Integer> getLength1() {
        return length1;
    }

    public Optional<String> getLengthSign() {
        return lengthSign;
    }

    public String getCharSet() {
        return charSet;
    }

    public Optional<String> getPrefix() {
        return prefix;
    }

    @Override
    public String toString() {
        String fieldNotation = "";

        if (prefix.isPresent()) {
            fieldNotation += prefix.get();
        }

        fieldNotation += length0;
        if (lengthSign.isPresent()) {
            fieldNotation += lengthSign.get();
            if (lengthSign.get().equals(RANGE_LENGTH_SIGN) || lengthSign.get().equals(MULTILINE_LENGTH_SIGN)) {
                fieldNotation += length1.get();
            }
        }
        fieldNotation += charSet;
        if (optional) {
            fieldNotation = "[" + fieldNotation + "]";
        }
        return fieldNotation;
    }

}