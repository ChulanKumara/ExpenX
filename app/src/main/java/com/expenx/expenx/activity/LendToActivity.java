package com.expenx.expenx.activity;

/**
 * Created by TP Live on 5/2/2017.
 */

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.expenx.expenx.R;
import com.expenx.expenx.core.CalculatorDialog;
import com.expenx.expenx.model.LendTo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class LendToActivity extends AppCompatActivity {


    public DatabaseReference mDatabase, mDatabase2;
    public FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    private String TAG = "expenxtag";
    private static final String REQUIRED = "Required";


    EditText mLendToName, mAmount, mDate, mDueDate, mRef, mDesc;
    TextView errorText;
    Button mAddBtn, mClear;
    ImageButton btnContacts, mEditAmount;
    Spinner mPayment;
    Calendar myCalendar = Calendar.getInstance();
    Calendar myCalendar_due = Calendar.getInstance();
    Spinner mspinner;

    SharedPreferences preferences = null;
    SharedPreferences.Editor editor = null;
    private static final int PICK_CONTACT = 1000;

    DatePickerDialog.OnDateSetListener date, date2;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lend_to);
        addItemsOnSpinner();


        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                    editor = preferences.edit();
                    editor.putString("uid", user.getUid());
                    editor.putString("email", user.getEmail());
                    editor.apply();

                    startActivity(new Intent(LendToActivity.this, ExpenxActivity.class));
                    LendToActivity.this.finish();
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };


        mAddBtn = (Button) findViewById(R.id.button_lend_add);
        mClear = (Button) findViewById(R.id.button_lend_clear);
        btnContacts = (ImageButton) findViewById(R.id.button_lend_name);
        mEditAmount = (ImageButton) findViewById(R.id.button_lend_amount);
        mLendToName = (EditText) findViewById(R.id.custom_name);
        mAmount = (EditText) findViewById(R.id.custom_amount);
        mDate = (EditText) findViewById(R.id.custom_date_lend);
        mDueDate = (EditText) findViewById(R.id.custom_due_date_lend);
        mspinner = (Spinner) findViewById(R.id.custom_spinner_payment);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("debt").child(mAuth.getCurrentUser().getUid());

        mDate.setText(String.format(myCalendar.get(Calendar.DATE) + "/" + (myCalendar.get(Calendar.MONTH) + 1) + "/" + (myCalendar.get(Calendar.YEAR)), "yy"));
        mDueDate.setText(String.format(myCalendar_due.get(Calendar.DATE) + "/" + (myCalendar_due.get(Calendar.MONTH) + 1) + "/" + (myCalendar_due.get(Calendar.YEAR) + 1), "yy"));

//        mPayment = (Spinner) findViewById(R.id.custom_spinner_payment);
        mRef = (EditText) findViewById(R.id.custom__ref);
        mDesc = (EditText) findViewById(R.id.custom_desc);
        mDate.setShowSoftInputOnFocus(false);
        mDueDate.setShowSoftInputOnFocus(false);
        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String LendTo = mLendToName.getText().toString().trim();
                String Amount = mAmount.getText().toString().trim();

                long Date = myCalendar.getTimeInMillis();
                long DueDate = myCalendar_due.getTimeInMillis();
                String mPayment = mspinner.getSelectedItem().toString();
                String Ref = mRef.getText().toString().trim();
                String Desc = mDesc.getText().toString().trim();

                if (LendTo.length() == 0) {
                    errorText = (TextView) findViewById(R.id.custom_name);
                    errorText.requestFocus();
                    errorText.setError("FIELD CANNOT BE EMPTY");
                } else if (Amount.length() == 0) {
                    errorText = (TextView) findViewById(R.id.custom_amount);
                    errorText.requestFocus();
                    errorText.setError("FIELD CANNOT BE EMPTY");
                } else {
                    Double DAmount = Double.parseDouble(Amount);
                    com.expenx.expenx.model.LendTo lend = new LendTo(DAmount, LendTo, Date, DueDate, Desc, mPayment, Ref, "lend");
                    mDatabase.push().setValue(lend);

                    Toast.makeText(LendToActivity.this, "Validation Successful", Toast.LENGTH_LONG).show();

                    LendToActivity.this.finish();
                }

            }


        });

        mClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LendToActivity.this.finish();
            }
        });

        mEditAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalculatorDialog calculatorDialog = new CalculatorDialog();
                calculatorDialog.showDialog(LendToActivity.this, mAmount);
            }
        });

        //contact select
        btnContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, PICK_CONTACT);
            }
        });


        //Calender for Date
        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }


        };

        mDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(LendToActivity.this, android.R.style.Theme_DeviceDefault_Dialog, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        //Calender for DueDate
        date2 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar_due.set(Calendar.YEAR, year);
                myCalendar_due.set(Calendar.MONTH, monthOfYear);
                myCalendar_due.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel2();
            }


        };

        mDueDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(LendToActivity.this, android.R.style.Theme_DeviceDefault_Dialog, date2, myCalendar_due
                        .get(Calendar.YEAR), myCalendar_due.get(Calendar.MONTH),
                        myCalendar_due.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        switch (reqCode) {
            case (PICK_CONTACT):
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor phone = getContentResolver().query(contactData, null, null, null, null);
                    if (phone.moveToFirst()) {
                        String contactNumberName = phone.getString(phone.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        // Todo something when contact number selected
                        mLendToName.setText(contactNumberName);
                    }
                }
                break;
        }
    }


    private void updateLabel() {

        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        mDate.setText(sdf.format(myCalendar.getTime()));
    }

    private void updateLabel2() {

        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        mDueDate.setText(sdf.format(myCalendar_due.getTime()));
    }

    public void addItemsOnSpinner() {

        mspinner = (Spinner) findViewById(R.id.custom_spinner_payment);
        List<String> list = new ArrayList<String>();
        list.add("Cash");
        list.add("Cheque");
        list.add("Money Order Payment");
        list.add("Credit Card Payment");
        list.add("Debit Card Payment");
        list.add("Online Payment");
        list.add("Gift Card");
        list.add("Voucher");
        list.add("Bitcoin");
        list.add("Other");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_items, R.id.textView,list);

        mspinner.setAdapter(dataAdapter);
    }
}
