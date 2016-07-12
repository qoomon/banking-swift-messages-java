package com.qoomon.banking.swift.message;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.qoomon.banking.swift.message.block.*;
import com.qoomon.banking.swift.message.block.exception.BlockFieldParseException;
import com.qoomon.banking.swift.message.block.exception.BlockParseException;
import com.qoomon.banking.swift.message.exception.SwiftMessageParseException;

import java.io.Reader;
import java.util.Set;

/**
 * Created by qoomon on 24/06/16.
 */
public class SwiftMessageReader {

    private final static Set<String> MESSAGE_START_BLOCK_ID_SET = ImmutableSet.of(BasicHeaderBlock.BLOCK_ID_1);

    private final SwiftBlockReader swiftBlockReader;

    // reset message builder
    private BasicHeaderBlock basicHeaderBlock = null;
    private ApplicationHeaderBlock applicationHeaderBlock = null;
    private UserHeaderBlock userHeaderBlock = null;
    private TextBlock textBlock = null;
    private UserTrailerBlock userTrailerBlock = null;
    private SystemTrailerBlock systemTrailerBlock = null;


    public SwiftMessageReader(Reader textReader) {

        Preconditions.checkArgument(textReader != null, "textReader can't be null");

        this.swiftBlockReader = new SwiftBlockReader(textReader);
    }

    public SwiftMessage readMessage() throws SwiftMessageParseException {
        SwiftMessage message = null;

        try {
            Set<String> nextValidBlockIdSet = MESSAGE_START_BLOCK_ID_SET;
            GeneralBlock nextBlock = swiftBlockReader.readBlock();
            while (nextBlock != null) {
                ensureValidNextBlock(nextBlock, nextValidBlockIdSet, swiftBlockReader);
                GeneralBlock currentBlock = nextBlock;
                nextBlock = swiftBlockReader.readBlock();

                switch (currentBlock.getId()) {
                    case BasicHeaderBlock.BLOCK_ID_1: {
                        basicHeaderBlock = BasicHeaderBlock.of(currentBlock);
                        nextValidBlockIdSet = ImmutableSet.of(ApplicationHeaderOutputBlock.BLOCK_ID_2);
                        break;
                    }
                    case ApplicationHeaderOutputBlock.BLOCK_ID_2: {
                        applicationHeaderBlock = ApplicationHeaderBlock.of(currentBlock);
                        nextValidBlockIdSet = ImmutableSet.of(UserHeaderBlock.BLOCK_ID_3);
                        break;
                    }
                    case UserHeaderBlock.BLOCK_ID_3: {
                        userHeaderBlock = UserHeaderBlock.of(currentBlock);
                        nextValidBlockIdSet = ImmutableSet.of(TextBlock.BLOCK_ID_4);
                        break;
                    }
                    case TextBlock.BLOCK_ID_4: {
                        textBlock = TextBlock.of(currentBlock);
                        nextValidBlockIdSet = ImmutableSet.of(UserTrailerBlock.BLOCK_ID_5, SystemTrailerBlock.BLOCK_ID_S);
                        break;
                    }
                    case UserTrailerBlock.BLOCK_ID_5: {
                        userTrailerBlock = UserTrailerBlock.of(currentBlock);
                        nextValidBlockIdSet = ImmutableSet.of(SystemTrailerBlock.BLOCK_ID_S);
                        break;
                    }
                    case SystemTrailerBlock.BLOCK_ID_S: {
                        systemTrailerBlock = SystemTrailerBlock.of(currentBlock);
                        nextValidBlockIdSet = ImmutableSet.of();
                        break;
                    }
                    default:
                        throw new SwiftMessageParseException("unexpected block id '" + currentBlock.getId() + "'", swiftBlockReader.getLineNumber());
                }

                // finish message
                if (nextBlock == null || MESSAGE_START_BLOCK_ID_SET.contains(nextBlock.getId())) {
                    message = new SwiftMessage(
                            basicHeaderBlock,
                            applicationHeaderBlock,
                            userHeaderBlock,
                            textBlock,
                            userTrailerBlock,
                            systemTrailerBlock);

                    // reset message builder
                    basicHeaderBlock = null;
                    applicationHeaderBlock = null;
                    userHeaderBlock = null;
                    textBlock = null;
                    userTrailerBlock = null;
                    systemTrailerBlock = null;
                    nextValidBlockIdSet = MESSAGE_START_BLOCK_ID_SET;
                }
            }
        } catch (BlockParseException | BlockFieldParseException e) {
            throw new SwiftMessageParseException("Block error", swiftBlockReader.getLineNumber(), e);
        }

        return message;
    }

    private void ensureValidNextBlock(GeneralBlock block, Set<String> expectedBlockIdSet, SwiftBlockReader swiftBlockReader) throws SwiftMessageParseException {
        String blockId = block != null ? block.getId() : null;
        if (!expectedBlockIdSet.contains(blockId)) {
            throw new SwiftMessageParseException("Expected Block '" + expectedBlockIdSet + "', but was '" + blockId + "'", swiftBlockReader.getLineNumber());
        }
    }

}