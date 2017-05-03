package com.expenx.expenx.model;

import com.google.firebase.database.Exclude;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by skaveesh on 2017-04-10.
 */

public class BorrowFrom {

    public String pushId;
    public double amount;
    public String borrowedFrom;
    public Long borrowedDate;
    public Long dueDate;
    public String description;
    public String paymentMethod;
    public String refCheckNo;

    public BorrowFrom(double amount, String borrowedFrom, Long borrowedDate, Long dueDate, String description, String paymentMethod, String refCheckNo) {
        this.amount = amount;
        this.borrowedFrom = borrowedFrom;
        this.borrowedDate = borrowedDate;
        this.dueDate = dueDate;
        this.description = description;
        this.paymentMethod = paymentMethod;
        this.refCheckNo = refCheckNo;
    }

    public BorrowFrom() {
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("amount", amount);
        result.put("lendFrom", borrowedFrom);
        result.put("lendDate", borrowedDate);
        result.put("dueDate", dueDate);
        result.put("description", description);
        result.put("paymentMethod", paymentMethod);
        result.put("refCheckNo", refCheckNo);

        return result;
    }
}
