package com.qoomon.banking.swift.message.block;

import com.google.common.base.Preconditions;
import com.qoomon.banking.swift.message.block.exception.BlockFieldParseException;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextBlock {

    public static final String BLOCK_ID_4 = "4";

    public static final Pattern FIELD_PATTERN = Pattern.compile("([^\\n]+)?\\n(.*\\n-)", Pattern.DOTALL);

    private final Optional<String> infoLine;
    private final String content;

    public TextBlock(String infoLine, String content) {
        Preconditions.checkArgument(content != null, "content can't be null");

        this.infoLine = Optional.ofNullable(infoLine);
        this.content = content;
    }

    public static TextBlock of(GeneralBlock block) throws BlockFieldParseException {
        Preconditions.checkArgument(block.getId().equals(BLOCK_ID_4), "unexpected block id '{}'", block.getId());

        Matcher blockMatcher = FIELD_PATTERN.matcher(block.getContent());
        if (!blockMatcher.matches()) {
            throw new BlockFieldParseException("Block " + BLOCK_ID_4 + " did not match pattern " + FIELD_PATTERN);
        }
        // remove first empty line
        String infoLine = blockMatcher.group(1);
        String content = blockMatcher.group(2);

        return new TextBlock(infoLine, content);
    }

    public String getContent() {
        return content;
    }

    public Optional<String> getInfoLine() {
        return infoLine;
    }
}
