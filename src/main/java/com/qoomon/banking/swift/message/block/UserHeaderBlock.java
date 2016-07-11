package com.qoomon.banking.swift.message.block;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.qoomon.banking.swift.message.block.exception.BlockFieldParseException;
import com.qoomon.banking.swift.message.block.exception.BlockParseException;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

    public final Optional<String> bankingPriorityCode;
    public final String messageUserReference;
    public final ImmutableMap<String, GeneralBlock> additionalSubblocks;

    public UserHeaderBlock(String bankingPriorityCode, String messageUserReference, Map<String, GeneralBlock> additionalSubblocks) {
        this.bankingPriorityCode = Optional.ofNullable(bankingPriorityCode);
        this.messageUserReference = Preconditions.checkNotNull(messageUserReference);
        this.additionalSubblocks = ImmutableMap.copyOf(Preconditions.checkNotNull(additionalSubblocks));
    }

    public static UserHeaderBlock of(GeneralBlock block) throws BlockFieldParseException {
        Preconditions.checkArgument(block.getId().equals(BLOCK_ID_3), "unexpected block id '" + block.getId() + "'");

        SwiftBlockReader blockReader = new SwiftBlockReader(new StringReader(block.getContent()));

        String bankingPriorityCode = null;
        String messageUserReference = null;
        Map<String, GeneralBlock> additionalSubblocks = new HashMap<>();

        try {
            GeneralBlock subblock;
            while ((subblock = blockReader.readBlock()) != null) {
                switch (subblock.getId()) {
                    case "113":
                        bankingPriorityCode = subblock.getContent(); // TODO regex check
                        break;
                    case "108":
                        messageUserReference = subblock.getContent(); // TODO regex check
                        break;
                    default:
                        additionalSubblocks.put(subblock.getId(), subblock);
                }
            }
        } catch (BlockParseException e) {
            throw new BlockFieldParseException("Block '" + block.getId() + "' content error", e);
        }

        return new UserHeaderBlock(bankingPriorityCode, messageUserReference, additionalSubblocks);
    }

    public Optional<String> getBankingPriorityCode() {
        return bankingPriorityCode;
    }

    public String getMessageUserReference() {
        return messageUserReference;
    }
}
