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
 * 1:  {113:xxxx} - Banking Priority Code of 4 alphanumeric characters - Optional
 * 2:  {108:x...} - Indicates the Message User Reference (MUR) value, which can be up to 16 characters, and will be returned in the ACK
 * </pre>
 * <p>
 * <b>Example</b><br>
 * {113:SEPA}{108:ILOVESEPA}
 *
 * @see <a href="https://www.ibm.com/support/knowledgecenter/SSBTEG_4.3.0/com.ibm.wbia_adapters.doc/doc/swift/swift72.htm">https://www.ibm.com/support/knowledgecenter/SSBTEG_4.3.0/com.ibm.wbia_adapters.doc/doc/swift/swift72.htm</a>
 */
public class UserHeaderBlock {

    public static final String BLOCK_ID_3 = "3";

    public static final Pattern BLOCK_CONTENT_PATTERN = Pattern.compile("(\\{113:[^}]{4}\\})?(\\{108:[^}]{1,16}\\})");

    public final Optional<String> bankingPriorityCode;
    public final String messageUserReference;

    public UserHeaderBlock(String bankingPriorityCode, String messageUserReference) {
        this.bankingPriorityCode = Optional.ofNullable(bankingPriorityCode);
        this.messageUserReference = Preconditions.checkNotNull(messageUserReference);
    }

    public static UserHeaderBlock of(GeneralBlock block) throws BlockFieldParseException {
        Preconditions.checkArgument(block.getId().equals(BLOCK_ID_3), "unexpected block id '" + block.getId() + "'");

        Matcher blockContentMatcher = BLOCK_CONTENT_PATTERN.matcher(block.getContent());
        if (!blockContentMatcher.matches()) {
            throw new BlockFieldParseException("Block '" + block.getId() + "' content did not match format " + BLOCK_CONTENT_PATTERN);
        }

        String bankingPriorityCode = blockContentMatcher.group(1);
        String messageUserReference = blockContentMatcher.group(2);

        return new UserHeaderBlock(bankingPriorityCode, messageUserReference);
    }

    public Optional<String> getBankingPriorityCode() {
        return bankingPriorityCode;
    }

    public String getMessageUserReference() {
        return messageUserReference;
    }
}
