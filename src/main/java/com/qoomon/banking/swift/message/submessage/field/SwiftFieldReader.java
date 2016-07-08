package com.qoomon.banking.swift.message.submessage.field;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.qoomon.banking.swift.message.submessage.field.exception.FieldParseException;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by qoomon on 27/06/16.
 */
public class SwiftFieldReader {

    private static final Pattern FIELD_STRUCTURE_PATTERN = Pattern.compile(":(?<tag>[^:]+):(?<content>.*)");

    private final LineNumberReader lineReader;

    private Set<MessageLineType> nextValidFieldSet = ImmutableSet.of(MessageLineType.FIELD);

    public SwiftFieldReader(Reader textReader) {
        this.lineReader = new LineNumberReader(textReader);
    }

    public int getLineNumber() {
        return lineReader.getLineNumber();
    }

    public GeneralField readField() throws FieldParseException {
        GeneralField field = null;
        try {
            Set<MessageLineType> currentValidFieldSet = nextValidFieldSet;
            String currentMessageLine;
            int currentMessageLineNumber;

            GeneralField.Builder currentFieldBuilder = null;

            while (field == null && (currentMessageLine = lineReader.readLine()) != null) {
                currentMessageLineNumber = lineReader.getLineNumber();
                if (currentMessageLineNumber == 1 && currentMessageLine.isEmpty()) {
                    continue;  // Skip first empty line if any
                }

                MessageLineType currentMessageLineType = determineMessageLineType(currentMessageLine);
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
                            throw new FieldParseException("Field content without any tag", currentMessageLineNumber);
                        }
                        currentFieldBuilder.appendContent("\n")
                                .appendContent(currentMessageLine);
                        nextValidFieldSet = ImmutableSet.of(MessageLineType.FIELD, MessageLineType.FIELD_CONTINUATION, MessageLineType.SEPARATOR);
                        break;
                    }
                    case SEPARATOR: {
                        currentFieldBuilder = GeneralField.newBuilder().setTag(Seperator.TAG);
                        nextValidFieldSet = ImmutableSet.of(MessageLineType.FIELD);
                        break;
                    }
                    default:
                        throw new FieldParseException("Bug: Missing handling for line type" + currentMessageLineType.name(), currentMessageLineNumber);

                }

                if (!currentValidFieldSet.contains(currentMessageLineType)) {
                    throw new FieldParseException("Parse error: unexpected line order of" + currentMessageLineType.name(), currentMessageLineNumber);
                }

                // lookahead
                lineReader.mark(256);
                String nextMessageLine = lineReader.readLine();
                lineReader.reset();

                // handle finishing field
                MessageLineType nextMessageLineType = nextMessageLine == null ? null : determineMessageLineType(nextMessageLine);
                if (nextMessageLineType != MessageLineType.FIELD_CONTINUATION && currentFieldBuilder != null) {
                    field = currentFieldBuilder.build();
                }

            }
        } catch (IOException e) {
            throw new FieldParseException(e);
        }
        return field;
    }


    private MessageLineType determineMessageLineType(String messageLine) {
        Preconditions.checkNotNull(messageLine);
        if (messageLine.isEmpty()) {
            return MessageLineType.EMPTY;
        }
        if (messageLine.equals(Seperator.TAG)) {
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
