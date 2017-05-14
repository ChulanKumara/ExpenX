package com.expenx.expenx.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.expenx.expenx.R;
import com.expenx.expenx.core.CalendarDataModel;
import com.expenx.expenx.core.EventDateDecorator;
import com.expenx.expenx.core.MessageOutput;
import com.expenx.expenx.model.Expense;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ForecastActivity extends AppCompatActivity {

    public DatabaseReference mDatabase;
    public FirebaseAuth mAuth;

    TextView mTextViewResult;
    TextView mLastMonthExpense;

    TextView mTextViewShoppingExpense;
    TextView mTextViewTravelExpense;
    TextView mTextViewFoodExpense;
    TextView mTextViewBillsExpense;
    TextView mTextViewMedicalExpense;
    TextView mTextViewOtherExpense;



    HashMap<Integer, Double> monthExpenseHash;

    HashMap<Integer, Double> monthShoppingExpenseHash;
    HashMap<Integer, Double> monthTravelExpenseHash;
    HashMap<Integer, Double> monthFoodExpenseHash;
    HashMap<Integer, Double> monthBillsExpenseHash;
    HashMap<Integer, Double> monthMedicalExpenseHash;
    HashMap<Integer, Double> monthOtherExpenseHash;
    Calendar lastMonthCalendarx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("expense").child(mAuth.getCurrentUser().getUid());

        monthExpenseHash = new HashMap<Integer, Double>();

        monthShoppingExpenseHash = new HashMap<Integer, Double>();
        monthTravelExpenseHash = new HashMap<Integer, Double>();
        monthFoodExpenseHash = new HashMap<Integer, Double>();
        monthBillsExpenseHash = new HashMap<Integer, Double>();
        monthMedicalExpenseHash = new HashMap<Integer, Double>();
        monthOtherExpenseHash = new HashMap<Integer, Double>();




        mTextViewResult = (TextView) findViewById(R.id.forecastTextResult);
        mLastMonthExpense = (TextView) findViewById(R.id.forecastTextLastMonth);

        mTextViewShoppingExpense = (TextView) findViewById(R.id.textViewShopping);
        mTextViewTravelExpense = (TextView) findViewById(R.id.textViewTravel);
        mTextViewFoodExpense = (TextView) findViewById(R.id.textViewFoods);
        mTextViewBillsExpense = (TextView) findViewById(R.id.textViewBills);
        mTextViewMedicalExpense = (TextView) findViewById(R.id.textViewMedical);
        mTextViewOtherExpense = (TextView) findViewById(R.id.textViewOther);

        calculateEachMonthExpense();
        calculateEachMonthExpenseForCategory("Shopping", mTextViewShoppingExpense);
        calculateEachMonthExpenseForCategory("Travel", mTextViewTravelExpense);
        calculateEachMonthExpenseForCategory("Food", mTextViewFoodExpense);
        calculateEachMonthExpenseForCategory("Bills", mTextViewBillsExpense);
        calculateEachMonthExpenseForCategory("Medical", mTextViewMedicalExpense);
        calculateEachMonthExpenseForCategory("Other", mTextViewOtherExpense);


    }

    private void calculateEachMonthExpense() {
        mDatabase.orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //set time to 1970/1/2
                Calendar lastMonthCalendar;
                lastMonthCalendar = Calendar.getInstance();
                lastMonthCalendar.setTimeInMillis(66600000);

                int i = 0;

                double accurateLastMonthExpense = 0;
                Date date = new Date();
                Calendar accurate = Calendar.getInstance();
                accurate.setTimeInMillis(date.getTime());

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Expense expense = snapshot.getValue(Expense.class);

                    long timeInMilli = expense.timestamp; //setting to this month
                    Calendar calendar = Calendar.getInstance(); //setting to this month
                    calendar.setTimeInMillis(timeInMilli); //setting to this month

                    if (accurate.get(Calendar.MONTH) > calendar.get(Calendar.MONTH) && accurate.get(Calendar.MONTH) - 2 < calendar.get(Calendar.MONTH))
                        accurateLastMonthExpense += expense.amount;

                    if (calendar.get(Calendar.MONTH) == lastMonthCalendar.get(Calendar.MONTH) && calendar.get(Calendar.YEAR) == lastMonthCalendar.get(Calendar.YEAR)) {
                        double lastValue = 0;
                        lastValue = monthExpenseHash.get(i);
                        monthExpenseHash.put(i, lastValue + expense.amount);
                        lastMonthCalendar.setTimeInMillis(expense.timestamp);
                    } else {
                        i++;
                        monthExpenseHash.put(i, expense.amount);
                        lastMonthCalendar.setTimeInMillis(expense.timestamp);
                    }
                }

                List<Double> listToForcast = new ArrayList<Double>();
                for (Map.Entry<Integer, Double> key : monthExpenseHash.entrySet()) {
                    listToForcast.add(key.getValue());
                }

                DecimalFormat decimalFormat = new DecimalFormat("#,###,###,###.00");
                if (forecast(listToForcast) > 0)
                    mTextViewResult.setText(decimalFormat.format(forecast(listToForcast)));

                if (accurateLastMonthExpense > 0)
                    mLastMonthExpense.setText(decimalFormat.format(accurateLastMonthExpense));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                MessageOutput.showSnackbarLongDuration(ForecastActivity.this, databaseError.getMessage());
            }
        });
    }


    private void calculateEachMonthExpenseForCategory(final String categoryToCalculate, final TextView textView) {

        mDatabase.orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                HashMap<Integer, Double> tempHash = new HashMap<Integer, Double>();

                List<DataSnapshot> dataSnapshotList = new ArrayList<DataSnapshot>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child("category").getValue().equals(categoryToCalculate))
                        dataSnapshotList.add(snapshot);
                }

                //set time to 1970/1/2
                Calendar lastMonthCalendar;
                lastMonthCalendar = Calendar.getInstance();
                lastMonthCalendar.setTimeInMillis(66600000);

                int i = 0;

                for (DataSnapshot snapshot : dataSnapshotList) {
                    Expense expense = snapshot.getValue(Expense.class);

                    long timeInMilli = expense.timestamp; //setting to this month
                    Calendar calendar = Calendar.getInstance(); //setting to this month
                    calendar.setTimeInMillis(timeInMilli); //setting to this month

                    if (calendar.get(Calendar.MONTH) == lastMonthCalendar.get(Calendar.MONTH) && calendar.get(Calendar.YEAR) == lastMonthCalendar.get(Calendar.YEAR)) {
                        double lastValue = 0;
                        lastValue = tempHash.get(i);
                        tempHash.put(i, lastValue + expense.amount);
                        lastMonthCalendar.setTimeInMillis(expense.timestamp);
                    } else {
                        i++;
                        tempHash.put(i, expense.amount);
                        lastMonthCalendar.setTimeInMillis(expense.timestamp);
                    }
                }

                List<Double> listToForcast = new ArrayList<Double>();
                for (Map.Entry<Integer, Double> key : tempHash.entrySet()) {
                    listToForcast.add(key.getValue());
                }

                DecimalFormat decimalFormat = new DecimalFormat("#,###,###,###.00");
                if (listToForcast.size() > 0) {
                    textView.setText(decimalFormat.format(forecast(listToForcast)));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                MessageOutput.showSnackbarLongDuration(ForecastActivity.this, databaseError.getMessage());
            }
        });
    }

    public double forecast(List<Double> e) {

        double forecast = 0;

        if (e.size() == 1) {
            for (double d : e)
                forecast += d;
            return forecast;
        } else if (e.size() == 2) {
            for (double d : e)
                forecast += d;
            return forecast / 2;
        } else if (e.size() >= 3) {

            double a = 0;
            double b = 0;

            int n = e.size();

            int X = e.size();
            int sigmaX = 0;

//            double Y = e.get(e.size() - 1); //lets assume last value of the array is this month expense
            double sigmaY = 0;

            double sigmaXY = 0;

            for (int i = 1; i <= e.size(); i++) {
                sigmaX += i;
            }

            for (double d : e) {
                sigmaY += d;
            }

            for (int i = 0; i <= e.size() - 1; i++) {
                sigmaXY += (i + 1) * e.get(i);
            }


            b = (((n * sigmaXY) - (sigmaX * sigmaY)) / ((n * (Math.pow(sigmaX, 2))) - (Math.pow(sigmaX, 2))));
            a = ((sigmaY / n) - (b * (sigmaX / n)));
            forecast = a + (b * X);
            return forecast;
        } else {
            return 0;
        }
    }


}
