package com.qoomon.banking.swift.field;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by qoomon on 27/06/16.
 */
public class SwiftMTFieldParser {

    public static final String SEPARATOR_FIELD_TAG = "--";

    private static final Pattern FIELD_STRUCTURE_PATTERN = Pattern.compile(":(?<tag>[^:]+):(?<content>.*)");

    public List<GeneralMTField> parse(Reader mt940TextReader) {

        List<GeneralMTField> fieldList = new LinkedList<>();

        try (LineNumberReader lineReader = new LineNumberReader(mt940TextReader)) {

            String nextMessageLine = lineReader.readLine();
            int nextMessageLineNumber = lineReader.getLineNumber();
            MessageLineType nextMessageLineType = nextMessageLine != null ? determineMessageLineType(nextMessageLine) : null;

            GeneralMTFieldBuilder currentFieldBuilder = null;

            Set<MessageLineType> validFieldSet = Sets.immutableEnumSet(MessageLineType.FIELD);

            while (nextMessageLine != null) {
                String currentMessageLine = nextMessageLine;
                int currentMessageLineNumber = nextMessageLineNumber;
                MessageLineType currentMessageLineType = nextMessageLineType;

                if (!validFieldSet.contains(currentMessageLineType)) {
                    throw new SwiftMTFieldParserException("Parse error: unexpected line " + currentMessageLineType.name(), currentMessageLineNumber);
                }

                switch (currentMessageLineType) {
                    case FIELD: {
                        Matcher fieldMatcher = FIELD_STRUCTURE_PATTERN.matcher(currentMessageLine);
                        if (!fieldMatcher.matches()) {
                            throw new SwiftMTFieldParserException("Parse error: " + currentMessageLineType.name() + " did not match " + FIELD_STRUCTURE_PATTERN.pattern(), currentMessageLineNumber);
                        }

                        // start of a new field
                        currentFieldBuilder = new GeneralMTFieldBuilder()
                                .setTag(fieldMatcher.group("tag"))
                                .appendContent(fieldMatcher.group("content"));

                        validFieldSet = Sets.immutableEnumSet(MessageLineType.FIELD, MessageLineType.FIELD_CONTINUATION, MessageLineType.SEPARATOR);
                        break;
                    }
                    case FIELD_CONTINUATION: {
                        if (currentFieldBuilder == null) {
                            throw new SwiftMTFieldParserException("Bug: invalid order check for line type" + currentMessageLineType.name(), currentMessageLineNumber);
                        }
                        currentFieldBuilder.appendContent("\n")
                                .appendContent(currentMessageLine);
                        validFieldSet = Sets.immutableEnumSet(MessageLineType.FIELD, MessageLineType.FIELD_CONTINUATION, MessageLineType.SEPARATOR);
                        break;
                    }
                    case SEPARATOR: {
                        currentFieldBuilder = new GeneralMTFieldBuilder().setTag(SEPARATOR_FIELD_TAG);
                        validFieldSet = Sets.immutableEnumSet(MessageLineType.FIELD);
                        break;
                    }
                    default:
                        throw new SwiftMTFieldParserException("Bug: Missing handling for line type" + currentMessageLineType.name(), currentMessageLineNumber);

                }

                // prepare next line
                nextMessageLine = lineReader.readLine();
                nextMessageLineNumber = lineReader.getLineNumber();
                nextMessageLineType = nextMessageLine != null ? determineMessageLineType(nextMessageLine) : null;

                // handle finishing field
                if (currentFieldBuilder != null && nextMessageLineType != MessageLineType.FIELD_CONTINUATION) {
                    fieldList.add(currentFieldBuilder.build());
                    currentFieldBuilder = null;
                }
            }

            return fieldList;
        } catch (IOException e) {
            throw new SwiftMTFieldParserException(e);
        }
    }

    private MessageLineType determineMessageLineType(String messageLine) {
        Preconditions.checkNotNull(messageLine);
        if (messageLine.isEmpty()) {
            return MessageLineType.EMPTY;
        }
        if (messageLine.equals(SEPARATOR_FIELD_TAG)) {
            return MessageLineType.SEPARATOR;
        }
        if (FIELD_STRUCTURE_PATTERN.matcher(messageLine).matches()) {
            return MessageLineType.FIELD;
        }
        return MessageLineType.FIELD_CONTINUATION;

    }

    private enum MessageLineType {
        FIELD,
        FIELD_CONTINUATION,
        SEPARATOR,
        EMPTY
    }


    private class GeneralMTFieldBuilder {

        String tag = null;

        StringBuilder contentBuilder = new StringBuilder();

        public GeneralMTField build() {
            return new GeneralMTField(tag, contentBuilder.toString());
        }

        public GeneralMTFieldBuilder setTag(String tag) {
            this.tag = tag;
            return this;
        }

        public GeneralMTFieldBuilder appendContent(String content) {
            this.contentBuilder.append(content);
            return this;
        }
    }

}
