package com.expenx.expenx.model;

import com.google.firebase.database.Exclude;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by skaveesh on 2017-04-10.
 */

public class LendTo {

    public String pushId;
    public double amount;
    public String lendFrom;
    public Long lendDate;
    public Long dueDate;
    public String description;
    public String paymentMethod;
    public String refCheckNo;

    public LendTo(double amount, String lendFrom, Long lendDate, Long dueDate, String description, String paymentMethod, String refCheckNo) {
        this.amount = amount;
        this.lendFrom = lendFrom;
        this.lendDate = lendDate;
        this.dueDate = dueDate;
        this.description = description;
        this.paymentMethod = paymentMethod;
        this.refCheckNo = refCheckNo;
    }

    public LendTo() {
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("amount", amount);
        result.put("lendFrom", lendFrom);
        result.put("lendDate", lendDate);
        result.put("dueDate", dueDate);
        result.put("description", description);
        result.put("paymentMethod", paymentMethod);
        result.put("refCheckNo", refCheckNo);

        return result;
    }
}
