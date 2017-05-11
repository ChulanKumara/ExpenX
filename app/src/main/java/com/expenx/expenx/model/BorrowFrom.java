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
    public String name;
    public Long date;
    public Long dueDate;
    public String description;
    public String paymentMethod;
    public String refCheckNo;
    public String type;

    public BorrowFrom(double amount, String borrowedFrom, Long borrowedDate, Long dueDate, String description, String paymentMethod, String refCheckNo,String type) {
        this.amount = amount;
        this.name = borrowedFrom;
        this.date = borrowedDate;
        this.dueDate = dueDate;
        this.description = description;
        this.paymentMethod = paymentMethod;
        this.refCheckNo = refCheckNo;
        this.type=type;
    }

    public BorrowFrom() {
    }
    public void setPushId(String pushId) {
        this.pushId = pushId;
    }
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("amount", amount);
        result.put("lendFrom", name);
        result.put("lendDate", date);
        result.put("dueDate", dueDate);
        result.put("description", description);
        result.put("paymentMethod", paymentMethod);
        result.put("refCheckNo", refCheckNo);

        return result;
    }
}
