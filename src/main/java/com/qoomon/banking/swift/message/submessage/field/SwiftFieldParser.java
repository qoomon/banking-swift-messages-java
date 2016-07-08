package com.qoomon.banking.swift.message.submessage.field;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.qoomon.banking.swift.message.submessage.field.exception.FieldParseException;

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
public class SwiftFieldParser {

    public static final String SEPARATOR_FIELD_TAG = "-";

    private static final Pattern FIELD_STRUCTURE_PATTERN = Pattern.compile(":(?<tag>[^:]+):(?<content>.*)");

    public List<GeneralField> parse(Reader mt940TextReader) throws FieldParseException {

        List<GeneralField> fieldList = new LinkedList<>();

        try (LineNumberReader lineReader = new LineNumberReader(mt940TextReader)) {
            GeneralField.Builder currentFieldBuilder = null;

            Set<MessageLineType> currentValidFieldSet = ImmutableSet.of(MessageLineType.FIELD);
            String currentMessageLine = lineReader.readLine();
            int currentMessageLineNumber = lineReader.getLineNumber();

            // Skip first empty line
            if(currentMessageLine != null && currentMessageLine.isEmpty()){
                currentMessageLine = lineReader.readLine();
            }

            while (currentMessageLine != null) {
                MessageLineType currentMessageLineType = determineMessageLineType(currentMessageLine);

                String nextMessageLine;
                int nextMessageLineNumber;
                Set<MessageLineType> nextValidFieldSet;
                switch (currentMessageLineType) {
                    case FIELD: {
                        Matcher fieldMatcher = FIELD_STRUCTURE_PATTERN.matcher(currentMessageLine);
                        if (!fieldMatcher.matches()) {
                            throw new FieldParseException("Parse error: " + currentMessageLineType.name() + " did not match " + FIELD_STRUCTURE_PATTERN.pattern(), currentMessageLineNumber);
                        }

                        // start of a new field
                        currentFieldBuilder = GeneralField.newBuilder()
                                .setTag(fieldMatcher.group("tag"))
                                .appendContent(fieldMatcher.group("content"));

                        nextValidFieldSet = ImmutableSet.of(MessageLineType.FIELD, MessageLineType.FIELD_CONTINUATION, MessageLineType.SEPARATOR);
                        break;
                    }
                    case EMPTY:
                    case FIELD_CONTINUATION: {
                        if (currentFieldBuilder == null) {
                            throw new FieldParseException("Bug: invalid order check for line type " + currentMessageLineType.name(), currentMessageLineNumber);
                        }
                        currentFieldBuilder.appendContent("\n")
                                .appendContent(currentMessageLine);
                        nextValidFieldSet = ImmutableSet.of(MessageLineType.FIELD, MessageLineType.FIELD_CONTINUATION, MessageLineType.SEPARATOR);
                        break;
                    }
                    case SEPARATOR: {
                        currentFieldBuilder = GeneralField.newBuilder().setTag(SEPARATOR_FIELD_TAG);
                        nextValidFieldSet = ImmutableSet.of(MessageLineType.FIELD);
                        break;
                    }
                    default:
                        throw new FieldParseException("Bug: Missing handling for line type" + currentMessageLineType.name(), currentMessageLineNumber);

                }

                if (!currentValidFieldSet.contains(currentMessageLineType)) {
                    throw new FieldParseException("Parse error: unexpected line order of" + currentMessageLineType.name(), currentMessageLineNumber);
                }

                // prepare next line
                nextMessageLine = lineReader.readLine();
                nextMessageLineNumber = lineReader.getLineNumber();

                // handle finishing field
                if (nextMessageLine != null) {
                    MessageLineType nextMessageLineType = determineMessageLineType(nextMessageLine);
                    if (currentFieldBuilder != null && nextMessageLineType != MessageLineType.FIELD_CONTINUATION) {
                        fieldList.add(currentFieldBuilder.build());
                        currentFieldBuilder = null;
                    }
                } else { // end of reader
                    if (currentFieldBuilder != null) {
                        fieldList.add(currentFieldBuilder.build());
                        currentFieldBuilder = null;
                    }
                }

                // prepare for next iteration
                currentValidFieldSet = nextValidFieldSet;
                currentMessageLine = nextMessageLine;
                currentMessageLineNumber = nextMessageLineNumber;
            }

            return fieldList;
        } catch (IOException e) {
            throw new FieldParseException(e);
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


}
