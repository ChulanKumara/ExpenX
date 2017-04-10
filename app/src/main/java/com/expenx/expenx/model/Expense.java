package com.expenx.expenx.model;

import com.google.firebase.database.Exclude;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by skaveesh on 2017-04-10.
 */

public class Expense {

    public String pushId;
    public double amount;
    public String category;
    public String description;
    public String paymentMethod;
    public Timestamp timestamp;

    public Expense() {
    }

    public Expense(double amount, String category, String description, String paymentMethod, Timestamp timestamp) {
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.paymentMethod = paymentMethod;
        this.timestamp = timestamp;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("amount", amount);
        result.put("category", category);
        result.put("description", description);
        result.put("paymentMethod", paymentMethod);
        result.put("timestamp", timestamp);

        return result;
    }
}
