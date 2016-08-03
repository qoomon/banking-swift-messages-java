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
        ibanTextList.add("AL47212110090000000235698741");
        ibanTextList.add("DZ4000400174401001050486");
        ibanTextList.add("AD1200012030200359100100");
        ibanTextList.add("AO06000600000100037131174");
        ibanTextList.add("AT611904300234573201");
        ibanTextList.add("AZ21NABZ00000000137010001944");
        ibanTextList.add("BH29BMAG1299123456BH00");
        ibanTextList.add("BA391290079401028494");
        ibanTextList.add("BE68539007547034");
        ibanTextList.add("BJ11B00610100400271101192591");
        ibanTextList.add("BR9700360305000010009795493P1");
        ibanTextList.add("BG80BNBG96611020345678");
        ibanTextList.add("BF1030134020015400945000643");
        ibanTextList.add("BI43201011067444");
        ibanTextList.add("CM2110003001000500000605306");
        ibanTextList.add("CV64000300004547069110176");
        ibanTextList.add("CR0515202001026284066");
        ibanTextList.add("HR1210010051863000160");
        ibanTextList.add("CY17002001280000001200527600");
        ibanTextList.add("CZ6508000000192000145399");
        ibanTextList.add("DK5000400440116243");
        ibanTextList.add("DO28BAGR00000001212453611324");
        ibanTextList.add("TL380080012345678910157");
        ibanTextList.add("EE382200221020145685");
        ibanTextList.add("FO1464600009692713");
        ibanTextList.add("FI2112345600000785");
        ibanTextList.add("FR1420041010050500013M02606");
        ibanTextList.add("GT82TRAJ01020000001210029690");
        ibanTextList.add("GE29NB0000000101904917");
        ibanTextList.add("DE89370400440532013000");
        ibanTextList.add("GI75NWBK000000007099453");
        ibanTextList.add("GR1601101250000000012300695");
        ibanTextList.add("GL8964710001000206");
        ibanTextList.add("HU42117730161111101800000000");
        ibanTextList.add("IS140159260076545510730339");
        ibanTextList.add("IR580540105180021273113007");
        ibanTextList.add("IE29AIBK93115212345678");
        ibanTextList.add("IL620108000000099999999");
        ibanTextList.add("IT60X0542811101000000123456");
        ibanTextList.add("CI05A00060174100178530011852");
        ibanTextList.add("JO94CBJO0010000000000131000302");
        ibanTextList.add("KZ176010251000042993");
        ibanTextList.add("KW74NBOK0000000000001000372151");
        ibanTextList.add("LV80BANK0000435195001");
        ibanTextList.add("LB30099900000001001925579115");
        ibanTextList.add("LI21088100002324013AA");
        ibanTextList.add("LT121000011101001000");
        ibanTextList.add("LU280019400644750000");
        ibanTextList.add("MK07300000000042425");
        ibanTextList.add("MG4600005030010101914016056");
        ibanTextList.add("MT84MALT011000012345MTLCAST001S");
        ibanTextList.add("MR1300012000010000002037372");
        ibanTextList.add("MU17BOMM0101101030300200000MUR");
        ibanTextList.add("ML03D00890170001002120000447");
        ibanTextList.add("MD24AG000225100013104168");
        ibanTextList.add("MC5813488000010051108001292");
        ibanTextList.add("ME25505000012345678951");
        ibanTextList.add("MZ59000100000011834194157");
        ibanTextList.add("NL91ABNA0417164300");
        ibanTextList.add("NO9386011117947");
        ibanTextList.add("PK24SCBL0000001171495101");
        ibanTextList.add("PS92PALS000000000400123456702");
        ibanTextList.add("PL27114020040000300201355387");
        ibanTextList.add("PT50000201231234567890154");
        ibanTextList.add("QA58DOHB00001234567890ABCDEFG");
        ibanTextList.add("XK051212012345678906");
        ibanTextList.add("RO49AAAA1B31007593840000");
        ibanTextList.add("SM86U0322509800000000270100");
        ibanTextList.add("SA0380000000608010167519");
        ibanTextList.add("SN12K00100152000025690007542");
        ibanTextList.add("RS35260005601001611379");
        ibanTextList.add("SK3112000000198742637541");
        ibanTextList.add("SI56191000000123438");
        ibanTextList.add("ES9121000418450200051332");
        ibanTextList.add("SE3550000000054910000003");
        ibanTextList.add("CH9300762011623852957");
        ibanTextList.add("TN5914207207100707129648");
        ibanTextList.add("TR330006100519786457841326");
        ibanTextList.add("AE260211000000230064016");
        ibanTextList.add("GB29NWBK60161331926819");
        ibanTextList.add("VG96VPVG0000012345678901");

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