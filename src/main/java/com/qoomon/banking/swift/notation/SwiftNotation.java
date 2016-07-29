package com.qoomon.banking.swift.notation;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * <pre>
 * a = alphabetic, A through Z, upper case only
 * n = numeric digits, 0 through 9 only
 * c = alpha-numeric capital letters and numeric digits only ( a &amp; n above )
 * d = decimals, including decimal comma ',' preceding the fractional part. The fractional part may be missing, but the decimal comma must always be present
 * e = space
 * s = sign ( + or _ )
 * h = hex ( 0 to 9, A to F)
 * x = SWIFT X character set : SPACE, A to Z, a to z, 0 to 9, and  + - / ? . : , ( ) '                   and CrLF
 * y = SWIFT Y character set : SPACE, A to Z, a to z, 0 to 9, and  + - / ? . : , ( ) ' = ! &quot; % &amp; * &lt; &gt; ;
 * z = SWIFT Z character set : SPACE, A to Z, a to z, 0 to 9, and  + - / ? . : , ( ) ' = ! &quot; % &amp; * &lt; &gt; ; and CrLf
 * A = alphabetic, A through Z, upper and lower case
 * B = alphanumeric upper case or lower case, and numeric digits
 * length specification:
 * nn = maximum length ( minimum is 1 )
 * nn-nn = minimum and maximum length
 * nn! = fixed length
 * nn*nn = maximum number of lines time maximum line length - Will always be the last field
 * separators
 * LSep = left separator (&quot;/&quot;, &quot;//&quot;, &quot;BR&quot; for CrLf, &quot;ISIN &quot;, etc.), field starts with the character specified
 * RSep = right separator (&quot;/&quot;, &quot;//&quot;, &quot;BR&quot; for CrLf, &quot;ISIN &quot;, etc.), field ends with the character specified
 * examples,
 * 6!n = 6 numeric, fixed length
 * 6n = numeric up to 6 characters
 * 1!e = one blank space
 * 6*50x = up to 6 lines of up to 50 characters
 * </pre>
 */
public class SwiftNotation {

    private final static Pattern DECIMAL_NUMBER_PATTERN = Pattern.compile("[0-9]+,[0-9]*");

    private static final Map<String, String> SEPARATOR_MAP = new HashMap<>();

    static {
        // see class description for separator details
        SEPARATOR_MAP.put("/", "/");
        SEPARATOR_MAP.put("//", "//");
        SEPARATOR_MAP.put("BR", "\n");
    }

    private static final Map<String, String> CHARSET_REGEX_MAP = new HashMap<>();

    static {
        // see class description for charset details
        CHARSET_REGEX_MAP.put("a", "[A-Z]");
        CHARSET_REGEX_MAP.put("n", "[0-9]");
        CHARSET_REGEX_MAP.put("c", "[0-9A-Z]");
        CHARSET_REGEX_MAP.put("d", "[0-9,]");
        CHARSET_REGEX_MAP.put("e", " ");
        CHARSET_REGEX_MAP.put("s", "[+_]");
        CHARSET_REGEX_MAP.put("h", "[0-9A-F]");
        CHARSET_REGEX_MAP.put("x", "[ 0-9A-Za-z+-/?.:,()'\\n]");
        CHARSET_REGEX_MAP.put("y", "[ 0-9A-Za-z+-/?.:,()'=!\"%&*<>;]");
        CHARSET_REGEX_MAP.put("z", "[ 0-9A-Za-z+-/?.:,()'=!\"%&*<>;\\n]");
        CHARSET_REGEX_MAP.put("A", "[A-Za-z]");
        CHARSET_REGEX_MAP.put("B", "[0-9A-Za-z]");
    }

    /**
     * Group 1: Field Prefix
     * Group 2: Field length0
     * Group 3: Field length sign ! - *
     * Group 4: Field length1
     * Group 5: Field charset
     */
    private static final Pattern FIELD_NOTATION_PATTERN = Pattern.compile("(" + String.join("|", SEPARATOR_MAP.keySet()) + ")?([0-9]{1,2})([!-*])?([0-9]{1,2})?([" + String.join("", CHARSET_REGEX_MAP.keySet()) + "])");


    private final String notation;
    private final List<FieldNotation> swiftFieldNotations;
    private final List<Pattern> swiftFieldNotationPatterns;


    public SwiftNotation(String notation) {

        this.notation = notation;
        this.swiftFieldNotations = parseSwiftNotation(notation);
        this.swiftFieldNotationPatterns = generateSubfieldPatterns(this.swiftFieldNotations);
    }


    /**
     * Render field values
     *
     * @param fieldValues field values
     * @return rendered field
     * @throws FieldNotationParseException on invalid field values
     */
    public String render(List<String> fieldValues) throws FieldNotationParseException {

        if (fieldValues.size() != swiftFieldNotations.size()) {
            throw new FieldNotationParseException("Expected fieldValues count " + swiftFieldNotations.size() + ", but was " + fieldValues.size(), 0);
        }

        StringBuilder resultBuilder = new StringBuilder();

        int fieldIndex = -1;
        for (FieldNotation fieldNotation : swiftFieldNotations) {
            fieldIndex++;
            Pattern fieldPattern = swiftFieldNotationPatterns.get(fieldIndex);
            String fieldValue = fieldValues.get(fieldIndex);

            if (fieldValue == null) {
                if (!fieldNotation.isOptional()) {
                    throw new FieldNotationParseException("Mandatory field '" + fieldIndex + "' value can't be null", resultBuilder.toString().length());
                }
            } else {
                String renderedFieldValue = fieldNotation.getPrefix().map(SEPARATOR_MAP::get).orElse("") + fieldValue;
                Matcher fieldMatcher = fieldPattern.matcher(renderedFieldValue);
                if (!fieldMatcher.find() || fieldMatcher.end() != renderedFieldValue.length()) {
                    throw new FieldNotationParseException("Field value '" + renderedFieldValue + "' didn't match " + fieldNotation, resultBuilder.toString().length());
                }

                resultBuilder.append(renderedFieldValue);
            }
        }


        return resultBuilder.toString();

    }

    /**
     * Parse sub fields
     *
     * @param fieldText Text to parse
     * @return List of field values. Missing optional fields are represented as NULL
     * @throws FieldNotationParseException on invalid field values
     */
    public List<String> parse(String fieldText) throws FieldNotationParseException {

        int parseIndex = 0;

        List<String> result = new LinkedList<>();

        int fieldIndex = -1;
        for (FieldNotation fieldNotation : swiftFieldNotations) {
            fieldIndex++;
            Pattern fieldPattern = swiftFieldNotationPatterns.get(fieldIndex);

            Matcher fieldMatcher = fieldPattern.matcher(fieldText).region(parseIndex, fieldText.length());
            if (!fieldMatcher.find()) {
                throw new FieldNotationParseException(fieldNotation + " did not found matching characters."
                        + "'" + fieldText.substring(parseIndex) + "'", parseIndex);
            }
            String fieldValue = fieldMatcher.group(1);

            parseIndex = fieldMatcher.end();

            // special handling for d charset due to only on comma constraint
            if (fieldNotation.getCharSet().equals("d")) {
                Matcher decimalCharsetMatcher = DECIMAL_NUMBER_PATTERN.matcher(fieldValue);
                if (!decimalCharsetMatcher.matches()) {
                    throw new FieldNotationParseException(fieldNotation + " did not found matching characters."
                            + "'" + fieldText.substring(parseIndex) + "'", parseIndex);
                }
            }

            // add field value
            result.add(fieldValue);
        }

        if (parseIndex != fieldText.length()) {
            throw new FieldNotationParseException("Unparsed characters remain."
                    + "'" + fieldText.substring(parseIndex) + "'", parseIndex);
        }

        return result;
    }

    /**
     * select charset
     * handle delimiter
     * handle length
     * group field value
     * handle prefix
     * set optional if so
     *
     * @param fieldNotationList
     * @return patterns for continuous field matching
     */
    private static List<Pattern> generateSubfieldPatterns(List<FieldNotation> fieldNotationList) {
        Preconditions.checkArgument(fieldNotationList != null, "fieldNotationList can't be null");

        List<Pattern> patterns = new ArrayList<>(fieldNotationList.size());
        int fieldIndex = -1;
        for (FieldNotation currentSubfield : fieldNotationList) {
            fieldIndex++;
            // select charset
            String charSetRegex = CHARSET_REGEX_MAP.get(currentSubfield.getCharSet());
            if (charSetRegex == null) {
                throw new IllegalArgumentException("Unknown charset: " + currentSubfield.getCharSet());
            }

            // handle delimiter if any
            String delimiterLookaheadRegex = "";
            // collect possible delimiters
            List<String> fieldDelimiterList = new LinkedList<>();
            List<FieldNotation> upcomingFieldNotations = fieldNotationList.subList(fieldIndex + 1, fieldNotationList.size());
            for (FieldNotation upcomingFieldNotation : upcomingFieldNotations) {
                if (upcomingFieldNotation.getPrefix().isPresent()) {
                    fieldDelimiterList.add(SEPARATOR_MAP.get(upcomingFieldNotation.getPrefix().get()));
                }
                if (!upcomingFieldNotation.isOptional()) {
                    break;
                }
            }
            if (!fieldDelimiterList.isEmpty()) {
                delimiterLookaheadRegex = "(?!" + String.join("|", fieldDelimiterList) + ")";
            }

            // create field regex
            String subFieldRegex;

            // handle length
            Optional<String> lengthSign = currentSubfield.getLengthSign();
            if (!lengthSign.isPresent()) {
                int maxCharacters = currentSubfield.getLength0();
                subFieldRegex = "(:?" + delimiterLookaheadRegex + charSetRegex + ")" + "{1," + maxCharacters + "}";
            } else {
                switch (lengthSign.get()) {
                    case FieldNotation.FIXED_LENGTH_SIGN: {
                        int fixedCharacters = currentSubfield.getLength0();
                        subFieldRegex = "(:?" + delimiterLookaheadRegex + charSetRegex + ")" + "{" + fixedCharacters + "}";
                        break;
                    }
                    case FieldNotation.RANGE_LENGTH_SIGN: {
                        int minCharacters = currentSubfield.getLength0();
                        int maxCharacters = currentSubfield.getLength1().get();
                        subFieldRegex = "(:?" + delimiterLookaheadRegex + charSetRegex + ")" + "{" + minCharacters + "," + maxCharacters + "}";
                        break;
                    }
                    case FieldNotation.MULTILINE_LENGTH_SIGN: {
                        int maxLines = currentSubfield.getLength0();
                        int maxLineCharacters = currentSubfield.getLength1().get();
                        String lineCharactersRegexRange = "{1," + maxLineCharacters + "}";
                        String lineRegex = "[^\\n]" + lineCharactersRegexRange;
                        subFieldRegex = "(?=" + lineRegex + "(\\n" + lineRegex + ")" + "{0," + (maxLines - 1) + "}" + "$)" // lookahead for maxLines
                                + "(:?" + delimiterLookaheadRegex + "(:?" + charSetRegex + "|\\n)" + ")" // add new line character to charset
                                + "{1," + (maxLines * maxLineCharacters + (maxLines - 1)) + "}$";  // calculate max length including newline signs
                        break;
                    }
                    default:
                        throw new RuntimeException("Unsupported length sign '" + lengthSign + "'");
                }
            }

            // group field value
            subFieldRegex = "(" + subFieldRegex + ")";

            // handle prefix
            Optional<String> prefix = currentSubfield.getPrefix();
            if (prefix.isPresent()) {
                subFieldRegex = SEPARATOR_MAP.get(prefix.get()) + subFieldRegex;
            }

            // make field optional if so
            if (currentSubfield.isOptional()) {
                subFieldRegex = "(?:" + subFieldRegex + ")?";
            }

            Pattern pattern = Pattern.compile("^" + subFieldRegex);
            patterns.add(pattern);
        }

        return ImmutableList.copyOf(patterns);
    }

    public List<FieldNotation> parseSwiftNotation(String swiftNotation) {
        List<FieldNotation> result = new LinkedList<>();

        Pattern fieldNotationPattern = Pattern.compile("\\[" + FIELD_NOTATION_PATTERN + "\\]" + "|" + FIELD_NOTATION_PATTERN);
        Matcher fieldNotationMatcher = fieldNotationPattern.matcher(swiftNotation);
        int parseIndex = 0;
        while (fieldNotationMatcher.find(parseIndex)) {
            if (fieldNotationMatcher.start() != parseIndex) {
                throw new RuntimeException("Parse error: Unexpected sign(s) near index " + parseIndex + " '" + swiftNotation + "'");
            }
            parseIndex = fieldNotationMatcher.end();

            String fieldNotation = fieldNotationMatcher.group();
            // trim optional indicator
            String trimmedFieldNotation = fieldNotation.replaceFirst("^\\[(.*)\\]$", "$1");
            Matcher fieldPropertiesMatcher = FIELD_NOTATION_PATTERN.matcher(trimmedFieldNotation);
            if (!fieldPropertiesMatcher.matches()) {
                throw new RuntimeException("Parse error: Unexpected sign(s) near index " + parseIndex + " '" + swiftNotation + "'");
            }

            boolean fieldOptional = fieldNotation.startsWith("[");
            String fieldPrefix = fieldPropertiesMatcher.group(1);
            Integer fieldLength0 = Integer.parseInt(fieldPropertiesMatcher.group(2));
            Integer fieldLength1 = fieldPropertiesMatcher.group(4) == null ? null : Integer.parseInt(fieldPropertiesMatcher.group(4));
            String fieldLengthSign = fieldPropertiesMatcher.group(3);
            String fieldCharset = fieldPropertiesMatcher.group(5);

            FieldNotation fieldNotationModel = new FieldNotation(
                    fieldOptional,
                    fieldPrefix,
                    fieldCharset,
                    fieldLength0,
                    fieldLength1,
                    fieldLengthSign);

            // add field
            result.add(fieldNotationModel);
        }
        if (parseIndex != swiftNotation.length()) {
            throw new RuntimeException("Parse error: Unexpected sign(s) near index " + parseIndex + " '" + swiftNotation + "'");
        }

        return ImmutableList.copyOf(result);
    }




    public String getNotation() {
        return notation;
    }

    public List<FieldNotation> getSwiftFieldNotations() {
        return swiftFieldNotations;
    }

}