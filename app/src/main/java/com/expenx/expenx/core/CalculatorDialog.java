package com.expenx.expenx.core;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.expenx.expenx.R;

/**
 * Created by skaveesh on 2017-05-04.
 */

public class CalculatorDialog implements View.OnClickListener {

    private Button one, two, three, four, five, six, seven, eight, nine, zero;
    private Button plus, subtract, divide, multiply, clear;
    private Button ac, percent, dot, double_zero, equal;
    private Button mCalculaorValueOk;
    private String currentDisplayedInput = "";
    private String inputToBeParsed = "";
    private TextView outputResult;
    private Calculator mCalculator;

    private double finalResultValue;

    private void obtainInputValues(String input) {
        switch (input) {
            case "0":
                currentDisplayedInput += "0";
                inputToBeParsed += "0";
                break;
            case "1":
                currentDisplayedInput += "1";
                inputToBeParsed += "1";
                break;
            case "2":
                currentDisplayedInput += "2";
                inputToBeParsed += "2";
                break;
            case "3":
                currentDisplayedInput += "3";
                inputToBeParsed += "3";
                break;
            case "4":
                currentDisplayedInput += "4";
                inputToBeParsed += "4";
                break;
            case "5":
                currentDisplayedInput += "5";
                inputToBeParsed += "5";
                break;
            case "6":
                currentDisplayedInput += "6";
                inputToBeParsed += "6";
                break;
            case "7":
                currentDisplayedInput += "7";
                inputToBeParsed += "7";
                break;
            case "8":
                currentDisplayedInput += "8";
                inputToBeParsed += "8";
                break;
            case "9":
                currentDisplayedInput += "9";
                inputToBeParsed += "9";
                break;
            case ".":
                currentDisplayedInput += ".";
                inputToBeParsed += ".";
                break;
            case "+":
                currentDisplayedInput += "+";
                inputToBeParsed += "+";
                break;
            case "-":
                currentDisplayedInput += "-";
                inputToBeParsed += "-";
                break;
            case "/":
                currentDisplayedInput += "/";
                inputToBeParsed += "/";
                break;
            case "x":
                currentDisplayedInput += "*";
                inputToBeParsed += "*";
                break;
            case "%":
                currentDisplayedInput += "%";
                inputToBeParsed += "%";
                break;
            case "00":
                currentDisplayedInput += "00";
                inputToBeParsed += "00";
                break;
            case "=":
                currentDisplayedInput += "00";
                inputToBeParsed += "00";
                break;
        }
        outputResult.setText(currentDisplayedInput);
    }

    @Override
    public void onClick(View view) {
        Button button = (Button) view;
        String data = button.getText().toString();

        if (data.equals("AC")) {
            outputResult.setText("");
            currentDisplayedInput = "";
            inputToBeParsed = "";
        } else if (data.equals("C")) {
            if (currentDisplayedInput.length() > 0) {
                String s = currentDisplayedInput;
                s = s.substring(0, currentDisplayedInput.length() - 1);
                currentDisplayedInput = s;

                outputResult.setText(s);
                inputToBeParsed = s;
            }
        } else if (data.equals("=")) {

            // call a function that will return the result of the calculate.
            String resultObject = mCalculator.getResult(currentDisplayedInput, inputToBeParsed);
            outputResult.setText(removeTrailingZero(resultObject));

            if (outputResult.getText().toString().trim().length() > 0 && !outputResult.getText().toString().trim().equalsIgnoreCase("Error") && !outputResult.getText().toString().trim().equals(""))
                finalResultValue = Double.parseDouble(outputResult.getText().toString());
            else
                finalResultValue = 0;
        } else {
            obtainInputValues(data);
        }
    }


    private String removeTrailingZero(String formattingInput) {
        if (!formattingInput.contains(".")) {
            return formattingInput;
        }
        int dotPosition = formattingInput.indexOf(".");
        String newValue = formattingInput.substring(dotPosition, formattingInput.length());
        if (newValue.equals(".0")) {
            return formattingInput.substring(0, dotPosition);
        }
        return formattingInput;
    }

    public void showDialog(Activity activity, final EditText finalEditTextValueView) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.calculator);


        //setting up calculator buttons and view

        outputResult = (TextView) dialog.findViewById(R.id.display);
        outputResult.setText("");
        mCalculator = new Calculator();
        one = (Button) dialog.findViewById(R.id.one);
        two = (Button) dialog.findViewById(R.id.two);
        three = (Button) dialog.findViewById(R.id.three);
        four = (Button) dialog.findViewById(R.id.four);
        five = (Button) dialog.findViewById(R.id.five);
        six = (Button) dialog.findViewById(R.id.six);
        seven = (Button) dialog.findViewById(R.id.seven);
        eight = (Button) dialog.findViewById(R.id.eight);
        nine = (Button) dialog.findViewById(R.id.nine);
        zero = (Button) dialog.findViewById(R.id.zero);
        plus = (Button) dialog.findViewById(R.id.plus);
        subtract = (Button) dialog.findViewById(R.id.minus);
        divide = (Button) dialog.findViewById(R.id.divide);
        multiply = (Button) dialog.findViewById(R.id.multiply);
        clear = (Button) dialog.findViewById(R.id.clear);
        ac = (Button) dialog.findViewById(R.id.acCalculator);
        percent = (Button) dialog.findViewById(R.id.percent);
        dot = (Button) dialog.findViewById(R.id.dot);
        double_zero = (Button) dialog.findViewById(R.id.double_zero);
        equal = (Button) dialog.findViewById(R.id.equal);
        mCalculaorValueOk = (Button) dialog.findViewById(R.id.buttonCalculaorOK);
        one.setOnClickListener(this);
        two.setOnClickListener(this);
        three.setOnClickListener(this);
        four.setOnClickListener(this);
        five.setOnClickListener(this);
        six.setOnClickListener(this);
        seven.setOnClickListener(this);
        eight.setOnClickListener(this);
        nine.setOnClickListener(this);
        zero.setOnClickListener(this);
        plus.setOnClickListener(this);
        subtract.setOnClickListener(this);
        divide.setOnClickListener(this);
        multiply.setOnClickListener(this);
        clear.setOnClickListener(this);
        ac.setOnClickListener(this);
        percent.setOnClickListener(this);
        dot.setOnClickListener(this);
        double_zero.setOnClickListener(this);
        equal.setOnClickListener(this);
        mCalculaorValueOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalEditTextValueView.setText(finalResultValue + "");
                dialog.dismiss();
            }
        });

        dialog.show();

    }


}
