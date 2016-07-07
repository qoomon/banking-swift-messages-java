package com.qoomon.banking.swift.message.block;

import com.google.common.base.Preconditions;

/**
 * Created by qoomon on 07/07/16.
 */
public class ApplicationHeaderBlock {

    public static final String BLOCK_ID_2 = "2";



    public static ApplicationHeaderBlock of(GeneralBlock block) {
        Preconditions.checkArgument(block.getId().equals(BLOCK_ID_2), "unexpected block id '" + block.getId() + "'");

        return new ApplicationHeaderBlock();
    }


}
