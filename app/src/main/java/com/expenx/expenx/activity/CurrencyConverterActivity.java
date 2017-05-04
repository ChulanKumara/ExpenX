package com.expenx.expenx.activity;

import android.app.Activity;
import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.expenx.expenx.R;
import com.expenx.expenx.core.CalculatorDialog;

public class CurrencyConverterActivity extends AppCompatActivity {

    //this is just an example. delete these when you are actually implementing
    public Button mOpenCalculator;
    public EditText mFinalCalculatorValueEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_converter);

        mFinalCalculatorValueEditText = (EditText) findViewById(R.id.editTextCalculatorFinalBalue) ;

        mOpenCalculator = (Button) findViewById(R.id.buttonCalculorOpen);
        mOpenCalculator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalculatorDialog calculatorDialog = new CalculatorDialog();
                calculatorDialog.showDialog(CurrencyConverterActivity.this, mFinalCalculatorValueEditText);
            }
        });
    }
}
