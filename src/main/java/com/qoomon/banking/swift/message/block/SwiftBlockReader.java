package com.qoomon.banking.swift.message.block;

import com.google.common.base.Preconditions;
import com.qoomon.banking.swift.message.block.exception.BlockParseException;

import java.io.IOException;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.*;

/**
 * Created by qoomon on 07/07/16.
 */
public class SwiftBlockReader {

    private static final char END_OF_STREAM = (char) -1;

    private static final Pattern BLOCK_PATTERN = Pattern.compile("^\\{(?<id>[^:]+):(?<content>.*)}", DOTALL);

    private final Reader textReader;

    private int lineNumber = 1;
    private int lineCharIndex = 0;
    private int openingBrackets = 0;
    private int closingBrackets = 0;

    public SwiftBlockReader(Reader textReader) {

        Preconditions.checkArgument(textReader != null, "textReader can't be null");

        this.textReader = textReader;
    }

    public GeneralBlock readBlock() throws BlockParseException {

        GeneralBlock block = null;
        StringBuilder blockBuilder = new StringBuilder();

        try {
            char messageCharacter;
            while (block == null && (messageCharacter = (char) textReader.read()) != END_OF_STREAM) {

                // increment line index
                if (messageCharacter == '\n') {
                    lineNumber++;
                    lineCharIndex = 0;
                }

                lineCharIndex++;

                if (blockBuilder.length() == 0 && messageCharacter != "{".charAt(0)) {
                    if (messageCharacter == "}".charAt(0)) {
                        throw new BlockParseException("Found closing bracket without preceding opening bracket", lineNumber);
                    } else {
                        throw new BlockParseException("No characters are allowed outside of blocks, but was: '" + messageCharacter + "'", lineNumber);
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
                        if (openingBrackets != 0) {
                            throw new BlockParseException("Unexpected block structure", lineNumber);
                        } else {
                            throw new BlockParseException("Unexpected block structure start", lineNumber);
                        }
                    }

                    String blockId = blockMatcher.group("id");
                    String blockContent = blockMatcher.group("content");
                    block = new GeneralBlock(blockId, blockContent);

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

        return block;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public int getLineCharIndex() {
        return lineCharIndex;
    }
}
