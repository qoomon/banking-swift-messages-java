package com.qoomon.banking.swift.message.submessage.field;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.qoomon.banking.swift.message.submessage.field.exception.FieldLineParseException;
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

    private final static Set<FieldLineType> FIELD_START_LINE_TYPE_SET = ImmutableSet.of(FieldLineType.FIELD, FieldLineType.SEPARATOR);

    private boolean init = false;

    private int currentFieldLineNumber = 0;

    private final LineNumberReader lineReader;

    private FieldLine nextFieldLine = null;


    public SwiftFieldReader(Reader textReader) {
        this.lineReader = new LineNumberReader(textReader);
    }

    public int getFieldLineNumber() {
        return currentFieldLineNumber;
    }

    public GeneralField readField() throws FieldParseException {
        try {
            if (!init) {
                nextFieldLine = readFieldLine(lineReader);
                init = true;
            }

            GeneralField field = null;

            GeneralField.Builder fieldBuilder = GeneralField.newBuilder();

            Set<FieldLineType> nextValidFieldLineTypeSet = FIELD_START_LINE_TYPE_SET;

            while (field == null && nextFieldLine != null) {

                ensureValidNextLine(nextFieldLine, nextValidFieldLineTypeSet, lineReader);

                FieldLine currentFieldLine = nextFieldLine;
                int currentLineNumber = lineReader.getLineNumber();

                nextFieldLine = readFieldLine(lineReader);

                switch (currentFieldLine.getType()) {
                    case FIELD: {
                        Matcher fieldMatcher = FIELD_STRUCTURE_PATTERN.matcher(currentFieldLine.getContent());
                        if (!fieldMatcher.matches()) {
                            throw new FieldParseException("Parse error: " + currentFieldLine.getType().name() + " did not match " + FIELD_STRUCTURE_PATTERN.pattern(), currentLineNumber);
                        }

                        // start of a new field
                        currentFieldLineNumber = lineReader.getLineNumber();
                        fieldBuilder
                                .setTag(fieldMatcher.group("tag"))
                                .appendContent(fieldMatcher.group("content"));
                        nextValidFieldLineTypeSet = ImmutableSet.of(FieldLineType.FIELD, FieldLineType.FIELD_CONTINUATION, FieldLineType.SEPARATOR);
                        break;
                    }
                    case FIELD_CONTINUATION: {
                        fieldBuilder
                                .appendContent("\n")
                                .appendContent(currentFieldLine.getContent());
                        nextValidFieldLineTypeSet = ImmutableSet.of(FieldLineType.FIELD, FieldLineType.FIELD_CONTINUATION, FieldLineType.SEPARATOR);
                        break;
                    }
                    case SEPARATOR: {
                        fieldBuilder.setTag(PageSeperator.TAG);
                        nextValidFieldLineTypeSet = ImmutableSet.of();
                        break;
                    }
                    default:
                        throw new FieldParseException("Bug: Missing handling for line type " + currentFieldLine.getType().name(), currentLineNumber);

                }

                // finish field
                if (nextFieldLine == null || FIELD_START_LINE_TYPE_SET.contains(nextFieldLine.getType())) {
                    field = fieldBuilder.build();
                }
            }

            return field;
        } catch (Exception e) {
            if (e instanceof FieldParseException)
                throw (FieldParseException) e;
            throw new FieldParseException(e);
        }
    }

    private void ensureValidNextLine(FieldLine nextFieldLine, Set<FieldLineType> expectedFieldLineTypeSet, LineNumberReader lineReader) throws FieldParseException {
        FieldLineType fieldLineType = nextFieldLine != null ? nextFieldLine.getType() : null;
        if (!expectedFieldLineTypeSet.contains(fieldLineType)) {
            throw new FieldParseException("Expected FieldLine '" + expectedFieldLineTypeSet + "', but was '" + fieldLineType + "'", lineReader.getLineNumber());
        }
    }


    private FieldLineType determineMessageLineType(String messageLine) {
        Preconditions.checkArgument(messageLine != null && !messageLine.isEmpty(), "messageLine can't be null or empty");

        if (messageLine.equals(PageSeperator.TAG)) {
            return FieldLineType.SEPARATOR;
        }
        if (messageLine.startsWith(":")) {
            return FieldLineType.FIELD;
        }
        return FieldLineType.FIELD_CONTINUATION;

    }

    private FieldLine readFieldLine(LineNumberReader lineReader) throws FieldLineParseException {
        try {
            String line = lineReader.readLine();
            return line == null ? null : new FieldLine(line);
        } catch (IOException e) {
            throw new FieldLineParseException(e.getMessage(), lineReader.getLineNumber(), e);
        }
    }

    private class FieldLine {
        private FieldLineType type;
        private String content;

        public FieldLine(String content) {
            this.type = determineMessageLineType(content);
            this.content = content;
        }

        public FieldLineType getType() {
            return type;
        }

        public String getContent() {
            return content;
        }
    }

    private enum FieldLineType {
        FIELD,
        FIELD_CONTINUATION,
        SEPARATOR
    }


}
