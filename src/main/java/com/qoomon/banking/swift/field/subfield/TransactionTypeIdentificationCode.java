package com.qoomon.banking.swift.field.subfield;

import java.text.ParseException;

/**
 * Created by qoomon on 06/07/16.
 */
public class TransactionTypeIdentificationCode {

    private final IdentificationType type;
    private final IdentificationCode code;

    TransactionTypeIdentificationCode(IdentificationType type, IdentificationCode code) {
        this.type = type;
        this.code = code;
    }

    public static TransactionTypeIdentificationCode parse(String text) throws ParseException {
        IdentificationType type = IdentificationType.valueOf(text.substring(0, 1));
        IdentificationCode code = IdentificationCode.valueOf(text.substring(1));
        return new TransactionTypeIdentificationCode(type, code);
    }

    public enum IdentificationType {
        F,
        N,
        S
    }

    public enum IdentificationCode {
        BNK, // Securities Related Item – Bank fees
        BOE, // Bill of exchange
        BRF, // Brokerage fee
        CAR, // Securities Related Item – Corporate Actions Related (Should only be used when no specific corporate action event code is available)
        CAS, // Securities Related Item – Cash in Lieu
        CHG, // Charges and other expenses
        CHK, // Cheques
        CLR, // Cash letters/Cheques remittance
        CMI, // Cash management item – No detail
        CMN, // Cash management item – Notional pooling
        CMP, // Compensation claims
        CMS, // Cash management item – Sweeping
        CMT, // Cash management item -Topping
        CMZ, // Cash management item – Zero balancing
        COL, // Collections (used when entering a principal amount)
        COM, // Commission
        CPN, // Securities Related Item – Coupon payments
        DCR, // Documentary credit (used when entering a principal amount)
        DDT, // Direct Debit Item
        DIS, // Securities Related Item – Gains disbursement
        DIV, // Securities Related Item – Dividends
        EQA, // Equivalent amount
        EXT, // Securities Related Item – External transfer for own account
        FEX, // Foreign exchange
        INT, // Interest
        LBX, // Lock box
        LDP, // Loan deposit
        MAR, // Securities Related Item – Margin payments/Receipts
        MAT, // Securities Related Item – Maturity
        MGT, // Securities Related Item – Management fees
        MSC, // Miscellaneous
        NWI, // Securities Related Item – New issues distribution
        ODC, // Overdraft charge
        OPT, // Securities Related Item – Options
        PCH, // Securities Related Item – Purchase (including STIF and Time deposits)
        POP, // Securities Related Item – Pair-off proceeds
        PRN, // Securities Related Item – Principal pay-down/pay-up
        REC, // Securities Related Item – Tax reclaim
        RED, // Securities Related Item – Redemption/Withdrawal
        RIG, // Securities Related Item – Rights
        RTI, // Returned item
        SAL, // Securities Related Item – Sale (including STIF and Time deposits)
        SEC, // Securities (used when entering a principal amount)
        SLE, // Securities Related Item – Securities lending related
        STO, // Standing order
        STP, // Securities Related Item – Stamp duty
        SUB, // Securities Related Item – Subscription
        SWP, // Securities Related Item – SWAP payment
        TAX, // Securities Related Item – Withholding tax payment
        TCK, // Travellers cheques
        TCM, // Securities Related Item – Tripartite collateral management
        TRA, // Securities Related Item – Internal transfer for own account
        TRF, // Transfer
        TRN, // Securities Related Item – Transaction fee
        UWC, // Securities Related Item – Underwriting commission
        VDA, // Value date adjustment (used with an entry made to withdraw an incorrectly dated entry – it will be followed by the correct entry with the relevant code)
        WAR  // Securities Related Item – Warrant
    }
}
