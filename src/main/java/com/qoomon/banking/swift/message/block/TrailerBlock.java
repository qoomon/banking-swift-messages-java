package com.qoomon.banking.swift.message.block;

import com.google.common.base.Preconditions;
import com.qoomon.banking.swift.message.block.exception.BlockParseException;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <b>User Header Block</b>
 * <p>
 * <b>Format</b>
 * <pre>
 * 1:  {MAC:x...} - Message Authentication Code calculated based on the entire contents of the message using a key that has been exchanged with the destination and a secret algorithm. Found on message categories 1,2,4,5,7,8, most 6s and 304.
 * 2:  {CHK:x...} - Checksum calculated for all message types.
 * 3:  {PDE:x...} - Possible Duplicate Emission added if user thinks the same message was sent previously
 * 4:  {DLM:x...} - Added by SWIFT if an urgent message (U) has not been delivered within 15 minutes, or a normal message (N) within 100 minutes.
 * </pre>
 * <p>
 * <b>Example</b><br>
 * {MAC:12345678}{CHK:123456789ABC}
 *
 * @see <a href="https://www.ibm.com/support/knowledgecenter/SSBTEG_4.3.0/com.ibm.wbia_adapters.doc/doc/swift/swift72.htm">https://www.ibm.com/support/knowledgecenter/SSBTEG_4.3.0/com.ibm.wbia_adapters.doc/doc/swift/swift72.htm</a>
 */
public class TrailerBlock {

    public static final String BLOCK_ID_5 = "5";

    public static final Pattern BLOCK_CONTENT_PATTERN = Pattern.compile("(\\{MAC:[^}]+\\})?(\\{CHK:[^}]+\\})?(\\{PDE:[^}]+\\})?(\\{DLM:[^}]+\\})?");

    public final Optional<String> messageAuthenticationCode;
    public final Optional<String> checksum;
    public final Optional<String> possibleDuplicateEmission;
    public final Optional<String> deliveryDelay;

    public TrailerBlock(String messageAuthenticationCode, String checksum, String possibleDuplicateEmission, String deliveryDelay) {
        this.messageAuthenticationCode = Optional.ofNullable(messageAuthenticationCode);
        this.checksum = Optional.ofNullable(checksum);
        this.possibleDuplicateEmission = Optional.ofNullable(possibleDuplicateEmission);
        this.deliveryDelay = Optional.ofNullable(deliveryDelay);
    }

    public static TrailerBlock of(GeneralBlock block) throws BlockParseException {
        Preconditions.checkArgument(block.getId().equals(BLOCK_ID_5), "unexpected block id '" + block.getId() + "'");

        Matcher blockContentMatcher = BLOCK_CONTENT_PATTERN.matcher(block.getContent());
        if (!blockContentMatcher.matches()) {
            throw new BlockParseException("Block '" + block.getId() + "' content did not match format " + BLOCK_CONTENT_PATTERN);
        }

        String messageAuthenticationCode = blockContentMatcher.group(1);
        String checksum = blockContentMatcher.group(2);
        String possibleDuplicateEmission = blockContentMatcher.group(3);
        String deliveryDelay = blockContentMatcher.group(4);

        return new TrailerBlock(messageAuthenticationCode, checksum, possibleDuplicateEmission, deliveryDelay);
    }

    public Optional<String> getMessageAuthenticationCode() {
        return messageAuthenticationCode;
    }

    public Optional<String> getChecksum() {
        return checksum;
    }

    public Optional<String> getPossibleDuplicateEmission() {
        return possibleDuplicateEmission;
    }

    public Optional<String> getDeliveryDelay() {
        return deliveryDelay;
    }
}
