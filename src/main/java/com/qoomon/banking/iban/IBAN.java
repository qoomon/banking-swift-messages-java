package com.qoomon.banking.iban;

import com.google.common.base.Preconditions;
import com.qoomon.banking.swift.notation.FieldNotationParseException;
import com.qoomon.banking.swift.notation.SwiftNotation;

import java.math.BigInteger;
import java.util.List;

/**
 * <b>International Bank Account Number</b>
 * <b>Format</b> 6!a2!a2!c[3!c]
 * <p>
 * <b>SubFields</b>
 * <pre>
 * 1: 2!a   - Country Code
 * 2: 2!n   - Check Sum
 * 3: 30!c  - BBAN (Basic Bank Account Number)
 * </pre>
 *
 * @see <a href="http://www.swift.com/dsp/resources/documents/IBAN_Registry.pdf">http://www.swift.com/dsp/resources/documents/IBAN_Registry.pdf</a>
 */
public class IBAN {

    public static final SwiftNotation NOTATION = new SwiftNotation("2!a2!n30c");

    public static final int IBAN_CHECKSUM_DIVIDEND = 97;
    public static final int IBAN_CHECKSUM_CHARACTER_NUMBER_OFFSET = 55;

    private final String countryCode;
    private final String checkDigits;
    private final String bban;

    public IBAN(String countryCode, String checkDigits, String bban) {

        Preconditions.checkArgument(countryCode != null, "countryCode can't be null");
        Preconditions.checkArgument(checkDigits != null, "checkDigits can't be null");
        Preconditions.checkArgument(bban != null, "bban can't be null");

        this.countryCode = countryCode;
        this.checkDigits = checkDigits;
        this.bban = bban;

        String ibanText = this.countryCode + this.checkDigits + this.bban;
        ensureValid(ibanText);
    }

    public static IBAN of(String value) {
        // remove all whitespaces
        String plainValue = value.replaceAll("\\s+", "");
        try {
            List<String> subfieldList = NOTATION.parse(plainValue);
            String countryCode = subfieldList.get(0);
            String checkSum = subfieldList.get(1);
            String bban = subfieldList.get(2);

            return new IBAN(countryCode, checkSum, bban);
        } catch (FieldNotationParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static void ensureValid(String value) {
        Preconditions.checkArgument(value != null, "value can't be null");

        try {
            List<String> subfieldList = NOTATION.parse(value);

            String countryCode = subfieldList.get(0);
            String checkDigits = subfieldList.get(1);
            String bban = subfieldList.get(2);
            // TODO validate country specific BBAN

            String expectedCheckDigits = calculateDigits(countryCode, bban);
            if (!checkDigits.equals(expectedCheckDigits)) {
                throw new IllegalArgumentException("Incorrect check digits. Expected '" + expectedCheckDigits + "', but was '" + checkDigits + "'");
            }
        } catch (FieldNotationParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static String calculateDigits(String countryCode, String bban) {
        String rearangedIban = bban + countryCode + "00";
        String rearangedIbanIntegerText = replaceCharactersWithInteger(rearangedIban);
        BigInteger rearangedIbanInteger = new BigInteger(rearangedIbanIntegerText);
        int rearangedIbanModRemainder = rearangedIbanInteger.mod(BigInteger.valueOf(IBAN_CHECKSUM_DIVIDEND)).intValue();
        int checkSum = 98 - rearangedIbanModRemainder;
        String checkDigits = String.format("%02d", checkSum);
        return checkDigits;
    }

    private static String replaceCharactersWithInteger(String source) {
        StringBuilder resultBuilder = new StringBuilder();
        for (char character : source.toCharArray()) {
            if (Character.isLetter(character)) {
                resultBuilder.append((int) character - IBAN_CHECKSUM_CHARACTER_NUMBER_OFFSET);
            } else {
                resultBuilder.append(character);
            }
        }
        return resultBuilder.toString();
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getCheckDigits() {
        return checkDigits;
    }

    public String getBban() {
        return bban;
    }
}
