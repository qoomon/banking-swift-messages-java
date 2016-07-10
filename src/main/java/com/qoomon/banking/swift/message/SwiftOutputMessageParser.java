package com.qoomon.banking.swift.message;

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
public class SwiftOutputMessageParser {


    public SwiftOutputMessage parse(Reader swiftMessageTextReader) throws SwiftMessageParseException {

        BasicHeaderBlock basicHeaderBlock = null;
        OutputApplicationHeaderBlock applicationHeaderBlock = null;
        UserHeaderBlock userHeaderBlock = null;
        TextBlock textBlock = null;
        UserTrailerBlock userTrailerBlock = null;
        SystemTrailerBlock systemTrailerBlock = null;

        Set<String> currentValidBlockIdSet = ImmutableSet.of(BasicHeaderBlock.BLOCK_ID_1);

        SwiftBlockReader swiftBlockReader = new SwiftBlockReader(swiftMessageTextReader);

        try {
            GeneralBlock currentBlock;
            while ((currentBlock = swiftBlockReader.readBlock()) != null) {

                switch (currentBlock.getId()) {
                    case BasicHeaderBlock.BLOCK_ID_1: {
                        ensureValidBlockId(currentBlock.getId(), currentValidBlockIdSet, swiftBlockReader);
                        basicHeaderBlock = BasicHeaderBlock.of(currentBlock);
                        currentValidBlockIdSet = ImmutableSet.of(OutputApplicationHeaderBlock.BLOCK_ID_2);
                        break;
                    }
                    case OutputApplicationHeaderBlock.BLOCK_ID_2: {
                        ensureValidBlockId(currentBlock.getId(), currentValidBlockIdSet, swiftBlockReader);
                        applicationHeaderBlock = OutputApplicationHeaderBlock.of(currentBlock);
                        currentValidBlockIdSet = ImmutableSet.of(UserHeaderBlock.BLOCK_ID_3);
                        break;
                    }
                    case UserHeaderBlock.BLOCK_ID_3: {
                        ensureValidBlockId(currentBlock.getId(), currentValidBlockIdSet, swiftBlockReader);
                        userHeaderBlock = UserHeaderBlock.of(currentBlock);
                        currentValidBlockIdSet = ImmutableSet.of(TextBlock.BLOCK_ID_4);
                        break;
                    }
                    case TextBlock.BLOCK_ID_4: {
                        ensureValidBlockId(currentBlock.getId(), currentValidBlockIdSet, swiftBlockReader);
                        textBlock = TextBlock.of(currentBlock);
                        currentValidBlockIdSet = ImmutableSet.of(UserTrailerBlock.BLOCK_ID_5, SystemTrailerBlock.BLOCK_ID_S);
                        break;
                    }
                    case UserTrailerBlock.BLOCK_ID_5: {
                        ensureValidBlockId(currentBlock.getId(), currentValidBlockIdSet, swiftBlockReader);
                        userTrailerBlock = UserTrailerBlock.of(currentBlock);
                        currentValidBlockIdSet = ImmutableSet.of(SystemTrailerBlock.BLOCK_ID_S);
                        break;
                    }
                    case SystemTrailerBlock.BLOCK_ID_S: {
                        ensureValidBlockId(currentBlock.getId(), currentValidBlockIdSet, swiftBlockReader);
                        systemTrailerBlock = SystemTrailerBlock.of(currentBlock);
                        currentValidBlockIdSet = ImmutableSet.of();
                        break;
                    }
                    default:
                        throw new SwiftMessageParseException("unexpected block id '" + currentBlock.getId() + "'", swiftBlockReader.getLineNumber());
                }
            }
        } catch (BlockParseException | BlockFieldParseException e) {
            throw new SwiftMessageParseException("Blockerror", swiftBlockReader.getLineNumber(), e);
        }

        return new SwiftOutputMessage(
                basicHeaderBlock,
                applicationHeaderBlock,
                userHeaderBlock,
                textBlock,
                userTrailerBlock,
                systemTrailerBlock);
    }

    private void ensureValidBlockId(String actualBlockId, Set<String> expectedBlockIdSet, SwiftBlockReader swiftBlockReader) throws SwiftMessageParseException {
        if (!expectedBlockIdSet.contains(actualBlockId)) {
            throw new SwiftMessageParseException("Expected Block '" + expectedBlockIdSet + "', but was '" + actualBlockId + "'", swiftBlockReader.getLineNumber());
        }
    }

}