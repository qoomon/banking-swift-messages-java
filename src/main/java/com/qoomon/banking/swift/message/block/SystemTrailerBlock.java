package com.qoomon.banking.swift.message.block;

import com.google.common.base.Preconditions;
import com.qoomon.banking.swift.message.block.exception.BlockFieldParseException;
import com.qoomon.banking.swift.message.block.exception.BlockParseException;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * <b>System Trail Block</b>
 * <p>
 * System trailers convey additional or special details about the SWIFT message. If any of the first three system trailers are present, they occur in the following order. The remaining system trailers can occur in any order.
 * <p>
 * <b>Sub Blocks</b>
 * <pre>
 * 1: CHK - Checksum
 * 2: SYS - System Originated Message
 * 3: TNG - Training
 * 4: PDM - Possible Duplicate Message
 * 5: DLM - Delayed Message
 * 6: MRF - Message Reference
 * </pre>
 *
 * @see <a href="https://msdn.microsoft.com/en-us/library/ee350615.aspx">https://msdn.microsoft.com/en-us/library/ee350615.aspx</a>
 */
public class SystemTrailerBlock implements SwiftBlock {

    public static final String BLOCK_ID_S = "S";

    private final Optional<String> checksum;

    private final Optional<String> systemOriginatedMessage;

    private final Optional<String> training;

    private final Optional<String> possibleDuplicateMessage;

    private final Optional<String> delayedMessage;

    private final Optional<String> messageReference;


    private final Map<String, GeneralBlock> additionalSubblocks;

    public SystemTrailerBlock(String checksum,
                              String systemOriginatedMessage,
                              String training,
                              String possibleDuplicateMessage,
                              String delayedMessage,
                              String messageReference,
                              Map<String, GeneralBlock> additionalSubblocks) {

        Preconditions.checkArgument(additionalSubblocks != null, "additionalSubblocks can't be null");

        this.checksum = Optional.ofNullable(checksum);
        this.systemOriginatedMessage = Optional.ofNullable(systemOriginatedMessage);
        this.training = Optional.ofNullable(training);
        this.possibleDuplicateMessage = Optional.ofNullable(possibleDuplicateMessage);
        this.delayedMessage = Optional.ofNullable(delayedMessage);
        this.messageReference = Optional.ofNullable(messageReference);
        this.additionalSubblocks = additionalSubblocks;
    }


    public static SystemTrailerBlock of(GeneralBlock block) throws BlockFieldParseException {
        Preconditions.checkArgument(block.getId().equals(BLOCK_ID_S), "unexpected block id '%s'", block.getId());

        SwiftBlockReader blockReader = new SwiftBlockReader(new StringReader(block.getContent()));

        String checksum = null;
        String systemOriginatedMessage = null;
        String training = null;
        String possibleDuplicateMessage = null;
        String delayedMessage = null;
        String messageReference = null;
        Map<String, GeneralBlock> additionalSubblocks = new HashMap<>();

        try {
            GeneralBlock subblock;
            while ((subblock = blockReader.readBlock()) != null) {
                switch (subblock.getId()) {
                    case "CHK":
                        checksum = subblock.getContent(); // TODO regex check
                        break;
                    case "SYS":
                        systemOriginatedMessage = subblock.getContent(); // TODO regex check
                        break;
                    case "TNG":
                        training = subblock.getContent(); // TODO regex check
                        break;
                    case "PDM":
                        possibleDuplicateMessage = subblock.getContent(); // TODO regex check
                        break;
                    case "DLM":
                        messageReference = subblock.getContent(); // TODO regex check
                        break;
                    case "MRF":
                        messageReference = subblock.getContent(); // TODO regex check
                        break;
                    default:
                        additionalSubblocks.put(subblock.getId(), subblock);
                        break;
                }
            }
        } catch (BlockParseException e) {
            throw new BlockFieldParseException("Block '" + block.getId() + "' content error", e);
        }

        return new SystemTrailerBlock(
                checksum,
                systemOriginatedMessage,
                training,
                possibleDuplicateMessage,
                delayedMessage,
                messageReference,
                additionalSubblocks
        );
    }

    public Optional<String> getChecksum() {
        return checksum;
    }

    public Optional<String> getSystemOriginatedMessage() {
        return systemOriginatedMessage;
    }

    public Optional<String> getTraining() {
        return training;
    }

    public Optional<String> getPossibleDuplicateMessage() {
        return possibleDuplicateMessage;
    }

    public Optional<String> getDelayedMessage() {
        return delayedMessage;
    }

    public Optional<String> getMessageReference() {
        return messageReference;
    }

    public GeneralBlock getAdditionalSubblocks(String id) {
        return additionalSubblocks.get(id);
    }

    @Override
    public String getId() {
        return BLOCK_ID_S;
    }

    @Override
    public String getContent() {
        StringBuilder contentBuilder = new StringBuilder();
        if(checksum.isPresent()) {
            contentBuilder.append(BlockUtils.swiftTextOf("CHK", checksum.get()));
        }
        if(systemOriginatedMessage.isPresent()) {
            contentBuilder.append(BlockUtils.swiftTextOf("SYS", systemOriginatedMessage.get()));
        }
        if(training.isPresent()) {
            contentBuilder.append(BlockUtils.swiftTextOf("TNG", training.get()));
        }
        if(possibleDuplicateMessage.isPresent()) {
            contentBuilder.append(BlockUtils.swiftTextOf("PDM", possibleDuplicateMessage.get()));
        }
        if(delayedMessage.isPresent()) {
            contentBuilder.append(BlockUtils.swiftTextOf("DLM", delayedMessage.get()));
        }
        if(messageReference.isPresent()) {
            contentBuilder.append(BlockUtils.swiftTextOf("MRF", messageReference.get()));
        }
        for (GeneralBlock subblock : additionalSubblocks.values()) {
            contentBuilder.append(BlockUtils.swiftTextOf(subblock.getId(), subblock.getContent()));
        }
        return contentBuilder.toString();
    }
}

