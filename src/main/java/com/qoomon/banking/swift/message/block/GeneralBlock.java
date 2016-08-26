package com.qoomon.banking.swift.message.block;

import com.google.common.base.Preconditions;

/**
 * Created by qoomon on 07/07/16.
 */
public class GeneralBlock implements SwiftBlock {

    private final String id;
    private final String content;

    public GeneralBlock(String id, String content) {

        Preconditions.checkArgument(id != null && !id.isEmpty(), "id can't be null or empty");
        Preconditions.checkArgument(content != null, "content can't be null");

        this.id = id;
        this.content = content;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getContent() {
        return content;
    }
}
