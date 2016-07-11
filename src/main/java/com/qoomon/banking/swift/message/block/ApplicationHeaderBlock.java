package com.qoomon.banking.swift.message.block;

import com.google.common.base.Preconditions;
import com.qoomon.banking.swift.message.block.exception.BlockFieldParseException;

import java.util.Optional;

/**
 * <b>Input Application Header Block</b>
 * <p>
 *
 * @see ApplicationHeaderInputBlock
 * @see ApplicationHeaderOutputBlock
 */
public class ApplicationHeaderBlock {

    public static final String BLOCK_ID_2 = "2";

    private final Optional<ApplicationHeaderInputBlock> input;
    private final Optional<ApplicationHeaderOutputBlock> output;

    public ApplicationHeaderBlock(ApplicationHeaderInputBlock input) {
        this.input = Optional.of(input);
        this.output = Optional.empty();
    }

    public ApplicationHeaderBlock(ApplicationHeaderOutputBlock output) {
        this.input = Optional.empty();
        this.output = Optional.of(output);
    }

    public static ApplicationHeaderBlock of(GeneralBlock block) throws BlockFieldParseException {
        Preconditions.checkArgument(block.getId().equals(BLOCK_ID_2), "unexpected block id '%s'", block.getId());

        if (block.getContent().startsWith("I")) {
            ApplicationHeaderInputBlock input = ApplicationHeaderInputBlock.of(block);
            return new ApplicationHeaderBlock(input);
        }

        if (block.getContent().startsWith("O")) {
            ApplicationHeaderOutputBlock output = ApplicationHeaderOutputBlock.of(block);
            return new ApplicationHeaderBlock(output);
        }

        throw new BlockFieldParseException("Block '" + block.getId() + "' unknown I/O Type");

    }

    public Optional<ApplicationHeaderInputBlock> getInput() {
        return input;
    }

    public Optional<ApplicationHeaderOutputBlock> getOutput() {
        return output;
    }
}
