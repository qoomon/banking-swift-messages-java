package com.qoomon.banking.swift.message.block;

import com.google.common.base.Preconditions;
import com.qoomon.banking.swift.message.block.exception.BlockFieldParseException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <b>Output Application Header Block</b>
 * <p>
 * <b>Fixed Length Format</b>
 * <pre>
 * 1:  1  - Mode - I = Input, O = Output
 * 2:  3  - Message Type MTxxx e.g. 940
 * 3:  4  - Input time with respect to the sender
 * 4: 28  - The Message Input Reference (MIR), including input date, with Sender's address
 * 5:  6  - Output date with respect to Receiver
 * 6:  4  - Output time with respect to Receiver
 * 7:  1  - Message Priority - U = Urgent, N = Normal, S = System
 * </pre>
 * <p>
 * <b>Example</b><br>
 * O1001200970103BANKBEBBAXXX22221234569701031201N
 *
 * @see <a href="https://www.ibm.com/support/knowledgecenter/SSBTEG_4.3.0/com.ibm.wbia_adapters.doc/doc/swift/swift72.htm">https://www.ibm.com/support/knowledgecenter/SSBTEG_4.3.0/com.ibm.wbia_adapters.doc/doc/swift/swift72.htm</a>
 */
public class OutputApplicationHeaderBlock {

    public static final String BLOCK_ID_2 = "2";

    public static final Pattern BLOCK_CONTENT_PATTERN = Pattern.compile("(O)(.{3})(.{4})(.{28})(.{6})(.{4})(.{1})");

    private final String mode;
    private final String messageType;
    private final String inputTime;
    private final String inputReference;
    private final String outputDate;
    private final String outputTime;
    private final String messagePriority;

    public OutputApplicationHeaderBlock(String mode, String messageType, String inputTime, String inputReference, String outputDate, String outputTime, String messagePriority) {
        this.mode = Preconditions.checkNotNull(mode);
        this.messageType = Preconditions.checkNotNull(messageType);
        this.inputTime = Preconditions.checkNotNull(inputTime);
        this.inputReference = Preconditions.checkNotNull(inputReference);
        this.outputDate = Preconditions.checkNotNull(outputDate);
        this.outputTime = Preconditions.checkNotNull(outputTime);
        this.messagePriority = Preconditions.checkNotNull(messagePriority);
    }

    public static OutputApplicationHeaderBlock of(GeneralBlock block) throws BlockFieldParseException {
        Preconditions.checkArgument(block.getId().equals(BLOCK_ID_2), "unexpected block id '" + block.getId() + "'");

        Matcher blockContentMatcher = BLOCK_CONTENT_PATTERN.matcher(block.getContent());
        if (!blockContentMatcher.matches()) {
            throw new BlockFieldParseException("Block '" + block.getId() + "' content did not match format " + BLOCK_CONTENT_PATTERN);
        }

        String mode = blockContentMatcher.group(1);
        String messageType = blockContentMatcher.group(2);
        String inputTime = blockContentMatcher.group(3);
        String inputReference = blockContentMatcher.group(4);
        String outputDate = blockContentMatcher.group(5);
        String outputTime = blockContentMatcher.group(6);
        String messagePriority = blockContentMatcher.group(7);

        return new OutputApplicationHeaderBlock(mode, messageType, inputTime, inputReference, outputDate, outputTime, messagePriority);
    }


}
