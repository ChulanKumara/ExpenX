package com.expenx.expenx.activity;

/**
 * Created by TP Live on 5/4/2017.
 */
import android.app.Activity;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.expenx.expenx.R;
import com.expenx.expenx.model.LendTo;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class DebtArrayAdapter extends ArrayAdapter<LendTo> {

        public DebtArrayAdapter(Activity context, List<LendTo> lendList) {
            super(context, 0, lendList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            LendTo lend = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.debt_layout, parent, false);
            }
            // Lookup view for data population
            TextView text1 = (TextView) convertView.findViewById(R.id.textViewLendto);
            TextView text2 = (TextView) convertView.findViewById(R.id.textViewDate);
            TextView text3 = (TextView) convertView.findViewById(R.id.textViewDueDate);
            TextView text4 = (TextView) convertView.findViewById(R.id.textViewAmount);
            // Populate the data into the template view using the data object
            if(lend.type.equals("lend")){
                text1.setText("Lend to "+lend.name);
                text4.setTextColor(Color.parseColor("#cc0c0c"));
             }
             else{ text1.setText("Borrow from "+lend.name);
                text4.setTextColor(Color.parseColor("#218c23"));
             }

            text2.setText("Date "+ getDate(lend.date));
            text3.setText("Due Date "+ getDate(lend.dueDate));
            text4.setText(""+lend.amount);
            // Return the completed view to render on screen
            return convertView;
        }


    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("dd/MM/yyyy", cal).toString();
        return date;
    }
    }