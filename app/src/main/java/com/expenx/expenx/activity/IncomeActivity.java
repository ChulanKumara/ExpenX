package com.expenx.expenx.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.expenx.expenx.R;
import com.expenx.expenx.core.CalculatorDialog;
import com.expenx.expenx.core.SpinnerPaymentMethodInitializer;
import com.expenx.expenx.model.Income;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class IncomeActivity extends AppCompatActivity {

    EditText mEditTextIncomeAmount, mEditTextIncomeDescription;
    ImageButton mImageButtonIncomeEditAmount;
    Spinner mSpinnerIncomeCategory, mSpinnerIncomePaymentMethod;
    Button mButtonCancelIncome, mButtonSaveIncome;

    TextView errorText;

    public DatabaseReference mDatabase;
    public FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);

        mEditTextIncomeAmount = (EditText) findViewById(R.id.editTextIncomeAmount);
        mEditTextIncomeDescription = (EditText) findViewById(R.id.editTextIncomeDescription);
        mImageButtonIncomeEditAmount = (ImageButton) findViewById(R.id.imageButtonIncomeEditAmount);
        mSpinnerIncomeCategory = (Spinner) findViewById(R.id.spinnerIncomeCategory);
        mSpinnerIncomePaymentMethod = (Spinner) findViewById(R.id.spinnerIncomePaymentMethod);
        mButtonCancelIncome = (Button) findViewById(R.id.buttonCancelIncome);
        mButtonSaveIncome = (Button) findViewById(R.id.buttonSaveIncome);

        initializeLists();

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("income").child(mAuth.getCurrentUser().getUid());

        mImageButtonIncomeEditAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalculatorDialog calculatorDialog = new CalculatorDialog();
                calculatorDialog.showDialog(IncomeActivity.this, mEditTextIncomeAmount);
            }
        });

        mButtonCancelIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IncomeActivity.this.finish();
            }
        });

        mButtonSaveIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEditTextIncomeAmount.getText().toString().trim().length() == 0) {
                    errorText = (TextView) findViewById(R.id.editTextIncomeAmount);
                    errorText.requestFocus();
                    errorText.setError("FIELD CANNOT BE EMPTY");
                } else {
                    Double DAmount = Double.parseDouble(mEditTextIncomeAmount.getText().toString().trim());

                    Date date = new Date();
                    long timeMilli = date.getTime();

                    Income income = new Income(DAmount, mSpinnerIncomeCategory.getSelectedItem().toString(), mEditTextIncomeDescription.getText().toString().trim(), mSpinnerIncomePaymentMethod.getSelectedItem().toString(), timeMilli);
                    mDatabase.push().setValue(income);

                    Toast.makeText(IncomeActivity.this, "Income Added Successfully", Toast.LENGTH_LONG).show();

                    IncomeActivity.this.finish();
                }
            }
        });
    }

    private void initializeLists(){
        List<String> listIncomeCategory = new ArrayList<String>();
        listIncomeCategory.add("Salary");
        listIncomeCategory.add("Business");
        listIncomeCategory.add("Insurance");
        listIncomeCategory.add("Online Selling");
        listIncomeCategory.add("Other");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_items, R.id.textView, listIncomeCategory);
        mSpinnerIncomeCategory.setAdapter(dataAdapter);

        mSpinnerIncomePaymentMethod.setAdapter(SpinnerPaymentMethodInitializer.initialize(IncomeActivity.this));
    }
}
