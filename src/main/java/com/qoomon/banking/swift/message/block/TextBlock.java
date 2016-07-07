package com.qoomon.banking.swift.message.block;

import com.google.common.base.Preconditions;

public class TextBlock {

    public static final String BLOCK_ID_4 = "4";

    private final String content;

    public TextBlock(String content) {

        this.content = content;
    }

    public String getContent() {
        return content;
    }


    public static TextBlock of(GeneralBlock block) {
        Preconditions.checkArgument(block.getId().equals(BLOCK_ID_4), "unexpected block id '" + block.getId() + "'");

        // remove trailing '-'
        String blockContent = block.getContent().replaceFirst("-$", "");
        return new TextBlock(blockContent);
    }
}
