package com.qoomon.banking.swift.field.notation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
 * x = SWIFT X character set : A to Z, a to z, 0 to 9, SPACE CrLF and / - ? : ( ) . , ' +
 * y = SWIFT Y character set : A to Z, a to z, 0 to 9, SPACE and . , - ( ) / = ' + : ? ! " % & * < > ;
 * z = SWIFT Z character set : A to Z, a to z, 0 to 9, SPACE CrLf and . , - ( ) / = ' + : ? ! " % & * < > ;
 * A = alphabetic, A through Z, upper and lower case
 * B = alphanumeric upper case or lower case, and numeric digits
 *
 * length specification:
 * nn = maximum length ( minimum is 1 )
 * nn-nn = minimum and maximum length
 * nn! = fixed length
 * nn*nn = maximum number of lines time maximum line length
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
public class SwiftFieldNotation {

    private static final Map<String, String> CHARSET_REGEX_MAP = new HashMap<String, String>() {{
        put("a", "[A-Z]");
        put("n", "[0-9]");
        put("c", "[0-9A-Z]");
        put("d", "[0-9,]");
        put("e", " ");
        put("s", "[+_]");
        put("h", "[0-9A-F]");
        put("x", "[0-9A-Za-z]"); // TODO [0-9A-Za-z TODO]
        put("y", "[0-9A-Za-z]"); // TODO [0-9A-Za-z TODO]
        put("z", "[0-9A-Za-z]"); // TODO [0-9A-Za-z TODO]
        put("A", "[A-Za-z]");
        put("B", "[0-9A-Za-z]");
    }};

    private final String notation;
    private final List<SubField> swiftSubFields;


    public SwiftFieldNotation(String notation) {

        this.swiftSubFields = parseSwiftNotation(notation);

        this.notation = notation;
    }

    public List<String> parse(String fieldText) {

        int parseIndex = 0;

        List<String> result = new LinkedList<>();

        for (SubField swiftSubField : swiftSubFields) {

            String charSet = swiftSubField.getCharSet();
            String charSetRegex = CHARSET_REGEX_MAP.get(charSet);
            if (charSetRegex == null) {
                throw new RuntimeException("Parse error: near index " + parseIndex + " '" + fieldText.substring(parseIndex, Math.min(parseIndex + 8, fieldText.length())) + "' unknown charset '" + charSet + "'");
            }
            if (charSetRegex.isEmpty()) {
                throw new UnsupportedOperationException("Parse error: near index " + parseIndex + " '" + fieldText.substring(parseIndex, Math.min(parseIndex + 8, fieldText.length())) + "' charset '" + charSet + "' not supported yet");
            }

            String subFieldRegex = "";
            if (swiftSubField.getLengthSign().isEmpty()) {
                subFieldRegex += charSetRegex + "{1," + swiftSubField.getLength0() + "}";
            } else if (swiftSubField.getLengthSign().equals("!")) {
                subFieldRegex += charSetRegex + "{" + swiftSubField.getLength0() + "}";
            } else if (swiftSubField.getLengthSign().equals("-")) {
                subFieldRegex += charSetRegex + "{" + swiftSubField.getLength0() + "," + swiftSubField.getLength1() + "}";
            } else if (swiftSubField.getLengthSign().equals("*")) {
                subFieldRegex += charSetRegex + "{1," + swiftSubField.getLength1() + "}";
                for (int i = 1; i < swiftSubField.getLength0(); i++) {
                    subFieldRegex += "(?:" + "\n" + charSetRegex + "{1," + swiftSubField.getLength1() + "}" + ")?";
                }
            } else {
                throw new RuntimeException("unexpected length sign '" + swiftSubField.getLengthSign() + "'");
            }
            if (swiftSubField.isOptional()) {
                subFieldRegex = "(?:" + subFieldRegex + ")?";
            }
            //name group for selection
            subFieldRegex = "(?<fieldValue>" + subFieldRegex + ")";
            Matcher subFieldMatcher = Pattern.compile(subFieldRegex + ".*", Pattern.DOTALL).matcher(fieldText).region(Math.min(parseIndex, fieldText.length() - 1), fieldText.length());
            if (!subFieldMatcher.matches()) {
                throw new UnsupportedOperationException("Parse error: near index " + parseIndex + " '" + fieldText.substring(parseIndex, Math.min(parseIndex + 8, fieldText.length())) + "' did not match " + swiftSubField);
            }
            parseIndex = subFieldMatcher.end("fieldValue");

            // special handling for d charset due to only on comma constraint
            if (charSet.equals("d")) {
                Matcher dCharsetMatcher = Pattern.compile("[^,]+,[^,]*").matcher(fieldText);
                if (!dCharsetMatcher.matches()) {
                    throw new UnsupportedOperationException("Parse error: near index " + parseIndex + " '" + fieldText.substring(parseIndex, Math.min(parseIndex + 8, fieldText.length())) + "' did not match " + swiftSubField);
                }

            }

            result.add(subFieldMatcher.group("fieldValue"));

        }

        return result;
    }

    public List<SubField> parseSwiftNotation(String swiftNotation) {
        List<SubField> result = new LinkedList<>();

        Pattern fieldNotationPattern = Pattern.compile("([0-9]{1,2})(!|([-*][0-9]{1,2}))?([acdehnsxyzAB])");
        Pattern fieldNotationOptionalPattern = Pattern.compile("(" + "(\\[(?<Optional>" + fieldNotationPattern + ")\\])" + "|" + "(?<Mandatory>" + fieldNotationPattern + ")" + ")");
        Matcher fieldNotationMatcher = fieldNotationOptionalPattern.matcher(swiftNotation);

        int parseIndex = 0;
        while (fieldNotationMatcher.find(parseIndex)) {
            if (fieldNotationMatcher.start() != parseIndex) {
                throw new RuntimeException("Parse error: near index " + parseIndex + " '" + swiftNotation.substring(parseIndex, Math.min(parseIndex + 8, swiftNotation.length())) + " unexpected sign(s) '" + swiftNotation.substring(parseIndex, fieldNotationMatcher.start()) + "'");
            }
            parseIndex = fieldNotationMatcher.end();

            SubField swiftSubField = new SubField();

            String field = fieldNotationMatcher.group("Mandatory");
            if (field == null) {
                field = fieldNotationMatcher.group("Optional");
                swiftSubField.optional = true;
            }

            Matcher fieldPropertiesMatcher = fieldNotationPattern.matcher(field);
            if (!fieldPropertiesMatcher.matches()) {
                //should never happened because it was check already with fieldNotationMatcher
                throw new RuntimeException("Unexpected mismatch");
            }

            int length0 = Integer.parseInt(fieldPropertiesMatcher.group(1));
            String lengthExtra = fieldPropertiesMatcher.group(2);

            swiftSubField.length0 = length0;
            if (lengthExtra != null) {
                String lenghtSign = lengthExtra.substring(0, 1);
                swiftSubField.lengthSign = lenghtSign;
                switch (lenghtSign) {
                    case "!": {
                        break;
                    }
                    case "-":
                    case "*": {
                        swiftSubField.length1 = Integer.parseInt(lengthExtra.substring(1));
                        break;
                    }
                }
            }

            swiftSubField.charSet = fieldPropertiesMatcher.group(4);


            result.add(swiftSubField);
        }
        if(parseIndex != swiftNotation.length()){
            throw new RuntimeException("Parse error: near index " + parseIndex + " '" + swiftNotation.substring(parseIndex, Math.min(parseIndex + 8, swiftNotation.length())));
        }
        return result;
    }

    public String getNotation() {
        return notation;
    }

    public class SubField {
        private boolean optional = false;
        private Integer length0 = null;
        private Integer length1 = null;
        private String charSet = null;
        public String lengthSign = "";

        public boolean isOptional() {
            return optional;
        }

        public int getLength0() {
            return length0;
        }

        public int getLength1() {
            return length1;
        }

        public String getLengthSign() {
            return lengthSign;
        }

        public String getCharSet() {
            return charSet;
        }

        @Override
        public String toString() {
            String fieldNotation = "";

            fieldNotation += length0;
            if (!lengthSign.isEmpty()) {
                fieldNotation += lengthSign;
                if (lengthSign.equals("-") || lengthSign.equals("*")) {
                    fieldNotation += length1;
                }
            }
            fieldNotation += charSet;
            if (optional) {
                fieldNotation = "[" + fieldNotation + "]";
            }
            return fieldNotation;
        }
    }


}
