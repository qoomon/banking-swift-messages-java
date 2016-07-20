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
 * c = alpha-numeric capital letters and numeric digits only ( a & n above )
 * d = decimals, including decimal comma ',' preceding the fractional part. The fractional part may be missing, but the decimal comman must always be present
 * e = space
 * s = sign ( + or _ )
 * h = hex ( 0 to 9, A to F)
 * x = SWIFT X character set : SPACE, A to Z, a to z, 0 to 9, and  + - / ? . : , ( ) '                   and CrLF
 * y = SWIFT Y character set : SPACE, A to Z, a to z, 0 to 9, and  + - / ? . : , ( ) ' = ! " % & * < > ;
 * z = SWIFT Z character set : SPACE, A to Z, a to z, 0 to 9, and  + - / ? . : , ( ) ' = ! " % & * < > ; and CrLf
 * A = alphabetic, A through Z, upper and lower case
 * B = alphanumeric upper case or lower case, and numeric digits
 *
 * length specification:
 * nn = maximum length ( minimum is 1 )
 * nn-nn = minimum and maximum length
 * nn! = fixed length
 * nn*nn = maximum number of lines time maximum line length - Will always be the last field
 *
 * separators
 * LSep = left separator ("/", "//", "BR" for CrLf, "ISIN ", etc.), field starts with the character specified
 * RSep = right separator ("/", "//", "BR" for CrLf, "ISIN ", etc.), field ends with the character specified
 *
 * examples,
 * 6!n = 6 numeric, fixed length
 * 6n = numeric up to 6 characters
 * 1!e = one blank space
 * 6*50x = up to 6 lines of up to 50 characters
 * </pre>
 */
public class SwiftNotation {

    private final static Pattern DECIMAL_NUMBER_PATTERN = Pattern.compile("[^,]+,[^,]*");

    private static final Map<String, String> SEPARATOR_MAP = new HashMap<>();
    public static final String FIXED_LENGTH_SIGN = "!";
    public static final String RANGE_LENGTH_SIGN = "-";
    public static final String MULTILINE_LENGTH_SIGN = "*";

    static {
        // see class description for separator details
        SEPARATOR_MAP.put("/", "/");
        SEPARATOR_MAP.put("//", "//");
        SEPARATOR_MAP.put("BR", "\\n");
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
    private final List<SubFieldNotation> swiftSubFieldNotations;
    private final List<Pattern> swiftSubFieldNotationPatterns;


    public SwiftNotation(String notation) {

        this.notation = notation;
        this.swiftSubFieldNotations = parseSwiftNotation(notation);
        this.swiftSubFieldNotationPatterns = generateSubfieldPatterns(this.swiftSubFieldNotations);
    }


    /**
     * Render field values
     *
     * @param fieldValues field values
     * @return rendered field
     * @throws FieldNotationParseException
     */
    public String render(List<String> fieldValues) throws FieldNotationParseException {

        if (fieldValues.size() != swiftSubFieldNotations.size()) {
            throw new FieldNotationParseException("Expected fieldValues count " + swiftSubFieldNotations.size() + ", but was " + fieldValues.size(), 0);
        }

        StringBuilder resultBuilder = new StringBuilder();

        int subfieldIndex = -1;
        for (SubFieldNotation subfieldNotation : swiftSubFieldNotations) {
            subfieldIndex++;
            Pattern subfieldPattern = swiftSubFieldNotationPatterns.get(subfieldIndex);
            String fieldValue = fieldValues.get(subfieldIndex);

            if (fieldValue == null) {
                if (!subfieldNotation.isOptional()) {
                    throw new FieldNotationParseException("Mandatory field '" + subfieldIndex + "' value can't be null", resultBuilder.toString().length());
                }
            } else {
                String renderedFieldValue = subfieldNotation.getPrefix().orElse("") + fieldValue;
                Matcher fieldMatcher = subfieldPattern.matcher(renderedFieldValue);
                if (!fieldMatcher.find() || fieldMatcher.end() != renderedFieldValue.length()) {
                    throw new FieldNotationParseException("Field value '" + renderedFieldValue + "' didn't match " + subfieldNotation, resultBuilder.toString().length());
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
     * @return List of subfield values. Missing optional fields are represented as NULL
     * @throws FieldNotationParseException
     */
    public List<String> parse(String fieldText) throws FieldNotationParseException {

        int parseIndex = 0;

        List<String> result = new LinkedList<>();

        int subfieldIndex = -1;
        for (SubFieldNotation subfieldNotation : swiftSubFieldNotations) {
            subfieldIndex++;
            Pattern subfieldPattern = swiftSubFieldNotationPatterns.get(subfieldIndex);

            Matcher subfieldMatcher = subfieldPattern.matcher(fieldText).region(parseIndex, fieldText.length());
            if (!subfieldMatcher.find()) {
                throw new FieldNotationParseException(subfieldNotation + " did not found matching characters."
                        + "'" + fieldText.substring(parseIndex) + "'", parseIndex);
            }
            String fieldValue = subfieldMatcher.group(1);

            parseIndex = subfieldMatcher.end();

            // special handling for d charset due to only on comma constraint
            if (subfieldNotation.getCharSet().equals("d")) {
                Matcher decimalCharsetMatcher = DECIMAL_NUMBER_PATTERN.matcher(fieldValue);
                if (!decimalCharsetMatcher.matches()) {
                    throw new FieldNotationParseException(subfieldNotation + " did not found matching characters."
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
     * @param subFieldNotationList
     * @return patterns for continuous field matching
     */
    private static List<Pattern> generateSubfieldPatterns(List<SubFieldNotation> subFieldNotationList) {
        Preconditions.checkArgument(subFieldNotationList != null, "subFieldNotationList can't be null");

        List<Pattern> patterns = new ArrayList<>(subFieldNotationList.size());
        int subfieldIndex = -1;
        for (SubFieldNotation currentSubfield : subFieldNotationList) {
            subfieldIndex++;
            // select charset
            String charSetRegex = CHARSET_REGEX_MAP.get(currentSubfield.getCharSet());
            if (charSetRegex == null) {
                throw new IllegalArgumentException("Unknown charset: " + currentSubfield.getCharSet());
            }

            // handle delimiter if any
            String delimiterLookaheadRegex = "";
            // collect possible delimiters
            List<String> fieldDelimiterList = new LinkedList<>();
            List<SubFieldNotation> upcomingSubFieldNotations = subFieldNotationList.subList(subfieldIndex + 1, subFieldNotationList.size());
            for (SubFieldNotation upcomingSubFieldNotation : upcomingSubFieldNotations) {
                if (upcomingSubFieldNotation.getPrefix().isPresent()) {
                    fieldDelimiterList.add(SEPARATOR_MAP.get(upcomingSubFieldNotation.getPrefix().get()));
                }
                if (!upcomingSubFieldNotation.isOptional()) {
                    break;
                }
            }
            if (!fieldDelimiterList.isEmpty()) {
                delimiterLookaheadRegex = "(?!" + String.join("|", fieldDelimiterList) + ")";
            }

            // create subfield regex
            String subFieldRegex;

            // handle length
            Optional<String> lengthSign = currentSubfield.getLengthSign();
            if (!lengthSign.isPresent()) {
                int maxCharacters = currentSubfield.getLength0();
                subFieldRegex = "(:?" + delimiterLookaheadRegex + charSetRegex + ")" + "{1," + maxCharacters + "}";
            } else {
                switch (lengthSign.get()) {
                    case FIXED_LENGTH_SIGN: {
                        int fixedCharacters = currentSubfield.getLength0();
                        subFieldRegex = "(:?" + delimiterLookaheadRegex + charSetRegex + ")" + "{" + fixedCharacters + "}";
                        break;
                    }
                    case RANGE_LENGTH_SIGN: {
                        int minCharacters = currentSubfield.getLength0();
                        int maxCharacters = currentSubfield.getLength1().get();
                        subFieldRegex = "(:?" + delimiterLookaheadRegex + charSetRegex + ")" + "{" + minCharacters + "," + maxCharacters + "}";
                        break;
                    }
                    case MULTILINE_LENGTH_SIGN: {
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

    public List<SubFieldNotation> parseSwiftNotation(String swiftNotation) {
        List<SubFieldNotation> result = new LinkedList<>();

        Pattern fieldNotationPattern = Pattern.compile("\\[" + FIELD_NOTATION_PATTERN + "\\]" + "|" + FIELD_NOTATION_PATTERN);
        Matcher fieldNotationMatcher = fieldNotationPattern.matcher(swiftNotation);
        int parseIndex = 0;
        while (fieldNotationMatcher.find(parseIndex)) {
            if (fieldNotationMatcher.start() != parseIndex) {
                throw new RuntimeException("Parse error: Unexpected sign(s) near index " + parseIndex + " '" + swiftNotation + "'");
            }
            parseIndex = fieldNotationMatcher.end();

            String subfieldNotation = fieldNotationMatcher.group();
            // trim optional indicator
            String trimmedSubfieldNotation = subfieldNotation.replaceFirst("^\\[(.*)\\]$", "$1");
            Matcher fieldPropertiesMatcher = FIELD_NOTATION_PATTERN.matcher(trimmedSubfieldNotation);
            if (!fieldPropertiesMatcher.matches()) {
                throw new RuntimeException("Parse error: Unexpected sign(s) near index " + parseIndex + " '" + swiftNotation + "'");
            }

            boolean fieldOptional = subfieldNotation.startsWith("[");
            String fieldPrefix = fieldPropertiesMatcher.group(1);
            Integer fieldLength0 = Integer.parseInt(fieldPropertiesMatcher.group(2));
            Integer fieldLength1 = fieldPropertiesMatcher.group(4) == null ? null : Integer.parseInt(fieldPropertiesMatcher.group(4));
            String fieldLengthSign = fieldPropertiesMatcher.group(3);
            String fieldCharset = fieldPropertiesMatcher.group(5);

            SubFieldNotation subFieldNotation = new SubFieldNotation(
                    fieldOptional,
                    fieldPrefix,
                    fieldCharset,
                    fieldLength0,
                    fieldLength1,
                    fieldLengthSign);

            // add field
            result.add(subFieldNotation);
        }
        if (parseIndex != swiftNotation.length()) {
            throw new RuntimeException("Parse error: Unexpected sign(s) near index " + parseIndex + " '" + swiftNotation + "'");
        }

        return ImmutableList.copyOf(result);
    }


    private class SubFieldNotation {
        private final Boolean optional;
        private final Optional<String> prefix;
        private final String charSet;
        private final Integer length0;
        private final Optional<Integer> length1;
        private final Optional<String> lengthSign;

        public SubFieldNotation(Boolean optional, String prefix, String charSet, Integer length0, Integer length1, String lengthSign) {

            Preconditions.checkArgument(optional != null, "optional can't be null");
            Preconditions.checkArgument(charSet != null, "charSet can't be null");
            Preconditions.checkArgument(length0 != null, "length0 can't be null");

            this.optional = optional;
            this.prefix = Optional.ofNullable(prefix);
            this.charSet = charSet;
            this.length0 = length0;
            this.length1 = Optional.ofNullable(length1);
            this.lengthSign = Optional.ofNullable(lengthSign);

            if (!this.lengthSign.isPresent()) {
                Preconditions.checkArgument(!this.length1.isPresent(), "Missing field length sign between field lengths : '%s'", this);
            } else switch (this.lengthSign.get()) {
                case FIXED_LENGTH_SIGN:
                    Preconditions.checkArgument(!this.length1.isPresent(), "Unexpected field length after fixed length sign %s : '%s'", FIXED_LENGTH_SIGN, this);
                    break;
                case RANGE_LENGTH_SIGN:
                    Preconditions.checkArgument(this.length1.isPresent(), "Missing field length after range length sign %s : '%s'", RANGE_LENGTH_SIGN, this);
                    break;
                case MULTILINE_LENGTH_SIGN:
                    Preconditions.checkArgument(this.length1.isPresent(), "Missing field length after multiline length sign %s : '%s'", MULTILINE_LENGTH_SIGN, this);
                    break;
                default:
                    Preconditions.checkArgument(false, "Unknown length sign : '" + this.toString() + "'");
            }
        }

        public Boolean isOptional() {
            return optional;
        }

        public Integer getLength0() {
            return length0;
        }

        public Optional<Integer> getLength1() {
            return length1;
        }

        public Optional<String> getLengthSign() {
            return lengthSign;
        }

        public String getCharSet() {
            return charSet;
        }

        public Optional<String> getPrefix() {
            return prefix;
        }

        @Override
        public String toString() {
            String fieldNotation = "";

            if (prefix.isPresent()) {
                fieldNotation += prefix.get();
            }

            fieldNotation += length0;
            if (lengthSign.isPresent()) {
                fieldNotation += lengthSign.get();
                if (lengthSign.get().equals(RANGE_LENGTH_SIGN) || lengthSign.get().equals(MULTILINE_LENGTH_SIGN)) {
                    fieldNotation += length1.get();
                }
            }
            fieldNotation += charSet;
            if (optional) {
                fieldNotation = "[" + fieldNotation + "]";
            }
            return fieldNotation;
        }

    }

    public String getNotation() {
        return notation;
    }

    public List<SubFieldNotation> getSwiftSubFieldNotations() {
        return swiftSubFieldNotations;
    }
}