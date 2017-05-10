package com.expenx.expenx.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.expenx.expenx.R;

public abstract class ReminderActivity extends AppCompatActivity implements OnItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        Spinner spinner = (Spinner) findViewById(R.id.frequency_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ReminderActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.frequency_array));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        //spinner.setOnItemSelectedListener(this);
    }
}
