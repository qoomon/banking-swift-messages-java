package com.qoomon.banking.swift.submessage;

import com.qoomon.banking.swift.message.exception.SwiftMessageParseException;
import com.qoomon.banking.swift.submessage.exception.PageParserException;
import com.qoomon.banking.swift.submessage.field.GeneralField;
import com.qoomon.banking.swift.submessage.field.SwiftFieldReader;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static java.lang.String.join;

public abstract class PageReader<T extends Page> {

    public final List<T> readAll() throws SwiftMessageParseException {
        List<T> result = new LinkedList<>();
        T page;
        while ((page = read()) != null) {
            result.add(page);
        }
        return result;
    }

    public abstract T read() throws SwiftMessageParseException;

    public static void ensureValidField(GeneralField field, Set<String> expectedFieldTagSet, SwiftFieldReader fieldReader) {
        if (field == null) {
            throw new PageParserException("Expected field(s): " + join(", ", expectedFieldTagSet) + "," +
                    " but was end of file", fieldReader.getFieldLineNumber());
        }
        if (!expectedFieldTagSet.contains(field.getTag())) {
            throw new PageParserException("Expected field(s): " + join(", ", expectedFieldTagSet) + "," +
                    " but was '" + field.getTag() + "'", fieldReader.getFieldLineNumber());
        }
    }
}
