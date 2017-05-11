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
import com.expenx.expenx.model.Expense;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExpenseActivity extends AppCompatActivity {

    EditText mEditTextExpenseAmount, mEditTextExpenseDescription;
    ImageButton mImageButtonExpenseEditAmount;
    Spinner mSpinnerExpenseCategory, mSpinnerExpensePaymentMethod;
    Button mButtonCancelExpense, mButtonSaveExpense;

    TextView errorText;

    public DatabaseReference mDatabase;
    public FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        mEditTextExpenseAmount = (EditText) findViewById(R.id.editTextExpenseAmount);
        mEditTextExpenseDescription = (EditText) findViewById(R.id.editTextExpenseDescription);
        mImageButtonExpenseEditAmount = (ImageButton) findViewById(R.id.imageButtonExpenseEditAmount);
        mSpinnerExpenseCategory = (Spinner) findViewById(R.id.spinnerExpenseCategory);
        mSpinnerExpensePaymentMethod = (Spinner) findViewById(R.id.spinnerExpensePaymentMethod);
        mButtonCancelExpense = (Button) findViewById(R.id.buttonCancelExpense);
        mButtonSaveExpense = (Button) findViewById(R.id.buttonSaveExpense);

        initializeLists();

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("expense").child(mAuth.getCurrentUser().getUid());

        mImageButtonExpenseEditAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalculatorDialog calculatorDialog = new CalculatorDialog();
                calculatorDialog.showDialog(ExpenseActivity.this, mEditTextExpenseAmount);
            }
        });

        mButtonCancelExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExpenseActivity.this.finish();
            }
        });

        mButtonSaveExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEditTextExpenseAmount.getText().toString().trim().length() == 0) {
                    errorText = (TextView) findViewById(R.id.editTextExpenseAmount);
                    errorText.requestFocus();
                    errorText.setError("FIELD CANNOT BE EMPTY");
                } else {
                    Double DAmount = Double.parseDouble(mEditTextExpenseAmount.getText().toString().trim());

                    Date date = new Date();
                    long timeMilli = date.getTime();

                    Expense expense = new Expense(DAmount, mSpinnerExpenseCategory.getSelectedItem().toString(), mEditTextExpenseDescription.getText().toString().trim(), mSpinnerExpensePaymentMethod.getSelectedItem().toString(), timeMilli);
                    mDatabase.push().setValue(expense);

                    Toast.makeText(ExpenseActivity.this, "Expense Added Successfully", Toast.LENGTH_LONG).show();

                    ExpenseActivity.this.finish();
                }
            }
        });
    }

    private void initializeLists(){
        List<String> listExpenseCategory = new ArrayList<String>();
        listExpenseCategory.add("Shopping");
        listExpenseCategory.add("Travel");
        listExpenseCategory.add("Food");
        listExpenseCategory.add("Bills");
        listExpenseCategory.add("Medical");
        listExpenseCategory.add("Other");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_items, R.id.textView, listExpenseCategory);
        mSpinnerExpenseCategory.setAdapter(dataAdapter);

        mSpinnerExpensePaymentMethod.setAdapter(SpinnerPaymentMethodInitializer.initialize(ExpenseActivity.this));
    }
}
