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

import com.expenx.expenx.R;
import com.expenx.expenx.core.CalculatorDialog;
import com.expenx.expenx.core.MessageOutput;
import com.expenx.expenx.model.Income;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class LoanCalculatorActivity extends AppCompatActivity {

    Spinner mSpinnerYears, mSpinnerMonths;
    EditText mEditTextLoanCalAmount, mEditTextLoanCalInterest;
    TextView mTextViewLoanResult;
    ImageButton mImageButtonLoanCalEditAmount;
    Button mButtonCalculateAnnually, mButtonCalculateMonthly;

    List<Integer> yearList, monthList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_calculator);

        mSpinnerYears = (Spinner) findViewById(R.id.spinnerYears);
        mSpinnerMonths = (Spinner) findViewById(R.id.spinnerMonths);

        addYearsToSpinner();
        addMonthsToSpinner();

        mTextViewLoanResult = (TextView) findViewById(R.id.textViewLoanResult);

        mEditTextLoanCalAmount = (EditText) findViewById(R.id.editTextLoanCalAmount);
        mEditTextLoanCalInterest = (EditText) findViewById(R.id.editTextLoanCalInterest);

        mImageButtonLoanCalEditAmount = (ImageButton) findViewById(R.id.imageButtonLoanCalEditAmount);
        mImageButtonLoanCalEditAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalculatorDialog calculatorDialog = new CalculatorDialog();
                calculatorDialog.showDialog(LoanCalculatorActivity.this, mEditTextLoanCalAmount);
            }
        });

        mButtonCalculateAnnually = (Button) findViewById(R.id.buttonCalculateAnnually);
        mButtonCalculateAnnually.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mEditTextLoanCalAmount.getText().toString().isEmpty() && !mEditTextLoanCalInterest.getText().toString().isEmpty() &&  (monthList.get(mSpinnerMonths.getSelectedItemPosition()) != 0 || yearList.get(mSpinnerYears.getSelectedItemPosition()) != 0) && yearList.get(mSpinnerYears.getSelectedItemPosition()) > 0) {
                    double loanAmount = Double.parseDouble(mEditTextLoanCalAmount.getText().toString());
                    int termInMonths = monthList.get(mSpinnerMonths.getSelectedItemPosition()) + (yearList.get(mSpinnerYears.getSelectedItemPosition()) * 12);
                    double interestRate = Double.parseDouble(mEditTextLoanCalInterest.getText().toString());

                    double result = calAnnualPay(loanAmount, termInMonths, interestRate);

                    DecimalFormat df = new DecimalFormat("#,###,###,###.00");
                    mTextViewLoanResult.setText(df.format(result));
                }else {
                    MessageOutput.showSnackbarLongDuration(LoanCalculatorActivity.this,"Please double check your input...!");
                    mTextViewLoanResult.setText("0.00");
                }
            }
        });

        mButtonCalculateMonthly = (Button) findViewById(R.id.buttonCalculateMonthly);
        mButtonCalculateMonthly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mEditTextLoanCalAmount.getText().toString().isEmpty() && !mEditTextLoanCalInterest.getText().toString().isEmpty() &&  (monthList.get(mSpinnerMonths.getSelectedItemPosition()) != 0 || yearList.get(mSpinnerYears.getSelectedItemPosition()) != 0)) {
                    double loanAmount = Double.parseDouble(mEditTextLoanCalAmount.getText().toString());
                    int termInMonths = monthList.get(mSpinnerMonths.getSelectedItemPosition()) + (yearList.get(mSpinnerYears.getSelectedItemPosition()) * 12);
                    double interestRate = Double.parseDouble(mEditTextLoanCalInterest.getText().toString());

                    double result = calMonthlyPay(loanAmount, termInMonths, interestRate);

                    DecimalFormat df = new DecimalFormat("#,###,###,###.00");
                    mTextViewLoanResult.setText(df.format(result));
                }else {
                    MessageOutput.showSnackbarLongDuration(LoanCalculatorActivity.this,"Please double check your input...!");
                    mTextViewLoanResult.setText("0.00");
                }
            }
        });

    }

    public void addYearsToSpinner() {
        yearList = new ArrayList<>();

        for (int i = 0; i <= 12; i++) {
            yearList.add(i);
        }

        ArrayAdapter<Integer> dataAdapter = new ArrayAdapter<>(this,
                R.layout.spinner_item_custom, yearList);

        dataAdapter.setDropDownViewResource(R.layout.spinner_item_custom);
        mSpinnerYears.setAdapter(dataAdapter);

        dataAdapter.setDropDownViewResource(R.layout.spinner_item_custom);
        mSpinnerYears.setAdapter(dataAdapter);
    }

    public void addMonthsToSpinner() {
        monthList = new ArrayList<>();

        for (int i = 0; i <= 12; i++) {
            monthList.add(i);
        }

        ArrayAdapter<Integer> dataAdapter = new ArrayAdapter<>(this,
                R.layout.spinner_item_custom, monthList);

        dataAdapter.setDropDownViewResource(R.layout.spinner_item_custom);
        mSpinnerMonths.setAdapter(dataAdapter);

        dataAdapter.setDropDownViewResource(R.layout.spinner_item_custom);
        mSpinnerMonths.setAdapter(dataAdapter);
    }

    public double calMonthlyPay(double loanAmount, int termInMonths, double interestRate) {

        interestRate /= 100.0;

        double monthlyRate = interestRate / 12.0;

        return (monthlyRate * loanAmount) / (1 - Math.pow(1 + monthlyRate, -termInMonths));
    }

    public double calAnnualPay(double loanAmount, int termInMonths, double interestRate) {

        interestRate /= 100.0;

        double monthlyRate = interestRate;

        double monthlyPayment = (monthlyRate * loanAmount) / (1 - Math.pow(1 + monthlyRate, -termInMonths));

        return monthlyPayment*12;
    }
}
