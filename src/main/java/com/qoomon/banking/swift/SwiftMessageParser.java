package com.qoomon.banking.swift;

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

        SwiftMessageTextBlock swiftMessageTextBlock = null;

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
                String block = blockBuilder.toString();

                Matcher matcher = blockStructurePattern.matcher(block);
                if (!matcher.matches()) {
                    throw new SwiftMessageParserException("Unexpected block structure", lineIndex);
                }

                String blockId = matcher.group("id");
                String blockContent = matcher.group("content");

                if (parsedBlockIdSet.contains(blockId)) {
                    throw new SwiftMessageParserException("Parse error: multiple blocks of " + blockId, lineIndex);
                }

                switch (blockId) {
                    case "1": {

                        break;
                    }
                    case "2": {

                        break;
                    }
                    case "3": {

                        break;
                    }
                    case SwiftMessageTextBlock.ID_4: {
                        if (!blockContent.endsWith("-")) {
                            throw new SwiftMessageParserException("Parse error: block" + blockId + " must end in '-'", lineIndex);
                        }
                        // remove trailing '-'
                        blockContent = blockContent.replaceFirst("-$", "");
                        swiftMessageTextBlock = new SwiftMessageTextBlock(blockContent);
                        break;
                    }
                    case "5": {

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

        return new SwiftMessage(swiftMessageTextBlock);
    }
}
