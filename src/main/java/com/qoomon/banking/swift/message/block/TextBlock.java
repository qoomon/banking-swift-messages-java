package com.qoomon.banking.swift.message.block;

import com.google.common.base.Preconditions;
import com.qoomon.banking.swift.message.block.exception.BlockFieldParseException;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextBlock implements SwiftBlock {

    public static final String BLOCK_ID_4 = "4";

    public static final Pattern FIELD_PATTERN = Pattern.compile("([^\\n]+)?\\n((?>:?.*\\n)*+-)");

    private final Optional<String> infoLine;

    private final String text;


    public TextBlock(String infoLine, String text) {
        Preconditions.checkArgument(text != null, "content can't be null");

        this.infoLine = Optional.ofNullable(infoLine);
        this.text = text;
    }

    public static TextBlock of(GeneralBlock block) throws BlockFieldParseException {
        Preconditions.checkArgument(block.getId().equals(BLOCK_ID_4), "unexpected block id '{}'", block.getId());

        Matcher blockMatcher = FIELD_PATTERN.matcher(block.getContent());
        if (!blockMatcher.matches()) {
            throw new BlockFieldParseException("Block " + BLOCK_ID_4 + " did not match pattern " + FIELD_PATTERN);
        }
        // remove first empty line
        String infoLine = blockMatcher.group(1);
        String text = blockMatcher.group(2);

        return new TextBlock(infoLine, text);
    }

    public Optional<String> getInfoLine() {
        return infoLine;
    }

    @Override
    public String getId() {
        return BLOCK_ID_4;
    }

    public String getText() {
        return text;
    }

    @Override
    public String getContent() {
        StringBuilder contentBuilder = new StringBuilder();
        if (infoLine.isPresent()) {
            contentBuilder.append(infoLine.get());
        }
        contentBuilder.append("\n").append(text);
        return contentBuilder.toString();
    }

}
