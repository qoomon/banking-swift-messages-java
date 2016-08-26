package com.qoomon.banking.swift.submessage.field;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

/**
 * Created by qoomon on 15/08/16.
 */
public final class FieldUtils {

    /**
     * Separates a string.
     *
     * @param splitter splitter
     * @param joiner   joiner
     * @param text     text to separate.
     * @return separated text
     */
    public static String seperate(final String text, final Splitter splitter, final Joiner joiner) {
        return joiner.join(splitter.split(text));
    }

    /**
     * Separates a string.
     *
     * @param maxLineLength max line length
     * @param text          text to separate.
     * @return separated text
     */
    public static String breakIntoLines(final String text, final int maxLineLength) {
        return seperate(text,
                Splitter.fixedLength(maxLineLength),
                Joiner.on("\n")
        );
    }

    /**
     * Convert to Swift Text Format.
     *
     * @param field to convert
     * @return swift text
     */
    public static String swiftTextOf(SwiftField field) {
        return ":" + field.getTag() + ":" + field.getContent();
    }
}
