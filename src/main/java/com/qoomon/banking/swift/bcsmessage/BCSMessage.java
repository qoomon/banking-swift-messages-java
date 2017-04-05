package com.qoomon.banking.swift.bcsmessage;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * Created by qoomon on 25/07/16
 * <p>
 * Banking Communication Standard Format Message
 * http://www.ebics.de/index.php?id=77 (Anlage 3 Datenformate)
 * https://de.wikipedia.org/wiki/Banking_Communication_Standard
 * https://www.bayernlb.de/internet/media/de/internet_4/de_1/downloads_5/0800_financial_office_it_operations_5/4200_1/formate/MT940_942.pdf
 * https://www.ksk-koeln.de/uebersicht-mt940-geschaeftsvorfallcodes.pdfx
 * http://www.kontopruef.de/mt940s.shtml
 * <p>
 * <b>DFÜ Field Description</b>
 * <pre>
 * 00                                      -  Buchungstext
 * 10                                      -  Primanoten-Nr.
 * 20, 21, 22, 23, 24, 25, 26, 27, 28, 29  -  Verwendungszweck
 * 30                                      -  Bankkennung Auftraggeber / Zahlungsempf.
 * 31                                      -  Kto.Nr. Auftraggeber / Zahlungsempf.
 * 32, 33                                  -  Name Auftraggeber / Zahlungsempf.
 * 34                                      -  Textschlüsselergänzung
 * 60, 61, 62, 63                          -  Fortsetzung Verwendungszweck
 * </pre>
 */

public class BCSMessage {

    private final String businessTransactionCode;
    private final Map<String, String> fieldMap;

    public BCSMessage(String businessTransactionCode, Map<String, String> fieldMap) {

        Preconditions.checkArgument(businessTransactionCode != null && !businessTransactionCode.isEmpty(), "businessTransactionCode can't be null or empy");
        Preconditions.checkArgument(businessTransactionCode.length() == 3, "businessTransactionCode length must be 3, but was: " + businessTransactionCode);
        Preconditions.checkArgument(fieldMap != null, "fieldMap can't be null");

        this.businessTransactionCode = businessTransactionCode;
        this.fieldMap = ImmutableMap.copyOf(fieldMap);
    }

    public String getBusinessTransactionCode() {
        return businessTransactionCode;
    }

    public Map<String, String> getFieldMap() {
        return fieldMap;
    }
}
