package com.expenx.expenx.activity;

import android.app.Activity;
import android.app.Dialog;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.expenx.expenx.R;
import com.expenx.expenx.core.CalculatorDialog;
import com.expenx.expenx.core.DefaultCurrencyInitializer;
import com.expenx.expenx.core.GetCurrencyRate;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class CurrencyConverterActivity extends AppCompatActivity {

    public Button mConvertCurrencyButton;
    public ImageButton mImageButtonSwapCurrency, mImageButtonEditAmount;
    public EditText mEditTextCurrencyAmount, mEditTextCurrencyResult;
    public Spinner mSpinnerLeftCurrency, mSpinnerRightCurrency;

    List<String> currencyList;

    Retrofit retrofit;

    public static final String ENDPOINT_URL = "http://free.currencyconverterapi.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_converter);

        mImageButtonEditAmount = (ImageButton) findViewById(R.id.imageButtonEditAmount);
        mImageButtonSwapCurrency = (ImageButton) findViewById(R.id.imageButtonSwapCurrency);

        mEditTextCurrencyAmount = (EditText) findViewById(R.id.editTextCurrencyAmount);
        mEditTextCurrencyResult = (EditText) findViewById(R.id.editTextCurrencyResult);

        mSpinnerLeftCurrency = (Spinner) findViewById(R.id.spinnerLeftCurrency);
        mSpinnerRightCurrency = (Spinner) findViewById(R.id.spinnerRightCurrency);

        mConvertCurrencyButton = (Button) findViewById(R.id.convertCurrencyButton);

        //requesting from free currency api
        retrofit = new Retrofit.Builder().baseUrl(ENDPOINT_URL).addConverterFactory(ScalarsConverterFactory.create()).build();

        addToCurrencyList();
        addItemsOnSpinner();
        addListenerOnButton();

        mImageButtonEditAmount.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CalculatorDialog calculatorDialog = new CalculatorDialog();
                calculatorDialog.showDialog(CurrencyConverterActivity.this, mEditTextCurrencyAmount);
            }
        });

        mImageButtonSwapCurrency.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int leftSpinnerIndex = mSpinnerLeftCurrency.getSelectedItemPosition();
                int rightSpinnerIndex = mSpinnerRightCurrency.getSelectedItemPosition();
                mSpinnerLeftCurrency.setSelection(rightSpinnerIndex);
                mSpinnerRightCurrency.setSelection(leftSpinnerIndex);
            }
        });
    }

    private void addToCurrencyList() {
        currencyList = DefaultCurrencyInitializer.initialize();
    }

    // add items into spinner dynamically
    public void addItemsOnSpinner() {

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item_custom, currencyList);

        dataAdapter.setDropDownViewResource(R.layout.spinner_item_custom);
        mSpinnerLeftCurrency.setAdapter(dataAdapter);

        dataAdapter.setDropDownViewResource(R.layout.spinner_item_custom);
        mSpinnerRightCurrency.setAdapter(dataAdapter);
    }

    public void addListenerOnButton() {

        mConvertCurrencyButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mEditTextCurrencyAmount.getText() != null) {
                    final String currencyToConvert = String.valueOf(mSpinnerLeftCurrency.getSelectedItem() + "_" + String.valueOf(mSpinnerRightCurrency.getSelectedItem()));

                    GetCurrencyRate getCurrencyRate = retrofit.create(GetCurrencyRate.class);
                    Call<ResponseBody> call = getCurrencyRate.getCurrencyRate(currencyToConvert, "ultra");
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                double currencyRate = jsonObject.getDouble(currencyToConvert);

                                double amountToConvert = Double.parseDouble(mEditTextCurrencyAmount.getText().toString());

                                NumberFormat decimalFormat = new DecimalFormat("#.00");

                                mEditTextCurrencyResult.setText(decimalFormat.format((amountToConvert * currencyRate)));

                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            t.printStackTrace();
                        }
                    });
                }
            }
        });
    }
}
