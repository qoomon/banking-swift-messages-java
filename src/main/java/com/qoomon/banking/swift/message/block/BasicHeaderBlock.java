package com.qoomon.banking.swift.message.block;

import com.google.common.base.Preconditions;

/**
 * Created by qoomon on 07/07/16.
 */
public class BasicHeaderBlock {

    public static final String BLOCK_ID_1 = "1";

    public static BasicHeaderBlock of(GeneralBlock block) {
        Preconditions.checkArgument(block.getId().equals(BLOCK_ID_1), "unexpected block id '" + block.getId() + "'");

        return new BasicHeaderBlock();
    }
}
