package com.qoomon.banking.swift.mt.mt942;

import com.google.common.base.Preconditions;
import com.qoomon.banking.swift.field.*;
import com.qoomon.banking.swift.group.TransactionGroup;

import java.util.List;
import java.util.Optional;

/**
 * TODO adjust from mt940 to mt492
 * Created by qoomon on 24/06/16.
 * <pre>
 * <div class="post-bodycopy cf">
 * <p>It is fair to say that most <a onclick="javascript:pageTracker._trackPageview('/outgoing/www.sepaforcorporates.com/sepa-implementation/a-must-visit-website-for-any-sepa-implementation/');" title="A Must Visit Website for any SEPA Implementation…" href="http://www.sepaforcorporates.com/sepa-implementation/a-must-visit-website-for-any-sepa-implementation/" target="_blank">SEPA implementations </a>are focusing on SEPA compliance, and rightly so! This focus will ensure that corporate <a onclick="javascript:pageTracker._trackPageview('/outgoing/www.sepaforcorporates.com/sepa-payments/sepa-credit-transfer-a-quick-overview/');" title="SEPA Credit Transfer – A Quick Overview" href="http://www.sepaforcorporates.com/sepa-payments/sepa-credit-transfer-a-quick-overview/" target="_blank">payments </a>and <a onclick="javascript:pageTracker._trackPageview('/outgoing/www.sepaforcorporates.com/sepa-direct-debits/sepa-direct-debit-a-quick-overview/');" title="SEPA Direct Debit – A Quick Overview" href="http://www.sepaforcorporates.com/sepa-direct-debits/sepa-direct-debit-a-quick-overview/" target="_blank">direct debit collections </a>can continue to happen uninterrupted after the <a onclick="javascript:pageTracker._trackPageview('/outgoing/www.sepaforcorporates.com/sepa-implementation/sepa-deadline-by-country-a-must-read/');" title="SEPA Deadline by Country – A Must Read" href="http://www.sepaforcorporates.com/sepa-implementation/sepa-deadline-by-country-a-must-read/" target="_blank">SEPA deadline</a>. The spotlight for now is very much on the corporate to bank space. Less attention is being given the other way, i.e. bank to corporate. As a bare minimum, I would recommend you to have an understanding of the <a onclick="javascript:pageTracker._trackPageview('/outgoing/www.sepaforcorporates.com/sepa-implementation/reason-codes-r-transactions-r-messages/');" title="Reason Codes, R-Transactions, R-Messages" href="http://www.sepaforcorporates.com/sepa-implementation/reason-codes-r-transactions-r-messages/" target="_blank">SEPA Rejection Reason Codes</a>, <a onclick="javascript:pageTracker._trackPageview('/outgoing/www.sepaforcorporates.com/sepa-implementation/reason-codes-r-transactions-r-messages/');" title="Reason Codes, R-Transactions, R-Messages" href="http://www.sepaforcorporates.com/sepa-implementation/reason-codes-r-transactions-r-messages/" target="_blank">R-Transactions </a>or <a onclick="javascript:pageTracker._trackPageview('/outgoing/www.sepaforcorporates.com/sepa-implementation/reason-codes-r-transactions-r-messages/');" title="Reason Codes, R-Transactions, R-Messages" href="http://www.sepaforcorporates.com/sepa-implementation/reason-codes-r-transactions-r-messages/" target="_blank">R-Messages</a>.</p>
 * <p>The other significant area in the bank to corporate space is bank statement reporting.&nbsp;With SEPA you may be asked questions about the content of your bank statement file. I am going to assume that the vast majority of corporates are using the MT940&nbsp;format for end of day bank statement reporting.</p>
 * <p>The intention of this post if to&nbsp;provide you with an overview of the MT940 file format. It should help you to identify in which field a particular value has or has not been sent!</p>
 * <p>
 * <h4><strong>What is an MT940?</strong></h4>
 * <p>An MT940 is a standard structured&nbsp;SWIFT Customer Statement message. In short, it is an electronic bank account statement which has been developed by SWIFT. It is a end of day statement&nbsp;file which details all entries booked to an account.</p>
 * <h4><strong>MT940 Format Details:</strong></h4>
 * <p>The MT940 file format consists of the following sections and tags:</p>
 * <h5>Tag 20 – TransactionGroup Reference Number</h5>
 * <ul>
 * <li><i></i>Mandatory – 16x</li>
 * <li><i></i>Used by the Sender to unambiguously identify the message</li>
 * </ul>
 * <h5>Tag 21 – Related Reference</h5>
 * <ul>
 * <li><i></i>Optional – 16x</li>
 * <li><i></i>If the MT&nbsp;940 is sent in response to an MT&nbsp;920 Request Message, this field must contain the field 20 TransactionGroup Reference Number of the request message</li>
 * </ul>
 * <h5>Tag 25 – Account Identification</h5>
 * <ul>
 * <li><i></i>Mandatory – 35x</li>
 * <li><i></i>This field identifies the account for which the statement is sent</li>
 * </ul>
 * <h5>Tag 28C – Statement Number/Sequence Number</h5>
 * <ul>
 * <li><i></i>Mandatory – 5n[/5n]</li>
 * <li><i></i>Sequential number of the statement, optionally followed by the sequence number of the message within that statement when more than one message is sent for one statement
 * <ul>
 * <li><i></i>For example – the first message within the statement would be represented as 28C:111/1, the second message within the same file would be 28C:111/2</li>
 * </ul>
 * </li>
 * </ul>
 * <h5>Tag 60a – Opening Balance</h5>
 * <ul>
 * <li><i></i>Mandatory – 1!a6!n3!a15d – D/C | Date | Currency | Amount</li>
 * <li><i></i>Indicating for the (intermediate) opening balance, whether it is a debit or credit balance, the date, the currency and the amount of the balance</li>
 * <li><i></i>There are 2 options:
 * <ul>
 * <li><i></i>Option F – :60F:
 * <ul>
 * <li><i></i>Debit/Credit | Last Statement Date (YYMMDD)&nbsp;&nbsp;| Currency (ISO)&nbsp;| Amount</li>
 * </ul>
 * </li>
 * <li><i></i>Option M – :60M:
 * <ul>
 * <li><i></i>Debit/Credit |&nbsp;Current Statement Date (YYMMDD)&nbsp;&nbsp;| Currency (ISO)&nbsp;| Amount</li>
 * </ul>
 * </li>
 * </ul>
 * </li>
 * </ul>
 * <p>&nbsp;</p>
 * <h5>Tag 61 – Statement Line</h5>
 * <ul>
 * <li><i></i>Optional – 6!n[4!n]2a[1!a]15d1!a3!c16x[//16x]<br>
 * [34x]<p></p>
 * <ul>
 * <li><i></i>6!n Value Date (YYMMDD)</li>
 * <li><i></i>[4!n] Entry Date (MMDD)</li>
 * <li><i></i>2a Debit/Credit Mark</li>
 * <li><i></i>[1!a] Funds Code (3rd character of the currency code, if needed)</li>
 * <li><i></i>15d Amount</li>
 * <li><i></i>1!a3!c TransactionGroup Type Identification Code</li>
 * <li><i></i>16x Customer Reference</li>
 * <li><i></i>[//16x] Bank Reference</li>
 * <li><i></i>[34x] Supplementary Details (this will be on a new/separate line)</li>
 * </ul>
 * </li>
 * </ul>
 * <h5>Tag 86 – Information to Account Owner</h5>
 * <ul>
 * <li><i></i>Optional – 6x65x</li>
 * <li><i></i>Additional information about the transaction detailed in the preceding statement line and which is to be passed on to the account owner</li>
 * </ul>
 * <p>&nbsp;</p>
 * <h5>Tag 62a – Closing Balance (Booked Funds)</h5>
 * <ul>
 * <li><i></i>Mandatory – 1!a6!n3!a15d – D/C | Date | Currency | Amount</li>
 * <li><i></i>Indicating for the (intermediate) closing balance, whether it is a debit or credit balance, the date, the currency and the amount of the balance</li>
 * <li><i></i>There are 2 options:
 * <ul>
 * <li><i></i>Option F – :60F:
 * <ul>
 * <li><i></i>Debit/Credit | Last Statement Date (YYMMDD)&nbsp;&nbsp;| Currency (ISO)&nbsp;| Amount</li>
 * </ul>
 * </li>
 * <li><i></i>Option M – :60M:
 * <ul>
 * <li><i></i>Debit/Credit |&nbsp;Current Statement Date (YYMMDD)&nbsp;&nbsp;| Currency (ISO)&nbsp;| Amount</li>
 * </ul>
 * </li>
 * </ul>
 * </li>
 * </ul>
 * <h5>Tag 64 – Closing Available Balance (Available Funds)</h5>
 * <ul>
 * <li><i></i>Optional – 1!a6!n3!a15d – D/C | Date | Currency | Amount</li>
 * <li><i></i>Indicates&nbsp;the funds which are available to the account owner (if credit balance) or the balance which is subject to interest charges (if debit balance).</li>
 * <li><i></i>Debit Balance /Credit Balance |&nbsp; Date (YYMMDD)&nbsp;&nbsp;| Currency (ISO)&nbsp;| Amount</li>
 * </ul>
 * <p>&nbsp;</p>
 * <h5>Tag 65 – Forward Available Balance</h5>
 * <ul>
 * <li><i></i>Optional – 1!a6!n3!a15d – - D/C | Date | Currency | Amount</li>
 * <li><i></i>Indicates the funds which are available to the account owner (if a credit or debit balance) for the specified forward value date</li>
 * <li><i></i>Debit Balance /Credit Balance |&nbsp; Date (YYMMDD)&nbsp;&nbsp;| Currency (ISO)&nbsp;| Amount</li>
 * </ul>
 * <p>&nbsp;</p>
 * <h5>Tag 86 – Information to Account Owner</h5>
 * <ul>
 * <li><i></i>Optional – 6x65x</li>
 * <li><i></i>Additional information about the statement as a whole</li>
 * </ul>
 * <span id="last"></span><div id="slidebox" style="right: -430px;"><a class="close"></a>Thanks for stopping by – Take a look around!</div>
 * <p>I’ve deliberately missed out the SWIFT header and footer sections, since here we’re mostly interested in the MT940 format and&nbsp;data contents.</p>
 * <p>Having quick access to this t is an MT940?has certainly helped me recently, and I hope it helps you too!</p>
 * <p>&nbsp;<br>
 * <script type="text/javascript" src="//newsharecounts.s3-us-west-2.amazonaws.com/nsc.js"></script></p>
 * <div class="shareaholic-canvas shareaholic-ui shareaholic-resolved-canvas ng-scope" data-app-id="24703863" data-app="share_buttons" data-title="MT940 Format Overview" data-link="http://www.sepaforcorporates.com/swift-for-corporates/account-statement-mt940-file-format-overview/" data-summary="" id="shr_canvas2"><div ng-show="ready" ng-class="containerClasses" ng-controller="AppCtrl as appCtrl" shr-overflow-limit="3" shr-overflow-child=".shareaholic-share-button, .shareaholic-total-count" shr-overflow-last="true" shr-overflow-enabled="" shr-overflow-wiggle="5" shr-auto-size="" shr-auto-size-trigger="3" shr-auto-size-target=".shareaholic-share-button .share-button-sizing, .shareaholic-total-count" shr-auto-size-mobile="config.isMobile" shr-auto-size-min-size="44" class="ng-scope">
 * <div class="shareaholic-share-buttons-container shareaholic-ui    badge-counter  " ng-style="config.verticalOffset ? {top: config.verticalOffset} : {}" style="top: 15%;">
 * <div class="shareaholic-share-buttons-wrapper shareaholic-ui">
 *
 * </div>
 * </pre>
 *
 * @see <a href="http://www.sepaforcorporates.com/swift-for-corporates/account-statement-mt940-file-format-overview/">http://www.sepaforcorporates.com/swift-for-corporates/account-statement-mt940-file-format-overview/</a>
 */
public class SwiftMT942 {

    /**
     * @see TransactionReferenceNumber#TAG
     */
    private final TransactionReferenceNumber transactionReferenceNumber;

    /**
     * @see RelatedReference#TAG
     */
    private final Optional<RelatedReference> relatedReference;

    /**
     * @see AccountIdentification#TAG
     */
    private final AccountIdentification accountIdentification;

    /**
     * @see StatementNumber#TAG
     */
    private final StatementNumber statementNumber;

    /**
     * @see FloorLimitIndicator#TAG
     */
    private final FloorLimitIndicator floorLimitIndicatorDebitCredit;

    /**
     * @see FloorLimitIndicator#TAG
     */
    private final Optional<FloorLimitIndicator> floorLimitIndicatorCredit;

    /**
     * @see DateTimeIndicator#TAG
     */
    private final DateTimeIndicator dateTimeIndicator;

    /**
     * @see StatementLine#TAG
     * @see InformationToAccountOwner#TAG
     */
    private final List<TransactionGroup> transactionList;

    /**
     * @see InformationToAccountOwner#TAG
     */
    private final Optional<InformationToAccountOwner> informationToAccountOwner;

    /**
     * @see TransactionSummary#TAG_DEBIT
     */
    private final Optional<TransactionSummary> transactionSummaryDebit;

    /**
     * @see TransactionSummary#TAG_CREDIT
     */
    private final Optional<TransactionSummary> transactionSummaryCredit;


    public SwiftMT942(
            TransactionReferenceNumber transactionReferenceNumber,
            RelatedReference relatedReference,
            AccountIdentification accountIdentification,
            StatementNumber statementNumber,
            FloorLimitIndicator floorLimitIndicatorDebitCredit,
            FloorLimitIndicator floorLimitIndicatorCredit,
            DateTimeIndicator dateTimeIndicator,
            List<TransactionGroup> transactionList,
            TransactionSummary transactionSummaryDebit,
            TransactionSummary transactionSummaryCredit,
            InformationToAccountOwner informationToAccountOwner) {
        this.transactionReferenceNumber = Preconditions.checkNotNull(transactionReferenceNumber);
        this.relatedReference = Optional.ofNullable(relatedReference);
        this.accountIdentification = Preconditions.checkNotNull(accountIdentification);
        this.statementNumber = Preconditions.checkNotNull(statementNumber);
        this.floorLimitIndicatorDebitCredit = Preconditions.checkNotNull(floorLimitIndicatorDebitCredit);
        this.floorLimitIndicatorCredit =  Optional.ofNullable(floorLimitIndicatorCredit);
        this.dateTimeIndicator = Preconditions.checkNotNull(dateTimeIndicator);
        this.transactionList =  Preconditions.checkNotNull(transactionList);
        this.transactionSummaryDebit =  Optional.ofNullable(transactionSummaryDebit);
        this.transactionSummaryCredit =  Optional.ofNullable(transactionSummaryCredit);
        this.informationToAccountOwner =  Optional.ofNullable(informationToAccountOwner);
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

    public FloorLimitIndicator getFloorLimitIndicatorDebitCredit() {
        return floorLimitIndicatorDebitCredit;
    }

    public Optional<FloorLimitIndicator> getFloorLimitIndicatorCredit() {
        return floorLimitIndicatorCredit;
    }

    public DateTimeIndicator getDateTimeIndicator() {
        return dateTimeIndicator;
    }

    public List<TransactionGroup> getTransactionList() {
        return transactionList;
    }

    public Optional<InformationToAccountOwner> getInformationToAccountOwner() {
        return informationToAccountOwner;
    }

    public Optional<TransactionSummary> getTransactionSummaryDebit() {
        return transactionSummaryDebit;
    }

    public Optional<TransactionSummary> getTransactionSummaryCredit() {
        return transactionSummaryCredit;
    }
}
