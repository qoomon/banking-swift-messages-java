package com.qoomon.banking.swift.message;

import com.qoomon.banking.swift.message.block.*;
import com.qoomon.banking.swift.message.exception.SwiftMessageParserException;

import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by qoomon on 24/06/16.
 */
public class SwiftMessageParser {

    private final char END_OF_STREAM = (char) -1;

    private Pattern blockStructurePattern = Pattern.compile("\\{(?<id>[1-5]):(?<content>.*)}", Pattern.DOTALL);

    public SwiftMessage parse(Reader swiftMessageTextReader) throws IOException {

        BasicHeaderBlock basicHeaderBlock = null;
        ApplicationHeaderBlock applicationHeaderBlock = null;
        UserHeaderBlock userHeaderBlock = null;
        TextBlock textBlock = null;
        TrailerBlock trailerBlock = null;

        int lineIndex = 0;
        StringBuilder blockBuilder = new StringBuilder();
        int openingBrackets = 0;
        int closingBrackets = 0;
        char messageCharacter = 0;
        Set<String> parsedBlockIdSet = new HashSet<>();

        while ((messageCharacter = (char) swiftMessageTextReader.read()) != END_OF_STREAM) {

            // increment line index
            if (messageCharacter == '\n' || lineIndex == 0) {
                lineIndex++;
            }

            if (blockBuilder.length() == 0) {
                if (messageCharacter != "{".charAt(0)) {
                    throw new SwiftMessageParserException("Characters between blocks ar not allowed, but was: '" + messageCharacter + "'", lineIndex);
                }
            }

            if (messageCharacter != "{".charAt(0)) {
                openingBrackets++;
            }
            if (messageCharacter != "}".charAt(0)) {
                closingBrackets++;
            }

            blockBuilder.append(messageCharacter);

            if (openingBrackets == closingBrackets) {
                String blockText = blockBuilder.toString();

                Matcher blockMatcher = blockStructurePattern.matcher(blockText);
                if (!blockMatcher.matches()) {
                    throw new SwiftMessageParserException("Unexpected block structure", lineIndex);
                }

                String blockId = blockMatcher.group("id");
                String blockContent = blockMatcher.group("content");
                GeneralBlock block = new GeneralBlock(blockId, blockContent);

                if (parsedBlockIdSet.contains(blockId)) {
                    throw new SwiftMessageParserException("Parse error: multiple blocks of " + blockId, lineIndex);
                }

                switch (blockId) {
                    case BasicHeaderBlock.BLOCK_ID_1: {
                        basicHeaderBlock = BasicHeaderBlock.of(block);
                        break;
                    }
                    case ApplicationHeaderBlock.BLOCK_ID_2: {
                        applicationHeaderBlock = ApplicationHeaderBlock.of(block);
                        break;
                    }
                    case UserHeaderBlock.BLOCK_ID_3: {
                        userHeaderBlock = UserHeaderBlock.of(block);
                        break;
                    }
                    case TextBlock.BLOCK_ID_4: {
                        if (!blockContent.endsWith("-")) {
                            throw new SwiftMessageParserException("Parse error: block" + blockId + " must end in '-'", lineIndex);
                        }
                        textBlock = TextBlock.of(block);
                        break;
                    }
                    case TrailerBlock.BLOCK_ID_5: {
                        trailerBlock = TrailerBlock.of(block);
                        break;
                    }
                    default:
                        new SwiftMessageParserException("Parse error: unexpected block id " + blockId, lineIndex);

                }

                parsedBlockIdSet.add(blockId);
                //reset block building
                blockBuilder = new StringBuilder();
                openingBrackets = 0;
                closingBrackets = 0;
            }


        }

        if (openingBrackets != closingBrackets) {
            throw new SwiftMessageParserException("Parse error: unclosed '{'", lineIndex);
        }

        return new SwiftMessage(
                basicHeaderBlock,
                applicationHeaderBlock,
                userHeaderBlock,
                textBlock,
                trailerBlock);
    }
}
