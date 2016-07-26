package com.qoomon.banking.swift.submessage.field;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by qoomon on 25/07/16.
 * http://www.kontopruef.de/mt940s.shtml
 * <p>
 * additio
 */
public class BCSMessageParser {

    private final static Pattern BUSINESS_TRANSACTION_CODE_PATTERN = Pattern.compile("^([0-9A-Z]{3,4})(.*)", Pattern.DOTALL);
    private final static Pattern SEPA_FIELD_PATTERN = Pattern.compile("^[?]([0-9]{2})([^?]*)");


    public BCSMessage parseMessage(String messageText) {
        // join multiline to one line
        messageText = messageText.replaceAll("\\n", "");
        Matcher matcher = BUSINESS_TRANSACTION_CODE_PATTERN.matcher(messageText);
        if (!matcher.matches()) {
            throw new RuntimeException("messageText " + messageText + " didn't match " + matcher.pattern());
        }
        String messageBTC = matcher.group(1);
        String messageContent = matcher.group(2);

        Map<String, String> messageFieldMap = new HashMap<>();

        int parseIndex = 0;
        Matcher messageFieldMatcher = SEPA_FIELD_PATTERN.matcher(messageContent);
        while (messageFieldMatcher.region(parseIndex, messageContent.length()).find()) {
            parseIndex = messageFieldMatcher.end();
            String fieldId = messageFieldMatcher.group(1);
            String fieldContent = messageFieldMatcher.group(2);
            messageFieldMap.put(fieldId, fieldContent);
        }

        if (parseIndex != messageContent.length()) {
            throw new RuntimeException("unparsed message part");
        }

        return new BCSMessage(messageBTC, messageFieldMap);

    }


}
