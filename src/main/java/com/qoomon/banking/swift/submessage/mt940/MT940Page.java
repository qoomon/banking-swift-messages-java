package com.qoomon.banking.swift.submessage.mt940;

import com.google.common.base.Preconditions;
import com.qoomon.banking.swift.submessage.field.*;

import java.util.List;
import java.util.Optional;

/**
 * Created by qoomon on 24/06/16.
 * <p>It is fair to say that most <a   href="http://www.sepaforcorporates.com/sepa-implementation/a-must-visit-website-for-any-sepa-implementation/" target="_blank">SEPA implementations </a>are focusing on SEPA compliance, and rightly so! This focus will ensure that corporate <a   href="http://www.sepaforcorporates.com/sepa-payments/sepa-credit-transfer-a-quick-overview/" target="_blank">payments </a>and <a   href="http://www.sepaforcorporates.com/sepa-direct-debits/sepa-direct-debit-a-quick-overview/" target="_blank">direct debit collections </a>can continue to happen uninterrupted after the <a   href="http://www.sepaforcorporates.com/sepa-implementation/sepa-deadline-by-country-a-must-read/" target="_blank">SEPA deadline</a>. The spotlight for now is very much on the corporate to bank space. Less attention is being given the other way, i.e. bank to corporate. As a bare minimum, I would recommend you to have an understanding of the <a   href="http://www.sepaforcorporates.com/sepa-implementation/reason-codes-r-transactions-r-messages/" target="_blank">SEPA Rejection Reason Codes</a>, <a   href="http://www.sepaforcorporates.com/sepa-implementation/reason-codes-r-transactions-r-messages/" target="_blank">R-Transactions </a>or <a   href="http://www.sepaforcorporates.com/sepa-implementation/reason-codes-r-transactions-r-messages/" target="_blank">R-Messages</a>.</p>
 * <p>The other significant area in the bank to corporate space is bank statement reporting.&nbsp;With SEPA you may be asked questions about the content of your bank statement file. I am going to assume that the vast majority of corporates are using the MT940&nbsp;format for end of day bank statement reporting.</p>
 * <p>The intention of this post if to&nbsp;provide you with an overview of the MT940 file format. It should help you to identify in which field a particular value has or has not been sent!</p>
 * <h3>What is an MT940?</h3>
 * <p>An MT940 is a standard structured&nbsp;SWIFT Customer Statement message. In short, it is an electronic bank account statement which has been developed by SWIFT. It is a end of day statement&nbsp;file which details all entries booked to an account.</p>
 * <h4>MT940 Format Details:</h4>
 * <p>The MT940 file format consists of the following sections and tags:</p>
 * <h5>Tag 20 – TransactionGroup Reference Number</h5>
 * <ul>
 * <li>Mandatory – 16x</li>
 * <li>Used by the Sender to unambiguously identify the message</li>
 * </ul>
 * <h5>Tag 21 – Related Reference</h5>
 * <ul>
 * <li>Optional – 16x</li>
 * <li>If the MT&nbsp;940 is sent in response to an MT&nbsp;920 Request Message, this field must contain the field 20 TransactionGroup Reference Number of the request message</li>
 * </ul>
 * <h5>Tag 25 – Account Identification</h5>
 * <ul>
 * <li>Mandatory – 35x</li>
 * <li>This field identifies the account for which the statement is sent</li>
 * </ul>
 * <h5>Tag 28C – Statement Number/Sequence Number</h5>
 * <ul>
 * <li>Mandatory – 5n[/5n]</li>
 * <li>Sequential number of the statement, optionally followed by the sequence number of the message within that statement when more than one message is sent for one statement
 * <ul>
 * <li>For example – the first message within the statement would be represented as 28C:111/1, the second message within the same file would be 28C:111/2</li>
 * </ul>
 * </li>
 * </ul>
 * <h5>Tag 60a – Opening Balance</h5>
 * <ul>
 * <li>Mandatory – 1!a6!n3!a15d – D/C | Date | Currency | Amount</li>
 * <li>Indicating for the (intermediate) opening balance, whether it is a debit or credit balance, the date, the currency and the amount of the balance</li>
 * <li>There are 2 options:
 * <ul>
 * <li>Option F – :60F:
 * <ul>
 * <li>Debit/Credit | Last Statement Date (YYMMDD)&nbsp;&nbsp;| Currency (ISO)&nbsp;| Amount</li>
 * </ul>
 * </li>
 * <li>Option M – :60M:
 * <ul>
 * <li>Debit/Credit |&nbsp;Current Statement Date (YYMMDD)&nbsp;&nbsp;| Currency (ISO)&nbsp;| Amount</li>
 * </ul>
 * </li>
 * </ul>
 * </li>
 * </ul>
 * <p>&nbsp;</p>
 * <h5>Tag 61 – Statement Line</h5>
 * <ul>
 * <li>Optional – 6!n[4!n]2a[1!a]15d1!a3!c16x[//16x]<br>
 * [34x]
 * <ul>
 * <li>6!n Value Date (YYMMDD)</li>
 * <li>[4!n] Entry Date (MMDD)</li>
 * <li>2a Debit/Credit Mark</li>
 * <li>[1!a] Funds Code (3rd character of the currency code, if needed)</li>
 * <li>15d Amount</li>
 * <li>1!a3!c TransactionGroup Type Identification Code</li>
 * <li>16x Customer Reference</li>
 * <li>[//16x] Bank Reference</li>
 * <li>[34x] Supplementary Details (this will be on a new/separate line)</li>
 * </ul>
 * </li>
 * </ul>
 * <h5>Tag 86 – Information to Account Owner</h5>
 * <ul>
 * <li>Optional – 6x65x</li>
 * <li>Additional information about the transaction detailed in the preceding statement line and which is to be passed on to the account owner</li>
 * </ul>
 * <p>&nbsp;</p>
 * <h5>Tag 62a – Closing Balance (Booked Funds)</h5>
 * <ul>
 * <li>Mandatory – 1!a6!n3!a15d – D/C | Date | Currency | Amount</li>
 * <li>Indicating for the (intermediate) closing balance, whether it is a debit or credit balance, the date, the currency and the amount of the balance</li>
 * <li>There are 2 options:
 * <ul>
 * <li>Option F – :60F:
 * <ul>
 * <li>Debit/Credit | Last Statement Date (YYMMDD)&nbsp;&nbsp;| Currency (ISO)&nbsp;| Amount</li>
 * </ul>
 * </li>
 * <li>Option M – :60M:
 * <ul>
 * <li>Debit/Credit |&nbsp;Current Statement Date (YYMMDD)&nbsp;&nbsp;| Currency (ISO)&nbsp;| Amount</li>
 * </ul>
 * </li>
 * </ul>
 * </li>
 * </ul>
 * <h5>Tag 64 – Closing Available Balance (Available Funds)</h5>
 * <ul>
 * <li>Optional – 1!a6!n3!a15d – D/C | Date | Currency | Amount</li>
 * <li>Indicates&nbsp;the funds which are available to the account owner (if credit balance) or the balance which is subject to interest charges (if debit balance).</li>
 * <li>Debit Balance /Credit Balance |&nbsp; Date (YYMMDD)&nbsp;&nbsp;| Currency (ISO)&nbsp;| Amount</li>
 * </ul>
 * <p>&nbsp;</p>
 * <h5>Tag 65 – Forward Available Balance</h5>
 * <ul>
 * <li>Optional – 1!a6!n3!a15d – - D/C | Date | Currency | Amount</li>
 * <li>Indicates the funds which are available to the account owner (if a credit or debit balance) for the specified forward value date</li>
 * <li>Debit Balance /Credit Balance |&nbsp; Date (YYMMDD)&nbsp;&nbsp;| Currency (ISO)&nbsp;| Amount</li>
 * </ul>
 * <p>&nbsp;</p>
 * <h5>Tag 86 – Information to Account Owner</h5>
 * <ul>
 * <li>Optional – 6x65x</li>
 * <li>Additional information about the statement as a whole</li>
 * </ul>
 * <div  style="right: -430px;"><a ></a>Thanks for stopping by – Take a look around!</div>
 * <p>I’ve deliberately missed out the SWIFT header and footer sections, since here we’re mostly interested in the MT940 format and&nbsp;data contents.</p>
 * <p>Having quick access to this t is an MT940?has certainly helped me recently, and I hope it helps you too!</p>
 * <p>&nbsp;<br>
 *
 * @see <a href="http://www.sepaforcorporates.com/swift-for-corporates/account-statement-mt940-file-format-overview/">http://www.sepaforcorporates.com/swift-for-corporates/account-statement-mt940-file-format-overview/</a>
 */
public class MT940Page {

    public static final String MESSAGE_ID_940 = "940";

    /**
     * @see TransactionReferenceNumber#FIELD_TAG_20
     */
    private final TransactionReferenceNumber transactionReferenceNumber;

    /**
     * @see RelatedReference#FIELD_TAG_21
     */
    private final Optional<RelatedReference> relatedReference;

    /**
     * @see AccountIdentification#FIELD_TAG_25
     */
    private final AccountIdentification accountIdentification;

    /**
     * @see StatementNumber#FIELD_TAG_28C
     */
    private final StatementNumber statementNumber;

    /**
     * @see OpeningBalance#FIELD_TAG_60F
     * @see OpeningBalance#FIELD_TAG_60M
     */
    private final OpeningBalance openingBalance;

    /**
     * @see StatementLine#FIELD_TAG_61
     * @see InformationToAccountOwner#FIELD_TAG_86
     */
    private final List<TransactionGroup> transactionGroupList;

    /**
     * @see ClosingBalance#FIELD_TAG_62F
     * @see ClosingBalance#FIELD_TAG_62M
     */
    private final ClosingBalance closingBalance;

    /**
     * @see ClosingAvailableBalance#FIELD_TAG_64
     */
    private final Optional<ClosingAvailableBalance> closingAvailableBalance;

    /**
     * @see ForwardAvailableBalance#FIELD_TAG_65
     */
    private final List<ForwardAvailableBalance> forwardAvailableBalanceList;

    /**
     * @see InformationToAccountOwner#FIELD_TAG_86
     */
    private final Optional<InformationToAccountOwner> informationToAccountOwner;

    public MT940Page(
            TransactionReferenceNumber transactionReferenceNumber,
            RelatedReference relatedReference,
            AccountIdentification accountIdentification,
            StatementNumber statementNumber,
            OpeningBalance openingBalance,
            List<TransactionGroup> transactionGroupList,
            ClosingBalance closingBalance,
            ClosingAvailableBalance closingAvailableBalance,
            List<ForwardAvailableBalance> forwardAvailableBalanceList,
            InformationToAccountOwner informationToAccountOwner) {

        Preconditions.checkArgument(accountIdentification != null, "accountIdentification can't be null");
        Preconditions.checkArgument(statementNumber != null, "statementNumber can't be null");
        Preconditions.checkArgument(openingBalance != null, "openingBalance can't be null");
        Preconditions.checkArgument(transactionGroupList != null, "transactionGroupList can't be null");
        Preconditions.checkArgument(closingBalance != null, "closingBalance can't be null");
        Preconditions.checkArgument(forwardAvailableBalanceList != null, "forwardAvailableBalanceList can't be null");

        this.transactionReferenceNumber = transactionReferenceNumber;
        this.relatedReference = Optional.ofNullable(relatedReference);
        this.accountIdentification = accountIdentification;
        this.statementNumber = statementNumber;
        this.openingBalance = openingBalance;
        this.transactionGroupList = transactionGroupList;
        this.closingBalance = closingBalance;
        this.closingAvailableBalance = Optional.ofNullable(closingAvailableBalance);
        this.forwardAvailableBalanceList = forwardAvailableBalanceList;
        this.informationToAccountOwner = Optional.ofNullable(informationToAccountOwner);
    }

    public TransactionReferenceNumber getTransactionReferenceNumber() {
        return transactionReferenceNumber;
    }

    public Optional<RelatedReference> getRelatedReference() {
        return relatedReference;
    }

    public AccountIdentification getAccountIdentification() {
        return accountIdentification;
    }

    public StatementNumber getStatementNumber() {
        return statementNumber;
    }

    public OpeningBalance getOpeningBalance() {
        return openingBalance;
    }

    public List<TransactionGroup> getTransactionGroupList() {
        return transactionGroupList;
    }

    public ClosingBalance getClosingBalance() {
        return closingBalance;
    }

    public Optional<ClosingAvailableBalance> getClosingAvailableBalance() {
        return closingAvailableBalance;
    }

    public List<ForwardAvailableBalance> getForwardAvailableBalanceList() {
        return forwardAvailableBalanceList;
    }

    public Optional<InformationToAccountOwner> getInformationToAccountOwner() {
        return informationToAccountOwner;
    }
}
