package com.qoomon.banking.swift.message.block;

import com.google.common.base.Preconditions;
import com.qoomon.banking.swift.message.block.exception.BlockParseException;

import java.util.regex.Pattern;

public class TextBlock {

    public static final String BLOCK_ID_4 = "4";

    public static final Pattern FIELD_PATTERN = Pattern.compile("\n(.*\n)*-", Pattern.DOTALL);

    private final String content;

    public TextBlock(String content) {

        this.content = content;
    }

    public static TextBlock of(GeneralBlock block) throws BlockParseException {
        Preconditions.checkArgument(block.getId().equals(BLOCK_ID_4), "unexpected block id '" + block.getId() + "'");

        String blockContent = block.getContent();

        if (!FIELD_PATTERN.matcher(blockContent).matches()) {
            throw new BlockParseException("Block " + BLOCK_ID_4 + " did not match pattern " + FIELD_PATTERN);
        }
        // remove first empty line
        blockContent = blockContent.replaceFirst("^\\n", "");

        return new TextBlock(blockContent);
    }

    public String getContent() {
        return content;
    }


}
