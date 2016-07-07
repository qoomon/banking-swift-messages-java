package com.qoomon.banking.swift.message.block;

import com.google.common.base.Preconditions;

/**
 * Created by qoomon on 07/07/16.
 */
public class UserHeaderBlock {

    public static final String BLOCK_ID_3 = "3";

    public static UserHeaderBlock of(GeneralBlock block) {
        Preconditions.checkArgument(block.getId().equals(BLOCK_ID_3), "unexpected block id '" + block.getId() + "'");

        return new UserHeaderBlock();
    }
}
