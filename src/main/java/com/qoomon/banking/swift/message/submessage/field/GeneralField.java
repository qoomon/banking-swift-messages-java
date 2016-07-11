package com.qoomon.banking.swift.message.submessage.field;

import com.google.common.base.Preconditions;

/**
 * Created by qoomon on 27/06/16.
 */
public class GeneralField implements SwiftField {

    private final String tag;
    private final String content;

    public GeneralField(String tag, String content) {

        Preconditions.checkArgument(tag != null && !tag.isEmpty(), "tag can't be null or empty");
        Preconditions.checkArgument(content != null, "content can't be null");

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

    public static Builder newBuilder(){
        return new Builder();
    }


    public static class Builder {

        String tag = null;

        StringBuilder contentBuilder = new StringBuilder();

        public GeneralField build() {
            return new GeneralField(tag, contentBuilder.toString());
        }

        public Builder setTag(String tag) {
            this.tag = tag;
            return this;
        }

        public Builder appendContent(String content) {
            this.contentBuilder.append(content);
            return this;
        }
    }

}
