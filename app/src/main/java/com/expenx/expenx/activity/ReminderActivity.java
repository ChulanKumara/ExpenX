package com.expenx.expenx.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.TextClock;

import com.expenx.expenx.R;
import com.expenx.expenx.core.DataModel;
import com.expenx.expenx.core.MessageOutput;
import com.expenx.expenx.core.NotifyService;
import com.expenx.expenx.model.Expense;
import com.expenx.expenx.model.Reminder;
import com.expenx.expenx.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.Timestamp;
import java.util.Calendar;

public abstract class ReminderActivity extends AppCompatActivity implements OnItemSelectedListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private FirebaseUser loggedUser = null;
    public User user = null;
    SharedPreferences sharedPreferences;
    private Button mSaveButton;

    private Switch mySwitch;
    private Spinner spinner;
    private TextClock textClock;

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

        //Check Fire Base User
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mSaveButton = (Button) findViewById(R.id.btnSave);
        spinner = (Spinner) findViewById(R.id.frequency_spinner);
        textClock = (TextClock) findViewById(R.id.textClock);
        mySwitch = (Switch) findViewById(R.id.ReminderToggle);
        //setSwitch();

        mSaveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                saveReminder();
            }
        });

    }

    private void setSwitch() {

        mySwitch.setChecked(false);

        mDatabase.child("user").child(sharedPreferences.getString("uid", null)).child("reminder").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                    Reminder reminder = dataSnapshot.getValue(Reminder.class);

                    if (reminder.onState) {
                        mySwitch.setChecked(true);
                    }
                    //Set TextClock And Frequency

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                MessageOutput.showSnackbarLongDuration(ReminderActivity.this, databaseError.getMessage());
            }
        });

        /*
        //check the current state before we display the screen

        */
    }

    public void saveReminder(){

        String frequncy;
        boolean onState;
        long time;

        //Check if the user have an active internet connection
        ConnectivityManager conMgr =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
        if (netInfo == null){
            Toast.makeText(this, "Check your internet connection", Toast.LENGTH_LONG).show();
            return;
        }

        if (mySwitch.isChecked()) {
            onState = true;
        } else {
            onState = false;
        }

        Object obj = spinner.getSelectedItem();
        switch((String)obj) {
            case "Daily" : {
                frequncy = "Daily";
            }
            case "Weekly" : {
               frequncy = "Weekly";
            }
            case "Monthly" : {
                frequncy = "Monthly";
            }
            case "Quaterly" : {
                frequncy = "Quaterly";
            }
            case "Yearly" : {
                frequncy = "Yearly";
            }
            default:{
                frequncy = "Weekly";
            }
        }

        String timeStr = textClock.getText().toString();
        time = Long.parseLong(timeStr);

        Reminder newReminder = new Reminder(frequncy,onState,time);
        DatabaseReference currentDb = mDatabase.child("user").child(sharedPreferences.getString("uid", null)).child("reminder");
        currentDb.setValue(newReminder);


        //Alam manger to set reminder

        //Daily Alaram
        Intent myIntent = new Intent(this , NotifyService.class);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, myIntent, 0);


        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.AM_PM, Calendar.AM);
        calendar.add(Calendar.DAY_OF_MONTH, 1);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000*60*60*24 , pendingIntent);

        // @Samintha -  I checked and alarm manager might not work after a restart :/

        Toast.makeText(this, "Reminder Updated", Toast.LENGTH_LONG).show();
    }


}
