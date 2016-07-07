package com.qoomon.banking.swift.message.block;

import com.google.common.base.Preconditions;

/**
 * Created by qoomon on 07/07/16.
 */
public class GeneralBlock {

    private final String id;
    private final String content;

    public GeneralBlock(String id, String content) {
        this.id = Preconditions.checkNotNull(id);;
        this.content = Preconditions.checkNotNull(content);
    }

    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }
}
