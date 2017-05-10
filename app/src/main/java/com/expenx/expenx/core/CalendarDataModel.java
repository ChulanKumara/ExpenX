package com.expenx.expenx.core;

/**
 * Created by Imanshu on 5/8/2017.
 */

public class CalendarDataModel {

    private String TransactionType;
    private String TransactionInfo;

    public CalendarDataModel(String TransactionType, String TransactionInfo) {
        this.TransactionType = TransactionType;
        this.TransactionInfo = TransactionInfo;
    }

    public String getTransactionType() {
        return TransactionType;
    }

    public String getTransactionInfo() {
        return TransactionInfo;
    }

}
