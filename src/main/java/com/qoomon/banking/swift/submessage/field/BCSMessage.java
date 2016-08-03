package com.qoomon.banking.swift.submessage.field;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * Created by qoomon on 25/07/16
 *
 * Banking Communication Standard Format Message
 * https://de.wikipedia.org/wiki/Banking_Communication_Standard
 * https://www.bayernlb.de/internet/media/de/internet_4/de_1/downloads_5/0800_financial_office_it_operations_5/4200_1/formate/MT940_942.pdf
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
