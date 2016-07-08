package com.qoomon.banking.swift.message;

import com.google.common.collect.ImmutableSet;
import com.qoomon.banking.swift.message.block.*;
import com.qoomon.banking.swift.message.block.exception.BlockParseException;
import com.qoomon.banking.swift.message.exception.SwiftMessageParseException;

import java.io.Reader;
import java.util.List;
import java.util.Set;

/**
 * Created by qoomon on 24/06/16.
 */
public class SwiftOutputMessageParser {

    private final SwiftBlockParser blockParser = new SwiftBlockParser();

    public SwiftOutputMessage parse(Reader swiftMessageTextReader) throws SwiftMessageParseException, BlockParseException {

        BasicHeaderBlock basicHeaderBlock = null;
        OutputApplicationHeaderBlock applicationHeaderBlock = null;
        UserHeaderBlock userHeaderBlock = null;
        TextBlock textBlock = null;
        UserTrailerBlock userTrailerBlock = null;
        SystemTrailerBlock systemTrailerBlock = null;

        Set<String> currentValidBlockIdSet = ImmutableSet.of(BasicHeaderBlock.BLOCK_ID_1);


        List<GeneralBlock> blockList = blockParser.parse(swiftMessageTextReader);

        int currentBlockNumber = 0;
        for (GeneralBlock currentBlock : blockList) {
            currentBlockNumber++;

            switch (currentBlock.getId()) {
                case BasicHeaderBlock.BLOCK_ID_1: {
                    ensureValidBlockId(currentBlock.getId(), currentValidBlockIdSet, currentBlockNumber);
                    basicHeaderBlock = BasicHeaderBlock.of(currentBlock);
                    currentValidBlockIdSet = ImmutableSet.of(OutputApplicationHeaderBlock.BLOCK_ID_2);
                    break;
                }
                case OutputApplicationHeaderBlock.BLOCK_ID_2: {
                    ensureValidBlockId(currentBlock.getId(), currentValidBlockIdSet, currentBlockNumber);
                    applicationHeaderBlock = OutputApplicationHeaderBlock.of(currentBlock);
                    currentValidBlockIdSet = ImmutableSet.of(UserHeaderBlock.BLOCK_ID_3);
                    break;
                }
                case UserHeaderBlock.BLOCK_ID_3: {
                    ensureValidBlockId(currentBlock.getId(), currentValidBlockIdSet, currentBlockNumber);
                    userHeaderBlock = UserHeaderBlock.of(currentBlock);
                    currentValidBlockIdSet = ImmutableSet.of(TextBlock.BLOCK_ID_4);
                    break;
                }
                case TextBlock.BLOCK_ID_4: {
                    ensureValidBlockId(currentBlock.getId(), currentValidBlockIdSet, currentBlockNumber);
                    textBlock = TextBlock.of(currentBlock);
                    currentValidBlockIdSet = ImmutableSet.of(UserTrailerBlock.BLOCK_ID_5, SystemTrailerBlock.BLOCK_ID_S);
                    break;
                }
                case UserTrailerBlock.BLOCK_ID_5: {
                    ensureValidBlockId(currentBlock.getId(), currentValidBlockIdSet, currentBlockNumber);
                    userTrailerBlock = UserTrailerBlock.of(currentBlock);
                    currentValidBlockIdSet = ImmutableSet.of(SystemTrailerBlock.BLOCK_ID_S);
                    break;
                }
                case SystemTrailerBlock.BLOCK_ID_S: {
                    ensureValidBlockId(currentBlock.getId(), currentValidBlockIdSet, currentBlockNumber);
                    systemTrailerBlock = SystemTrailerBlock.of(currentBlock);
                    currentValidBlockIdSet = ImmutableSet.of();
                    break;
                }
                default:
                    throw new SwiftMessageParseException("unexpected block id '" + currentBlock.getId() + "'", currentBlockNumber);
            }
        }

        return new SwiftOutputMessage(
                basicHeaderBlock,
                applicationHeaderBlock,
                userHeaderBlock,
                textBlock,
                userTrailerBlock,
                systemTrailerBlock);
    }

    private void ensureValidBlockId(String actualBlockId, Set<String> expectedBlockIdSet, int currentBlockNumber) throws SwiftMessageParseException {
        if (!expectedBlockIdSet.contains(actualBlockId)) {
            throw new SwiftMessageParseException("Expected Block '" + expectedBlockIdSet + "', but was '" + actualBlockId + "'", currentBlockNumber);
        }
    }

}