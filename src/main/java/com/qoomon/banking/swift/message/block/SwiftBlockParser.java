package com.qoomon.banking.swift.message.block;

import com.google.common.collect.Iterables;
import com.qoomon.banking.swift.message.block.exception.BlockParseException;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.*;

/**
 * Created by qoomon on 07/07/16.
 */
public class SwiftBlockParser {

    private final char END_OF_STREAM = (char) -1;

    private static final Pattern BLOCK_PATTERN = Pattern.compile("^\\{(?<id>[0-9A-Z]):(?<content>.*)}", DOTALL);

    public List<GeneralBlock> parse(Reader swiftMessageTextReader) throws BlockParseException {

        List<GeneralBlock> result = new ArrayList<>(5);

        int lineNumber = 1;
        StringBuilder blockBuilder = new StringBuilder();
        int openingBrackets = 0;
        int closingBrackets = 0;
        char messageCharacter = 0;

        try {
            while ((messageCharacter = (char) swiftMessageTextReader.read()) != END_OF_STREAM) {

                // increment line index
                if (messageCharacter == '\n') {
                    lineNumber++;
                }

                if (blockBuilder.length() == 0) {
                    if (messageCharacter != "{".charAt(0)) {
                        if (!result.isEmpty()) {
                            throw new BlockParseException("Characters between blocks ar not allowed, but was: '" + messageCharacter + "' after block " +Iterables.getLast(result).getId(), lineNumber);
                        } else {
                            throw new BlockParseException("Characters before blocks ar not allowed, but was: '" + messageCharacter + "'", lineNumber);
                        }

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

                    Matcher blockMatcher = BLOCK_PATTERN.matcher(blockBuilder.toString());
                    if (!blockMatcher.matches()) {
                        if (!result.isEmpty()) {
                            throw new BlockParseException("Unexpected block structure after block " + Iterables.getLast(result).getId(), lineNumber);
                        } else {
                            throw new BlockParseException("Unexpected block structure start", lineNumber);
                        }
                    }

                    String blockId = blockMatcher.group("id");
                    String blockContent = blockMatcher.group("content");
                    GeneralBlock block = new GeneralBlock(blockId, blockContent);
                    result.add(block);

                    //reset block building
                    blockBuilder = new StringBuilder();
                    openingBrackets = 0;
                    closingBrackets = 0;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (openingBrackets != closingBrackets) {
            throw new BlockParseException("Parse error: unclosed '{'", lineNumber);
        }

        return result;
    }
}
