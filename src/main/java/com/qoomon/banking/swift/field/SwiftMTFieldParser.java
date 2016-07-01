package com.qoomon.banking.swift.field;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by qoomon on 27/06/16.
 */
public class SwiftMTFieldParser {

    public static final String SEPARATOR_FIELD_TAG = "--";

    private static final Pattern FIELD_STRUCTURE_PATTERN = Pattern.compile(":(?<tag>[^:]+):(?<content>.*)");

    public List<GeneralMTField> parse(Reader mt940TextReader) {

        List<GeneralMTField> result = new LinkedList<>();

        try (LineNumberReader lineReader = new LineNumberReader(mt940TextReader)) {

            String currentFieldTag = null;
            StringBuilder currentFieldContentBuilder = null;

            MessageLineType messageLineType = null;
            while (messageLineType != MessageLineType.END) {
                String messageLine = lineReader.readLine();
                int messageLineNumber = lineReader.getLineNumber();
                messageLineType = determineMessageLineType(messageLine);

                // handle finishing field
                if (currentFieldTag != null && messageLineType != MessageLineType.FIELD_CONTINUATION) {
                    GeneralMTField field = new GeneralMTField(currentFieldTag, currentFieldContentBuilder.toString());
                    result.add(field);
                    currentFieldTag = null;
                    currentFieldContentBuilder = null;
                }

                switch (messageLineType) {
                    case FIELD: {
                        Matcher fieldMatcher = FIELD_STRUCTURE_PATTERN.matcher(messageLine);
                        if (!fieldMatcher.matches()) {
                            throw new SwiftMTFieldParserException("Parse error: " + messageLineType.name() + " did not match " + FIELD_STRUCTURE_PATTERN.pattern(), messageLineNumber);
                        }

                        // start of a new field
                        currentFieldTag = fieldMatcher.group("tag");
                        currentFieldContentBuilder = new StringBuilder();

                        String content = fieldMatcher.group("content");
                        currentFieldContentBuilder.append(content);
                        break;
                    }
                    case FIELD_CONTINUATION: {
                        if (currentFieldTag == null) {
                            throw new SwiftMTFieldParserException("Parse error: " + messageLineType.name() + " unexpected, no field to continue", messageLineNumber);
                        }
                        // append line to content of current field
                        currentFieldContentBuilder.append("\n").append(messageLine);
                        break;
                    }
                    case SEPARATOR: {
                        result.add(new GeneralMTField(SEPARATOR_FIELD_TAG, ""));
                        break;
                    }
                    case EMPTY_LINE: {
                        if (messageLineNumber != 1) {
                            throw new SwiftMTFieldParserException("Parse error: " + messageLineType.name() + " unexpected", messageLineNumber);
                        }
                        break;
                    }
                    case END: {
                        // do nothing
                        break;
                    }
                    default:
                        throw new SwiftMTFieldParserException("Parse error: unexpected line " + messageLineType.name(), messageLineNumber);

                }
            }

            return result;
        } catch (IOException e) {
            throw new SwiftMTFieldParserException(e);
        }
    }

    private MessageLineType determineMessageLineType(String messageLine) {
        if (messageLine == null) {
            return MessageLineType.END;
        }
        if (messageLine.isEmpty()) {
            return MessageLineType.EMPTY_LINE;
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
        EMPTY_LINE,
        END
    }


}
