package com.qoomon.banking.swift.message.submessage.field;

/**
 * Created by qoomon on 08/07/2016.
 */
public class Seperator implements SwiftField {

    public static final String TAG = "-";

    @Override
    public String getTag() {
        return TAG;
    }
}
