package com.qoomon.banking.swift.message;

import com.qoomon.banking.swift.message.block.*;
import com.qoomon.banking.swift.message.block.exception.BlockParseException;
import com.qoomon.banking.swift.message.exception.SwiftMessageParseException;

import java.io.Reader;
import java.util.List;

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
        TrailerBlock trailerBlock = null;

        String currentValidBlockId = BasicHeaderBlock.BLOCK_ID_1;


        List<GeneralBlock> blockList = blockParser.parse(swiftMessageTextReader);

        int currentBlockNumber = 0;
        for (GeneralBlock currentBlock : blockList) {
            currentBlockNumber++;

            switch (currentBlock.getId()) {
                case BasicHeaderBlock.BLOCK_ID_1: {
                    ensureValidBlockId(currentBlock.getId(), currentValidBlockId, currentBlockNumber);
                    basicHeaderBlock = BasicHeaderBlock.of(currentBlock);
                    currentValidBlockId = OutputApplicationHeaderBlock.BLOCK_ID_2;
                    break;
                }
                case OutputApplicationHeaderBlock.BLOCK_ID_2: {
                    ensureValidBlockId(currentBlock.getId(), currentValidBlockId, currentBlockNumber);
                    applicationHeaderBlock = OutputApplicationHeaderBlock.of(currentBlock);
                    currentValidBlockId = UserHeaderBlock.BLOCK_ID_3;
                    break;
                }
                case UserHeaderBlock.BLOCK_ID_3: {
                    ensureValidBlockId(currentBlock.getId(), currentValidBlockId, currentBlockNumber);
                    userHeaderBlock = UserHeaderBlock.of(currentBlock);
                    currentValidBlockId = TextBlock.BLOCK_ID_4;
                    break;
                }
                case TextBlock.BLOCK_ID_4: {
                    ensureValidBlockId(currentBlock.getId(), currentValidBlockId, currentBlockNumber);
                    textBlock = TextBlock.of(currentBlock);
                    currentValidBlockId = TrailerBlock.BLOCK_ID_5;
                    break;
                }
                case TrailerBlock.BLOCK_ID_5: {
                    ensureValidBlockId(currentBlock.getId(), currentValidBlockId, currentBlockNumber);
                    trailerBlock = TrailerBlock.of(currentBlock);
                    currentValidBlockId = "";
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
                trailerBlock);
    }

    private void ensureValidBlockId(String actualBlockId, String expectedBlockId, int currentBlockNumber) throws SwiftMessageParseException {
        if (!expectedBlockId.equals(actualBlockId)) {
            throw new SwiftMessageParseException("Expected Block '" + expectedBlockId + "', but was '" + actualBlockId + "'", currentBlockNumber);
        }
    }
}