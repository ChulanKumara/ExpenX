package com.expenx.expenx.core;

/**
 * Created by Imanshu on 5/8/2017.
 */

public class CalendarDataModel {

    private String TransactionType;
    private String TransactionInfo;
    private String TransactionDesc;

    public CalendarDataModel(String transactionType, String transactionInfo, String transactionDesc) {
        TransactionType = transactionType;
        TransactionInfo = transactionInfo;
        TransactionDesc = transactionDesc;
    }

    public String getTransactionType() {
        return TransactionType;
    }

    public String getTransactionInfo() {
        return TransactionInfo;
    }

    public String getTransactionDesc() { return TransactionDesc; }
}
