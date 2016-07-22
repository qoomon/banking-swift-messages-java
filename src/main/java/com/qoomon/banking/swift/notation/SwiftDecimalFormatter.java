package com.qoomon.banking.swift.notation;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;

/**
 * Created by qoomon on 21/07/16.
 */
public class SwiftDecimalFormatter {

    private final static DecimalFormat DECIMAL_FORMAT = new DecimalFormat();

    static {
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setDecimalSeparator(',');

        DECIMAL_FORMAT.setDecimalSeparatorAlwaysShown(true);
        DECIMAL_FORMAT.setGroupingUsed(false);
        DECIMAL_FORMAT.setMinimumIntegerDigits(1);
        DECIMAL_FORMAT.setMinimumFractionDigits(0);
        DECIMAL_FORMAT.setDecimalFormatSymbols(decimalFormatSymbols);
        DECIMAL_FORMAT.setMaximumFractionDigits(Integer.MAX_VALUE);
        DECIMAL_FORMAT.setParseBigDecimal(true);
    }

    public static BigDecimal parse(String numberText) {
        try {
            return (BigDecimal) DECIMAL_FORMAT.parse(numberText);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static String format(BigDecimal number) {
        return DECIMAL_FORMAT.format(number);
    }

}
