package com.qoomon.banking.swift.submessage.mt103;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.qoomon.banking.swift.message.exception.SwiftMessageParseException;
import com.qoomon.banking.swift.submessage.PageSeparator;
import com.qoomon.banking.swift.submessage.exception.PageParserException;
import com.qoomon.banking.swift.submessage.field.*;

import java.io.Reader;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Parser for {@link MT103Page}
 */
public class MT103PageReader {

    private static final Set<String> MESSAGE_START_FIELD_TAG_SET = ImmutableSet.of(TransactionReferenceNumber.FIELD_TAG_20);

    private static final Set<String> MESSAGE_END_FIELD_TAG_SET = ImmutableSet.of(PageSeparator.TAG);

    private final SwiftFieldReader fieldReader;

    private GeneralField currentField = null;

    private GeneralField nextField = null;


    public MT103PageReader(Reader textReader) {

        Preconditions.checkArgument(textReader != null, "textReader can't be null");

        this.fieldReader = new SwiftFieldReader(textReader);
    }

    public List<MT103Page> readAll() throws SwiftMessageParseException {
        List<MT103Page> result = new LinkedList<>();
        MT103Page page;
        while ((page = read()) != null) {
            result.add(page);
        }
        return result;
    }

    public MT103Page read() throws SwiftMessageParseException {
        try {
            if (currentField == null) {
                nextField = fieldReader.readField();
            }

            MT103Page page = null;

            // message fields
            TransactionReferenceNumber transactionReferenceNumber = null;
            TimeIndicator timeIndicator = null;
            BankOperation bankOperation = null;

            Set<String> nextValidFieldSet = MESSAGE_START_FIELD_TAG_SET;

            while (page == null && nextField != null) {

                ensureValidNextField(nextField, nextValidFieldSet, fieldReader);

                GeneralField previousField = currentField;
                currentField = nextField;
                nextField = fieldReader.readField();

                switch (currentField.getTag()) {
                    case TransactionReferenceNumber.FIELD_TAG_20: {
                        transactionReferenceNumber = TransactionReferenceNumber.of(currentField);
                        nextValidFieldSet = ImmutableSet.of(
                                TimeIndicator.FIELD_TAG_13C,
                                BankOperation.FIELD_TAG_23B);
                        break;
                    }
                    case TimeIndicator.FIELD_TAG_13C: {
                        timeIndicator = TimeIndicator.of(currentField);
                        nextValidFieldSet = ImmutableSet.of(
                                BankOperation.FIELD_TAG_23B);
                        break;
                    }
                    case BankOperation.FIELD_TAG_23B: {
                        bankOperation = BankOperation.of(currentField);
                        nextValidFieldSet = ImmutableSet.of(

                        );
                        break;
                    }

                    case PageSeparator.TAG: {
                        nextValidFieldSet = ImmutableSet.of();
                        break;
                    }
                    default:
                        throw new PageParserException("Unexpected field '" + currentField.getTag() + "'", fieldReader.getFieldLineNumber());
                }

                // finish message
                if (MESSAGE_END_FIELD_TAG_SET.contains(currentField.getTag())) {
                    page = new MT103Page(
                            transactionReferenceNumber,
                            timeIndicator,
                            bankOperation
                    );
                } else if (nextField == null) {
                    throw new PageParserException("Unfinished page. Missing page delimiter " + MESSAGE_END_FIELD_TAG_SET, fieldReader.getFieldLineNumber());
                }
            }


            return page;
        } catch (SwiftMessageParseException e) {
            throw e;
        } catch (Exception e) {
            throw new SwiftMessageParseException(e.getMessage(), fieldReader.getFieldLineNumber(), e);
        }
    }

    private void ensureValidNextField(GeneralField field, Set<String> expectedFieldTagSet, SwiftFieldReader fieldReader) throws SwiftMessageParseException {
        String fieldTag = field != null ? field.getTag() : null;
        if (!expectedFieldTagSet.contains(fieldTag)) {
            throw new PageParserException("Expected Field '" + expectedFieldTagSet + "', but was '" + fieldTag + "'", fieldReader.getFieldLineNumber());
        }
    }
}
