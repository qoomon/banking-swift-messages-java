package com.qoomon.banking.swift.message.block;

import com.google.common.base.Preconditions;
import com.qoomon.banking.swift.message.block.exception.BlockFieldParseException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <b>System Trail Block</b> TODO
 */
public class SystemTrailerBlock {

    public static final String BLOCK_ID_S = "S";

    private static final Pattern BLOCK_CONTENT_PATTERN = Pattern.compile("(.*)");

    private final String content;

    public SystemTrailerBlock(String content) {
        Preconditions.checkArgument(content!= null, "content can't be null");
        this.content = content;
    }

    public static SystemTrailerBlock of(GeneralBlock block) throws BlockFieldParseException {
        Preconditions.checkArgument(block.getId().equals(BLOCK_ID_S), "unexpected block id '" + block.getId() + "'");

        Matcher blockContentMatcher = BLOCK_CONTENT_PATTERN.matcher(block.getContent());
        if (!blockContentMatcher.matches()) {
            throw new BlockFieldParseException("Block '" + block.getId() + "' content did not match format " + BLOCK_CONTENT_PATTERN);
        }

        String content = blockContentMatcher.group(1);

        return new SystemTrailerBlock(content);
    }

    public String getContent() {
        return content;
    }
}

