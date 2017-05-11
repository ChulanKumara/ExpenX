package com.expenx.expenx.core;

import android.app.Activity;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.expenx.expenx.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by skaveesh on 2017-05-11.
 */

public class SpinnerPaymentMethodInitializer {

    public static ArrayAdapter<String> initialize(Activity activity){
        List<String> listPaymentMethod = new ArrayList<String>();
        listPaymentMethod.add("Cash");
        listPaymentMethod.add("Cheque");
        listPaymentMethod.add("Money Order Payment");
        listPaymentMethod.add("Credit Card Payment");
        listPaymentMethod.add("Debit Card Payment");
        listPaymentMethod.add("Online Payment");
        listPaymentMethod.add("Gift Card");
        listPaymentMethod.add("Voucher");
        listPaymentMethod.add("Bitcoin");
        listPaymentMethod.add("Other");
        return new ArrayAdapter<String>(activity,
                R.layout.spinner_items, R.id.textView, listPaymentMethod);

    }

}
