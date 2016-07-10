package com.qoomon.banking.swift.message;

import com.google.common.base.Preconditions;
import com.qoomon.banking.swift.message.block.*;

import java.util.Optional;

/**
 * Created by qoomon on 24/06/16.
 * <pre>
 * <div class="post-bodycopy cf">
 * <p>If you’re looking at a <a onclick="javascript:pageTracker._trackPageview('/outgoing/www.sepaforcorporates.com/swift-for-corporates/swift-message-types-know-mts-mxs/');" title="SWIFT Message Types – Know Your MTs from your MXs…" href="http://www.sepaforcorporates.com/swift-for-corporates/swift-message-types-know-mts-mxs/" target="_blank">SWIFT message</a> for the first time, it can be a bit daunting. To the untrained eye the whole <a onclick="javascript:pageTracker._trackPageview('/outgoing/www.sepaforcorporates.com/swift-for-corporates/swift-message-types-know-mts-mxs/');" title="SWIFT Message Types – Know Your MTs from your MXs…" href="http://www.sepaforcorporates.com/swift-for-corporates/swift-message-types-know-mts-mxs/" target="_blank">SWIFT message</a> structure can look like gobbledygook. But actually, there is a bit of a method to the madness. Whether you are receiving, processing or constructing an MT101 or an MT940 message it is important to what you’re dealing with, and what needs to go where. Let me explain…..</p>
 * <h2>SWIFT Message Structure</h2>
 * <p>A SWIFT MT message consists of the following blocks or segments:</p>
 * <ul>
 * <li><i></i><span style="color: #119980;" data-mce-mark="1">{1:} Basic Header Block</span></li>
 * <li><i></i><span style="color: #119980;" data-mce-mark="1">{2:} Application Header Block</span></li>
 * <li><i></i><span style="color: #119980;" data-mce-mark="1">{3:} User Header Block</span></li>
 * <li><i></i><span style="color: #119980;" data-mce-mark="1">{4:} Text Block</span></li>
 * <li><i></i><span style="color: #119980;" data-mce-mark="1">{5:} Trailer Block</span></li>
 * </ul>
 * <p>To keep it very simple I’ve not included any data at this point – but to highlight&nbsp;the SWIFT message structure will appear something like the following:</p>
 * <p><span style="color: #119980;" data-mce-mark="1">{1:}{2:}{3:}{4:</span></p>
 * <p><span style="color: #119980;" data-mce-mark="1">-}</span></p>
 * <p><span style="color: #119980;" data-mce-mark="1">{5:}</span></p>
 * <h3>SWIFT Message Structure: Basic Header Block</h3>
 * <p>This will provide information on the contents of the Basic Header Block – the bit that starts {1:</p>
 * <p>It will typically consist of something like: <span style="color: #119980;" data-mce-mark="1">{1:F01YOURCODEZABC1234567890}</span> where:</p>
 * <ul>
 * <li><i></i><span style="color: #119980;" data-mce-mark="1">{1:</span> – Identifies the Block – i.e. the Basic&nbsp;Header Block</li>
 * <li><i></i><span style="color: #119980;" data-mce-mark="1">F</span> – Indicates the Application Id – in this case, FIN</li>
 * <li><i></i><span style="color: #119980;" data-mce-mark="1">01</span> – Indicates the Service Id
 * <ul>
 * <li><i></i>01 = FIN</li>
 * <li><i></i>21 = Acknowledgement (ACK) or Negative Acknowledgement (NAK)</li>
 * </ul>
 * </li>
 * <li><i></i><span style="color: #119980;" data-mce-mark="1">YOURCODEZABC</span> – The Logical Terminal Address – which is typically your BIC 8 (YOURCODE)&nbsp;+ Logical Terminal Code (Z) + Branch Code
 * <ul>
 * <li><i></i>I know YOURCODE is an invalid BIC – but lets go with it…</li>
 * </ul>
 * </li>
 * <li><i></i><span style="color: #119980;" data-mce-mark="1">1234</span> – Session Number – Ask SWIFT or your Service Bureau how they want you to populate this – this is not very interesting for corporates</li>
 * <li><i></i><span style="color: #119980;" data-mce-mark="1">567890</span> – Sequence Number – As above, ask your SWIFT people how they want this populated</li>
 * <li><i></i><span style="color: #119980;" data-mce-mark="1">}</span> – Indicated the end of the Basic Header Block</li>
 * </ul>
 * <h3>SWIFT Message Structure: Application Header Block</h3>
 * <p>The Application Header Block will always starts {2:</p>
 * <p>And will look something like: <span style="color: #119980;" data-mce-mark="1">{2:I101YOURBANKXJKLU3003}</span> where:</p>
 * <ul>
 * <li><i></i><span style="color: #119980;" data-mce-mark="1">{2:</span> – Indicates the start of the Application Header block</li>
 * <li><i></i><span style="color: #119980;" data-mce-mark="1">I</span> – Informs you that you’re in Input mode (i.e. the Sender), O would indicate Output mode – so you would be the recipient of the message</li>
 * <li><i></i><span style="color: #119980;" data-mce-mark="1">101</span> – Message type – in this case, an MT101</li>
 * <li><i></i><span style="color: #119980;" data-mce-mark="1">YOURBANKXJKL</span> – The recipients BIC, consisting of their BIC (YOURBANK) + Recipients Logical Terminal Code (X) + Recipients Branch Code (JKL)</li>
 * <li><i></i><span style="color: #119980;" data-mce-mark="1">U</span> – Message Priority:
 * <ul>
 * <li><i></i>U – Urgent</li>
 * <li><i></i>N – Normal</li>
 * <li><i></i>S – System</li>
 * </ul>
 * </li>
 * <li><i></i><span style="color: #119980;" data-mce-mark="1">3</span> – Delivery Monitoring – Ask your SWIFT contacts or Service Bureau how you should populate this, if at all&nbsp;- Optional</li>
 * <li><i></i><span style="color: #119980;" data-mce-mark="1">003</span> – Non-delivery notification period – again, ask your SWIFT contacts how to populate this, if at all – Optional</li>
 * <li><i></i><span style="color: #119980;" data-mce-mark="1">}</span> – Indicated the end of the Application Header Block</li>
 * </ul>
 * <h3>SWIFT Message Structure: User Header Block</h3>
 * <p>The User Header Block will always starts {3:</p>
 * <p>And will look something like: <span style="color: #119980;" data-mce-mark="1">{3:{108:ILOVESEPA}}</span> where:</p>
 * <ul>
 * <li><i></i><span style="color: #119980;" data-mce-mark="1">{3:</span> – Indicates the start of the User Header Block</li>
 * <li><i></i>You can add a optional bank priority code, in this example I have not added it…</li>
 * <li><i></i><span style="color: #119980;" data-mce-mark="1">{108:ILOVESEPA}</span> – Indicates the Message User Reference (MUR) value, which can be up to 16 characters, and will be returned in the ACK</li>
 * <li><i></i><span style="color: #119980;" data-mce-mark="1">}</span> – Indicated the end of the User Header Block</li>
 * </ul>
 * <h3>SWIFT Message Structure: Text Block</h3>
 * <p>The Text Block will always starts <span style="color: #119980;" data-mce-mark="1">{4:</span></p>
 * <p>And will look something like: <span style="color: #119980;" data-mce-mark="1">{4:</span></p>
 * <p>Followed by the details of the message you’re sending. In this case, it is a MT101 – as indicated in Application Header Block message type.</p>
 * <p>Finally ending with <span style="color: #119980;" data-mce-mark="1">-}</span></p>
 * <h3>SWIFT Message Structure: Trailer Block</h3>
 * <p>The Trailer Block will always starts <span style="color: #119980;" data-mce-mark="1">{5:</span></p>
 * <p>This can be added by you or the system. Work with your SWIFT contacts to know if you need to indicate this block.</p>
 * <p>And ends with <span style="color: #119980;" data-mce-mark="1">}</span></p>
 * <p>&nbsp;</p>
 * <span id="last"></span><div id="slidebox" style="right: -430px;"><a class="close"></a>Kindly&nbsp;TWEET or share this post via LinkedIn- Thank You…!!</div>
 * <p>&nbsp;</p>
 * <p><span style="text-decoration: underline;" data-mce-mark="1">Sources:</span></p>
 * <ul>
 * <li><i></i>IBM <a onclick="javascript:pageTracker._trackPageview('/outgoing/www-01.ibm.com/support/knowledgecenter/SSBTEG_4.3.0/com.ibm.wbia_adapters.doc/doc/swift/swift72.htm%23HDRI1023624');" title="IBM - SWIFT Message Block Structure" href="http://www-01.ibm.com/support/knowledgecenter/SSBTEG_4.3.0/com.ibm.wbia_adapters.doc/doc/swift/swift72.htm%23HDRI1023624" target="_blank">SWIFT Message Block Structure</a></li>
 * <li><i></i><a onclick="javascript:pageTracker._trackPageview('/outgoing/coding.pstodulka.com/2015/01/10/anatomy-of-a-swift-message/');" title="Anatomy of a SWIFT Message" href="http://coding.pstodulka.com/2015/01/10/anatomy-of-a-swift-message/" target="_blank">Anatomy of a SWIFT Message</a></li>
 * <li><i></i><a onclick="javascript:pageTracker._trackPageview('/outgoing/vermaparas.wordpress.com/');" title="SWIFT by Paras" href="https://vermaparas.wordpress.com/" target="_blank">SWIFT</a> by Paras</li>
 * </ul>
 * </div>
 * </pre>
 *
 * @see <a href="http://www.sepaforcorporates.com/swift-for-corporates/read-swift-message-structure/">http://www.sepaforcorporates.com/swift-for-corporates/read-swift-message-structure/</a>
 */
public class SwiftOutputMessage {

    /**
     * {1:} Basic Header Block
     */
    private final BasicHeaderBlock basicHeaderBlock;

    /**
     * {2:} Application Header Block
     */
    private final ApplicationHeaderOutputBlock applicationHeaderBlock;

    /**
     * {3:} User Header Block
     */
    private final UserHeaderBlock userHeaderBlock;

    /**
     * {4:} Text Block
     */
    private final TextBlock textBlock;

    /**
     * {5:} User Trailer Block
     */
    private final Optional<UserTrailerBlock> userTrailerBlock;

    /**
     * {S:} System Trailer Block
     */
    private final Optional<SystemTrailerBlock> systemTrailerBlock;


    public SwiftOutputMessage(BasicHeaderBlock basicHeaderBlock,
                              ApplicationHeaderOutputBlock applicationHeaderBlock,
                              UserHeaderBlock userHeaderBlock,
                              TextBlock textBlock,
                              UserTrailerBlock userTrailerBlock,
                              SystemTrailerBlock systemTrailerBlock) {
        this.basicHeaderBlock = Preconditions.checkNotNull(basicHeaderBlock);
        this.applicationHeaderBlock = Preconditions.checkNotNull(applicationHeaderBlock);
        this.userHeaderBlock = Preconditions.checkNotNull(userHeaderBlock);
        this.textBlock = Preconditions.checkNotNull(textBlock);
        this.userTrailerBlock = Optional.ofNullable(userTrailerBlock);
        this.systemTrailerBlock = Optional.ofNullable(systemTrailerBlock);
    }

    public BasicHeaderBlock getBasicHeaderBlock() {
        return basicHeaderBlock;
    }

    public ApplicationHeaderOutputBlock getApplicationHeaderBlock() {
        return applicationHeaderBlock;
    }

    public UserHeaderBlock getUserHeaderBlock() {
        return userHeaderBlock;
    }

    public TextBlock getTextBlock() {
        return textBlock;
    }

    public Optional<UserTrailerBlock> getUserTrailerBlock() {
        return userTrailerBlock;
    }

    public Optional<SystemTrailerBlock> getSystemTrailerBlock() {
        return systemTrailerBlock;
    }
}
