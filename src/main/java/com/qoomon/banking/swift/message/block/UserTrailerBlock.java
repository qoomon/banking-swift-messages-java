package com.qoomon.banking.swift.message.block;

import com.google.common.base.Preconditions;
import com.qoomon.banking.swift.message.block.exception.BlockFieldParseException;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <b>User Header Block</b>
 * <p>
 * <b>Format</b>
 * <pre>
 * 1:  {MAC:x...} - Message Authentication Code calculated based on the entire contents of the message using a key that has been exchanged with the destination and a secret algorithm. Found on message categories 1,2,4,5,7,8, most 6s and 304.
 * 2:  {PAC:x...} - Proprietary Authentication Code.
 * 3:  {CHK:x...} - Checksum calculated for all message types.
 * 4:  {TNG:x...} - Training.
 * 5:  {PDE:x...} - Possible Duplicate Emission added if user thinks the same message was sent previously
 * 6:  {DLM:x...} - Added by SWIFT if an urgent message (U) has not been delivered within 15 minutes, or a normal message (N) within 100 minutes.
 * </pre>
 * <p>
 * <b>Example</b><br>
 * {MAC:12345678}{CHK:123456789ABC}
 *
 * @see <a href="https://www.ibm.com/support/knowledgecenter/SSBTEG_4.3.0/com.ibm.wbia_adapters.doc/doc/swift/swift72.htm">https://www.ibm.com/support/knowledgecenter/SSBTEG_4.3.0/com.ibm.wbia_adapters.doc/doc/swift/swift72.htm</a>
 */
public class UserTrailerBlock {

    public static final String BLOCK_ID_5 = "5";

    public static final Pattern BLOCK_CONTENT_PATTERN = Pattern.compile("(\\{MAC:[^}]*\\})?(\\{PAC:[^}]*\\})?(\\{CHK:[^}]*\\})?(\\{TNG:[^}]*\\})?(\\{PDE:[^}]*\\})?(\\{DLM:[^}]*\\})?");

    public final Optional<String> messageAuthenticationCode;
    public final Optional<String> proprietaryAuthenticationCode;
    public final Optional<String> checksum;
    public final Optional<String> training;
    public final Optional<String> possibleDuplicateEmission;
    public final Optional<String> deliveryDelay;

    public UserTrailerBlock(String messageAuthenticationCode, String proprietaryAuthenticationCode, String checksum, String training, String possibleDuplicateEmission, String deliveryDelay) {
        this.messageAuthenticationCode = Optional.ofNullable(messageAuthenticationCode);
        this.proprietaryAuthenticationCode = Optional.ofNullable(proprietaryAuthenticationCode);
        this.checksum = Optional.ofNullable(checksum);
        this.training = Optional.ofNullable(training);
        this.possibleDuplicateEmission = Optional.ofNullable(possibleDuplicateEmission);
        this.deliveryDelay = Optional.ofNullable(deliveryDelay);
    }

    public static UserTrailerBlock of(GeneralBlock block) throws BlockFieldParseException {
        Preconditions.checkArgument(block.getId().equals(BLOCK_ID_5), "unexpected block id '" + block.getId() + "'");

        Matcher blockContentMatcher = BLOCK_CONTENT_PATTERN.matcher(block.getContent());
        if (!blockContentMatcher.matches()) {
            throw new BlockFieldParseException("Block '" + block.getId() + "' content did not match format " + BLOCK_CONTENT_PATTERN);
        }

        String messageAuthenticationCode = blockContentMatcher.group(1);
        String proprietaryAuthenticationCode = blockContentMatcher.group(2);
        String checksum = blockContentMatcher.group(3);
        String training = blockContentMatcher.group(4);
        String possibleDuplicateEmission = blockContentMatcher.group(5);
        String deliveryDelay = blockContentMatcher.group(6);

        return new UserTrailerBlock(messageAuthenticationCode, proprietaryAuthenticationCode,checksum, training, possibleDuplicateEmission, deliveryDelay);
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
