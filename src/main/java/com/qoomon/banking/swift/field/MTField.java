package com.qoomon.banking.swift.field;

/**
 * Created by qoomon on 27/06/16.
 */
public class MTField {

    private final String tag;
    private final String content;

    public MTField(String tag, String content) {
        this.tag = tag;
        this.content = content;
    }

    public String getTag() {
        return tag;
    }

    public String getContent() {
        return content;
    }
}
