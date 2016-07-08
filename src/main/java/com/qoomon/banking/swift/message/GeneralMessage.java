package com.qoomon.banking.swift.message;

import com.qoomon.banking.swift.message.block.*;

public class GeneralMessage {

    /**
     * {1:} Basic Header Block
     */
    private final BasicHeaderBlock basicHeaderBlock;

    /**
     * {2:} Application Header Block
     */
    private final GeneralBlock applicationHeaderBlock;

    /**
     * {3:} User Header Block
     */
    private final UserHeaderBlock userHeaderBlock;

    /**
     * {4:} Text Block
     */
    private final TextBlock textBlock;

    /**
     * {5:} Trailer Block
     */
    private final TrailerBlock trailerBlock;

    public GeneralMessage(BasicHeaderBlock basicHeaderBlock, GeneralBlock applicationHeaderBlock, UserHeaderBlock userHeaderBlock, TextBlock textBlock, TrailerBlock trailerBlock) {
        this.basicHeaderBlock = basicHeaderBlock;
        this.applicationHeaderBlock = applicationHeaderBlock;
        this.userHeaderBlock = userHeaderBlock;
        this.textBlock = textBlock;
        this.trailerBlock = trailerBlock;
    }

    public BasicHeaderBlock getBasicHeaderBlock() {
        return basicHeaderBlock;
    }

    public GeneralBlock getApplicationHeaderBlock() {
        return applicationHeaderBlock;
    }

    public UserHeaderBlock getUserHeaderBlock() {
        return userHeaderBlock;
    }

    public TextBlock getTextBlock() {
        return textBlock;
    }

    public TrailerBlock getTrailerBlock() {
        return trailerBlock;
    }
}
