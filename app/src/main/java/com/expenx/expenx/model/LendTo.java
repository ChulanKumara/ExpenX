package com.expenx.expenx.model;

import com.google.firebase.database.Exclude;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by skaveesh on 2017-04-10.
 */

public class LendTo {

    public String pushId;
    public double amount;
    public String name;
    public long date;
    public long dueDate;
    public String description;
    public String paymentMethod;
    public String refCheckNo;
    public String type;


    public LendTo(double amount, String lendFrom, long lendDate, long dueDate, String description, String paymentMethod, String refCheckNo,String type) {
        this.amount = amount;
        this.name = lendFrom;
        this.date = lendDate;
        this.dueDate = dueDate;
        this.description = description;
        this.paymentMethod = paymentMethod;
        this.refCheckNo = refCheckNo;
        this.type=type;

    }
    public LendTo() {

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
