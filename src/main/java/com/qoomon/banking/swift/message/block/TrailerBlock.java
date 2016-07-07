package com.qoomon.banking.swift.message.block;

import com.google.common.base.Preconditions;

/**
 * Created by qoomon on 07/07/16.
 */
public class TrailerBlock {

    public static final String BLOCK_ID_5 = "5";

    public static TrailerBlock of(GeneralBlock block) {
        Preconditions.checkArgument(block.getId().equals(BLOCK_ID_5), "unexpected block id '" + block.getId() + "'");

        return new TrailerBlock();
    }
}
