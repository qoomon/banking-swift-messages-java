package com.qoomon.banking.swift.message.block;


/**
 * Created by qoomon on 26/08/16.
 */
public final class BlockUtils {

    /**
     * Convert to Swift Text Format.
     *
     * @param block to convert
     * @return swift text
     */
    public static String swiftTextOf(SwiftBlock block) {
        return swiftTextOf(block.getId(), block.getContent());
    }

    public static String swiftTextOf(String id, String content) {
        return "{" + id + ":" + content + "}";
    }
}
