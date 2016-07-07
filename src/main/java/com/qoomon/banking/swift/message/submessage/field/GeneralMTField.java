package com.qoomon.banking.swift.message.submessage.field;

import com.google.common.base.Preconditions;

/**
 * Created by qoomon on 27/06/16.
 */
public class GeneralMTField implements SwiftMTField {

    private final String tag;
    private final String content;

    public GeneralMTField(String tag, String content) {
        Preconditions.checkNotNull(tag);
        Preconditions.checkArgument(!tag.isEmpty());
        Preconditions.checkNotNull(content);
        this.tag = tag;
        this.content = content;
    }

    @Override
    public String getTag() {
        return tag;
    }

    public String getContent() {
        return content;
    }

}
