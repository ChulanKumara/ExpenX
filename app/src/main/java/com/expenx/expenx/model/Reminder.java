package com.expenx.expenx.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by skaveesh on 2017-04-10.
 */

@IgnoreExtraProperties
public class Reminder {

    public String frequency;
    public boolean onState;
    public Long time;

    public Reminder(String frequency, boolean onState, Long time) {
        this.frequency = frequency;
        this.onState = onState;
        this.time = time;
    }

    public Reminder() {
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("frequency", frequency);
        result.put("onState", onState);
        result.put("time", time);

        return result;
    }
}
