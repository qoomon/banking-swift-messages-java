package com.qoomon.banking.iban;

import org.assertj.core.api.SoftAssertions;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;


/**
 * Created by qoomon on 19/07/16.
 */
public class IBANTest {

    @Test
    public void of_WHEN_valid_iban_RETURN_iban() throws Exception {

        // Given
        String ibanText = "DE44 5001 0517 5407 3249 31";

        // When
        IBAN iban = IBAN.of(ibanText);

        // Then
        assertThat(iban).isNotNull();
        assertThat(iban.getCountryCode()).isEqualTo("DE");
        assertThat(iban.getCheckDigits()).isEqualTo("44");
        assertThat(iban.getBban()).isEqualTo("500105175407324931");
    }

    @Test
    public void of_WHEN_valid_iban_list_RETURN_iban() throws Exception {

        // Given
        List<String> ibanTextList = new LinkedList<>();
        ibanTextList.add("DE44 5001 0517 5407 3249 31");
        ibanTextList.add("GR16 0110 1250 0000 0001 2300 695");
        ibanTextList.add("SA03 8000 0000 6080 1016 7519");
        ibanTextList.add("CH93 0076 2011 6238 5295 7");
        ibanTextList.add("CH93 0076 2011 6238 5295 7");
        ibanTextList.add("TR33 0006 1005 1978 6457 8413 26");
        ibanTextList.add("AL47 2121 1009 0000 0002 3569 8741");
        ibanTextList.add("AD12 0001 2030 2003 5910 0100");
        ibanTextList.add("AT61 1904 3002 3457 3201");
        ibanTextList.add("AZ21 NABZ 0000 0000 1370 1000 1944");
        ibanTextList.add("BH67 BMAG 0000 1299 1234 56");
        ibanTextList.add("BE62 5100 0754 7061");
        ibanTextList.add("BA39 1290 0794 0102 8494");
        ibanTextList.add("BG80 BNBG 9661 1020 3456 78");
        ibanTextList.add("HR12 1001 0051 8630 0016 0");
        ibanTextList.add("CY17 0020 0128 0000 0012 0052 7600");
        ibanTextList.add("CZ65 0800 0000 1920 0014 5399");
        ibanTextList.add("DK50 0040 0440 1162 43");
        ibanTextList.add("EE38 2200 2210 2014 5685");
        ibanTextList.add("FO97 5432 0388 8999 44");
        ibanTextList.add("FI21 1234 5600 0007 85");
        ibanTextList.add("FR14 2004 1010 0505 0001 3M02 606");
        ibanTextList.add("GE29 NB00 0000 0101 9049 17");
        ibanTextList.add("DE89 3704 0044 0532 0130 00");
        ibanTextList.add("GI75 NWBK 0000 0000 7099 453");
        ibanTextList.add("GR16 0110 1250 0000 0001 2300 695");
        ibanTextList.add("GL56 0444 9876 5432 10");
        ibanTextList.add("HU42 1177 3016 1111 1018 0000 0000");
        ibanTextList.add("IS14 0159 2600 7654 5510 7303 39");
        ibanTextList.add("IE29 AIBK 9311 5212 3456 78");
        ibanTextList.add("IL62 0108 0000 0009 9999 999");
        ibanTextList.add("IT40 S054 2811 1010 0000 0123 456");
        ibanTextList.add("JO94 CBJO 0010 0000 0000 0131 0003 02");
        ibanTextList.add("KW81 CBKU 0000 0000 0000 1234 5601 01");
        ibanTextList.add("LV80 BANK 0000 4351 9500 1");
        ibanTextList.add("LB62 0999 0000 0001 0019 0122 9114");
        ibanTextList.add("LI21 0881 0000 2324 013A A");
        ibanTextList.add("LT12 1000 0111 0100 1000");
        ibanTextList.add("LU28 0019 4006 4475 0000");
        ibanTextList.add("MK072 5012 0000 0589 84");
        ibanTextList.add("MT84 MALT 0110 0001 2345 MTLC AST0 01S");
        ibanTextList.add("MU17 BOMM 0101 1010 3030 0200 000M UR");
        ibanTextList.add("MD24 AG00 0225 1000 1310 4168");
        ibanTextList.add("MC93 2005 2222 1001 1223 3M44 555");
        ibanTextList.add("ME25 5050 0001 2345 6789 51");
        ibanTextList.add("NL39 RABO 0300 0652 64");
        ibanTextList.add("NO93 8601 1117 947");
        ibanTextList.add("PK36 SCBL 0000 0011 2345 6702");
        ibanTextList.add("PL60 1020 1026 0000 0422 7020 1111");
        ibanTextList.add("PT50 0002 0123 1234 5678 9015 4");
        ibanTextList.add("QA58 DOHB 0000 1234 5678 90AB CDEF G");
        ibanTextList.add("RO49 AAAA 1B31 0075 9384 0000");
        ibanTextList.add("SM86 U032 2509 8000 0000 0270 100");
        ibanTextList.add("SA03 8000 0000 6080 1016 7519");
        ibanTextList.add("RS35 2600 0560 1001 6113 79");
        ibanTextList.add("SK31 1200 0000 1987 4263 7541");
        ibanTextList.add("SI56 1910 0000 0123 438");
        ibanTextList.add("ES80 2310 0001 1800 0001 2345");
        ibanTextList.add("SE35 5000 0000 0549 1000 0003");
        ibanTextList.add("CH93 0076 2011 6238 5295 7");
        ibanTextList.add("TN59 1000 6035 1835 9847 8831");
        ibanTextList.add("TR33 0006 1005 1978 6457 8413 26");
        ibanTextList.add("AE07 0331 2345 6789 0123 456");

        SoftAssertions softly = new SoftAssertions();
        // When
        for (String ibanText : ibanTextList) {
            IBAN iban = IBAN.of(ibanText);

            // Then
            softly.assertThat(iban).isNotNull();

        }
        softly.assertAll();
    }

    @Test
    public void of_WHEN_check_digits_are_wrong_THROW_exception() throws Exception {

        // Given
        String ibanText = "DE11 5001 0517 5407 3249 31";

        // When
        Throwable exception = catchThrowable(() -> IBAN.of(ibanText));

        // Then
        assertThat(exception).isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    public void of_WHEN_iban_is_too_long_THROW_exception() throws Exception {

        // Given
        String ibanText = "DE11 000000000000000000000000000000 0";

        // When
        Throwable exception = catchThrowable(() -> IBAN.of(ibanText));

        // Then
        assertThat(exception).isInstanceOf(IllegalArgumentException.class);

    }
}