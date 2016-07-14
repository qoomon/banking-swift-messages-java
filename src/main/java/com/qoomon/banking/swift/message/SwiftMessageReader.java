package com.qoomon.banking.swift.message;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.qoomon.banking.swift.message.block.*;
import com.qoomon.banking.swift.message.exception.SwiftMessageParseException;

import java.io.Reader;
import java.util.Set;

/**
 * Created by qoomon on 24/06/16.
 */
public class SwiftMessageReader {

    private final static Set<String> MESSAGE_START_BLOCK_ID_SET = ImmutableSet.of(BasicHeaderBlock.BLOCK_ID_1);

    private final SwiftBlockReader blockReader;

    private GeneralBlock currentBlock = null;
    private GeneralBlock nextBlock = null;


    public SwiftMessageReader(Reader textReader) {

        Preconditions.checkArgument(textReader != null, "textReader can't be null");

        this.blockReader = new SwiftBlockReader(textReader);
    }

    public SwiftMessage readMessage() throws SwiftMessageParseException {
        try {
            if (currentBlock == null) {
                nextBlock = blockReader.readBlock();
            }

            SwiftMessage message = null;

            // message fields (builder) // TODO create builder
            BasicHeaderBlock messageBuilderBasicHeaderBlock = null;
            ApplicationHeaderBlock messageBuilderApplicationHeaderBlock = null;
            UserHeaderBlock messageBuilderUserHeaderBlock = null;
            TextBlock messageBuilderTextBlock = null;
            UserTrailerBlock messageBuilderUserTrailerBlock = null;
            SystemTrailerBlock messageBuilderSystemTrailerBlock = null;

            Set<String> nextValidBlockIdSet = MESSAGE_START_BLOCK_ID_SET;

            while (message == null && nextBlock != null) {

                ensureValidNextBlock(nextBlock, nextValidBlockIdSet, blockReader);

                currentBlock = nextBlock;
                nextBlock = blockReader.readBlock();

                switch (currentBlock.getId()) {
                    case BasicHeaderBlock.BLOCK_ID_1: {
                        messageBuilderBasicHeaderBlock = BasicHeaderBlock.of(currentBlock);
                        nextValidBlockIdSet = ImmutableSet.of(ApplicationHeaderOutputBlock.BLOCK_ID_2);
                        break;
                    }
                    case ApplicationHeaderOutputBlock.BLOCK_ID_2: {
                        messageBuilderApplicationHeaderBlock = ApplicationHeaderBlock.of(currentBlock);
                        nextValidBlockIdSet = ImmutableSet.of(UserHeaderBlock.BLOCK_ID_3);
                        break;
                    }
                    case UserHeaderBlock.BLOCK_ID_3: {
                        messageBuilderUserHeaderBlock = UserHeaderBlock.of(currentBlock);
                        nextValidBlockIdSet = ImmutableSet.of(TextBlock.BLOCK_ID_4);
                        break;
                    }
                    case TextBlock.BLOCK_ID_4: {
                        messageBuilderTextBlock = TextBlock.of(currentBlock);
                        nextValidBlockIdSet = ImmutableSet.of(UserTrailerBlock.BLOCK_ID_5, SystemTrailerBlock.BLOCK_ID_S);
                        break;
                    }
                    case UserTrailerBlock.BLOCK_ID_5: {
                        messageBuilderUserTrailerBlock = UserTrailerBlock.of(currentBlock);
                        nextValidBlockIdSet = ImmutableSet.of(SystemTrailerBlock.BLOCK_ID_S);
                        break;
                    }
                    case SystemTrailerBlock.BLOCK_ID_S: {
                        messageBuilderSystemTrailerBlock = SystemTrailerBlock.of(currentBlock);
                        nextValidBlockIdSet = ImmutableSet.of();
                        break;
                    }
                    default:
                        throw new SwiftMessageParseException("unexpected block id '" + currentBlock.getId() + "'", blockReader.getLineNumber());
                }

                // finish message
                if (nextBlock == null || MESSAGE_START_BLOCK_ID_SET.contains(nextBlock.getId())) {
                    message = new SwiftMessage(
                            messageBuilderBasicHeaderBlock,
                            messageBuilderApplicationHeaderBlock,
                            messageBuilderUserHeaderBlock,
                            messageBuilderTextBlock,
                            messageBuilderUserTrailerBlock,
                            messageBuilderSystemTrailerBlock);
                }
            }

            return message;
        } catch (SwiftMessageParseException e) {
            throw e;
        } catch (Exception e) {
            throw new SwiftMessageParseException(e.getMessage(), blockReader.getLineNumber(), e);
        }
    }

    private void ensureValidNextBlock(GeneralBlock block, Set<String> expectedBlockIdSet, SwiftBlockReader blockReader) throws SwiftMessageParseException {
        String blockId = block != null ? block.getId() : null;
        if (!expectedBlockIdSet.contains(blockId)) {
            throw new SwiftMessageParseException("Expected Block '" + expectedBlockIdSet + "', but was '" + blockId + "'", blockReader.getLineNumber());
        }
    }

}