package com.expenx.expenx.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.expenx.expenx.R;
import com.expenx.expenx.core.MessageOutput;
import com.expenx.expenx.model.BorrowFrom;
import com.expenx.expenx.model.Expense;
import com.expenx.expenx.model.Income;
import com.expenx.expenx.model.User;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Haritha on 12/05/2017.
 */

public class ChartViewActivity extends AppCompatActivity {

    private RelativeLayout chartLayout;

    private int expenseAmount = 0;
    private int incomeAmount = 0;
    private int borrowedAmount = 0;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private DatabaseReference mDatabase;
    private FirebaseUser loggedUser = null;
    public User user = null;
    SharedPreferences sharedPreferences;

    PieChart pieChart;
    PieData pieData;
    PieDataSet pieDataSet;

    private ArrayList<String> xVal;
    private ArrayList<Entry> yVal;
    private  ArrayList<Integer> colors;

    private int[] yData = new int[3];
    private String[] xData = {"Expense", "Income", "Debt"};

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_view_pie);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        chartLayout = (RelativeLayout) findViewById(R.id.pieChartLayout);
        pieChart = (PieChart) findViewById(R.id.pieChart);

        Button barChartView = (Button) findViewById(R.id.switchToBarChart);
        barChartView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChartViewActivity.this, ChartViewActivity2.class));
                ChartViewActivity.this.overridePendingTransition(0, 0);
                finish();
            }
        });

        loadExpenseData();
        loadIncomeData();
        loadDebtData();

    }

    private void loadExpenseData() {

        mDatabase.child("expense").child(sharedPreferences.getString("uid", null)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Expense expense = snapshot.getValue(Expense.class);
                    expenseAmount += (int) expense.amount;
                }
                yData[0] = expenseAmount;
                initView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                MessageOutput.showSnackbarLongDuration(ChartViewActivity.this, databaseError.getMessage());
            }
        });
    }

    private void loadIncomeData() {

        mDatabase.child("income").child(sharedPreferences.getString("uid", null)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Income income = snapshot.getValue(Income.class);
                    incomeAmount += (int)income.amount;
                }
                yData[1] = incomeAmount;
                initView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                MessageOutput.showSnackbarLongDuration(ChartViewActivity.this, databaseError.getMessage());
            }
        });
    }

    private void loadDebtData() {

        mDatabase.child("debt").child(sharedPreferences.getString("uid", null)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    BorrowFrom borrowFrom = snapshot.getValue(BorrowFrom.class);
                    borrowedAmount += (int)borrowFrom.amount;
                }
                yData[2] = borrowedAmount;
                initView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                MessageOutput.showSnackbarLongDuration(ChartViewActivity.this, databaseError.getMessage());
            }
        });

    }

    private void initView() {

        pieChart.setUsePercentValues(true);

        yVal = new ArrayList<Entry>();
        for (int i = 0; i < yData.length; i++) {
            yVal.add(new Entry(yData[i], i));
        }

        xVal = new ArrayList<String>();
        for (int i = 0; i < xData.length; i++) {
            xVal.add(xData[i]);
        }

        colors = new ArrayList<>();
        colors.add(Color.rgb(255, 14, 81));
        colors.add(Color.rgb(14, 255, 104));
        colors.add(Color.rgb(232, 77, 255));

        Legend legend = pieChart.getLegend();
        legend.setCustom(new int[]{Color.rgb(255, 14, 81), Color.rgb(14, 255, 104),Color.rgb(232, 77, 255)},
                new String[]{"Expense", "Income", "Debt"});
        legend.setTextColor(Color.rgb(255, 255, 255));
        legend.setTextSize(13f);

        pieDataSet = new PieDataSet(yVal, "");
        pieChart.setDescription("");

        pieDataSet.setColors(colors);
        pieDataSet.setSliceSpace(1);

        pieData = new PieData(xVal, pieDataSet);
        pieData.setValueFormatter(new PercentFormatter());
        pieData.setValueTextSize(16f);
        pieData.setValueTextColor(Color.WHITE);

        pieChart.setData(pieData);

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(45);
        pieChart.setTransparentCircleRadius(15);
        pieChart.setHoleColor(Color.parseColor("#2f3043"));
        pieChart.setHovered(true);
        pieChart.animateX(2000);
        pieChart.animateY(2000);
        pieChart.invalidate();

    }


}
