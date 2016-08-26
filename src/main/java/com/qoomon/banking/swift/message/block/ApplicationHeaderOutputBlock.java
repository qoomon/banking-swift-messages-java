package com.qoomon.banking.swift.message.block;

import com.google.common.base.Preconditions;
import com.qoomon.banking.swift.message.block.exception.BlockFieldParseException;
import com.qoomon.banking.swift.submessage.field.subfield.MessagePriority;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <b>Output Application Header Block</b>
 * <p>
 * <b>Fixed Length Format</b>
 * <pre>
 *  1:  1  - Mode - O = Output
 *  2:  3  - Message Type MTxxx e.g. 940
 *  3:  4  - Input time with respect to the sender
 *  4:  6  - Input date with respect to the sender
 *  5: 12  - The Message Input Reference (MIR), with Sender's address
 *  6:  4  - Session number
 *  7:  6  — Sequence number
 *  8:  6  - Output date with respect to Receiver
 *  9:  4  - Output time with respect to Receiver
 * 10:  1  - Message Priority - U = Urgent, N = Normal, S = System
 * </pre>
 * <b>Example</b><br>
 * O1001200970103BANKBEBBAXXX22221234569701031201N
 *
 * @see <a href="https://www.ibm.com/support/knowledgecenter/SSBTEG_4.3.0/com.ibm.wbia_adapters.doc/doc/swift/swift72.htm">https://www.ibm.com/support/knowledgecenter/SSBTEG_4.3.0/com.ibm.wbia_adapters.doc/doc/swift/swift72.htm</a>
 */
public class ApplicationHeaderOutputBlock {

    public static final String MODE_CODE = "O";

    public static final Pattern BLOCK_CONTENT_PATTERN = Pattern.compile("(O)(.{3})(.{4})(.{6})(.{12})(.{4})(.{6})(.{6})(.{4})(.{1})");

    private static final DateTimeFormatter INPUT_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HHmmyyMMdd");

    private static final DateTimeFormatter OUTPUT_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyMMddHHmm");

    private final String sessionNumber;

    private final String sequenceNumber;

    private final String messageType;

    private final LocalDateTime inputDateTime;

    private final String inputReference;

    private final LocalDateTime outputDateTime;

    private final MessagePriority messagePriority;


    public ApplicationHeaderOutputBlock(String sessionNumber, String sequenceNumber, String messageType, LocalDateTime inputDateTime, String inputReference, LocalDateTime outputDateTime, MessagePriority messagePriority) {

        Preconditions.checkArgument(sessionNumber != null, "sessionNumber can't be null");
        Preconditions.checkArgument(sequenceNumber != null, "sequenceNumber can't be null");
        Preconditions.checkArgument(messageType != null, "messageType can't be null");
        Preconditions.checkArgument(inputDateTime != null, "inputDateTime can't be null");
        Preconditions.checkArgument(inputReference != null, "inputReference can't be null");
        Preconditions.checkArgument(outputDateTime != null, "outputDateTime can't be null");
        Preconditions.checkArgument(messagePriority != null, "messagePriority can't be null");

        this.sessionNumber = sessionNumber;
        this.sequenceNumber = sequenceNumber;
        this.messageType = messageType;
        this.inputDateTime = inputDateTime;
        this.inputReference = inputReference;
        this.outputDateTime = outputDateTime;
        this.messagePriority = messagePriority;
    }

    public static ApplicationHeaderOutputBlock of(GeneralBlock block) throws BlockFieldParseException {
        Preconditions.checkArgument(block.getId().equals(ApplicationHeaderBlock.BLOCK_ID_2), "unexpected block id '%s'", block.getId());

        Matcher blockContentMatcher = BLOCK_CONTENT_PATTERN.matcher(block.getContent());
        if (!blockContentMatcher.matches()) {
            throw new BlockFieldParseException("Block '" + block.getId() + "' content did not match format " + BLOCK_CONTENT_PATTERN);
        }

        String mode = blockContentMatcher.group(1);
        if (!mode.equals(MODE_CODE)) {
            throw new BlockFieldParseException("Block '" + block.getId() + "' expect mod '" + MODE_CODE + "', but was " + mode);
        }

        String messageType = blockContentMatcher.group(2);
        LocalDateTime inputDateTime = LocalDateTime.parse(blockContentMatcher.group(3) + blockContentMatcher.group(4), INPUT_DATE_TIME_FORMATTER);
        String inputReference = blockContentMatcher.group(5);
        String sessionNumber = blockContentMatcher.group(6);
        String sequenceNumber = blockContentMatcher.group(7);
        LocalDateTime outputDateTime = LocalDateTime.parse(blockContentMatcher.group(8) + blockContentMatcher.group(9), OUTPUT_DATE_TIME_FORMATTER);
        MessagePriority messagePriority = MessagePriority.of(blockContentMatcher.group(10));

        return new ApplicationHeaderOutputBlock(sessionNumber, sequenceNumber, messageType, inputDateTime, inputReference, outputDateTime, messagePriority);
    }


    public String getMessageType() {
        return messageType;
    }

    public LocalDateTime getInputDateTime() {
        return inputDateTime;
    }

    public String getInputReference() {
        return inputReference;
    }

    public LocalDateTime getOutputDateTime() {
        return outputDateTime;
    }

    public MessagePriority getMessagePriority() {
        return messagePriority;
    }

    public String getSessionNumber() {
        return sessionNumber;
    }

    public String getSequenceNumber() {
        return sequenceNumber;
    }

    public String getContent() {
        StringBuilder contentBuilder = new StringBuilder();
        contentBuilder.append(MODE_CODE);
        contentBuilder.append(messageType);
        contentBuilder.append(INPUT_DATE_TIME_FORMATTER.format(inputDateTime));
        contentBuilder.append(inputReference);
        contentBuilder.append(sessionNumber);
        contentBuilder.append(sequenceNumber);
        contentBuilder.append(OUTPUT_DATE_TIME_FORMATTER.format(outputDateTime));
        contentBuilder.append(messagePriority.asText());
        return contentBuilder.toString();
    }
}
