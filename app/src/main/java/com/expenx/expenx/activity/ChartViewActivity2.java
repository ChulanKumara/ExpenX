package com.expenx.expenx.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.expenx.expenx.R;
import com.expenx.expenx.core.MessageOutput;
import com.expenx.expenx.model.BorrowFrom;
import com.expenx.expenx.model.Expense;
import com.expenx.expenx.model.Income;
import com.expenx.expenx.model.User;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by c2k on 13/05/2017.
 */

public class ChartViewActivity2 extends AppCompatActivity {

    private List<BarEntry> entries;

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

    BarDataSet set;
    BarChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_view_bar);
        entries = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        chartLayout = (RelativeLayout) findViewById(R.id.barchartLayout);
        chart = (BarChart) findViewById(R.id.barchart);

        Button pieChartView = (Button) findViewById(R.id.switchToPieChart);
        pieChartView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChartViewActivity2.this, ChartViewActivity.class));
                ChartViewActivity2.this.overridePendingTransition(0, 0);
                finish();
            }
        });

        loadExpenseData();
        loadIncomeData();
        loadDebtData();
    }

    public void loadExpenseData() {

        mDatabase.child("expense").child(sharedPreferences.getString("uid", null)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Expense expense = snapshot.getValue(Expense.class);
                    expenseAmount += (int) expense.amount;
                }
                entries.add(new BarEntry(expenseAmount, 0));
                initView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                MessageOutput.showSnackbarLongDuration(ChartViewActivity2.this, databaseError.getMessage());
            }
        });
    }

    public  void loadIncomeData(){

        mDatabase.child("income").child(sharedPreferences.getString("uid", null)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Income income = snapshot.getValue(Income.class);
                    incomeAmount += (int)income.amount;
                }
                entries.add(new BarEntry(incomeAmount, 1));
                initView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                MessageOutput.showSnackbarLongDuration(ChartViewActivity2.this, databaseError.getMessage());
            }
        });

    }


    public void loadDebtData() {
        mDatabase.child("debt").child(sharedPreferences.getString("uid", null)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    BorrowFrom borrowFrom = snapshot.getValue(BorrowFrom.class);
                    borrowedAmount += (int)borrowFrom.amount;
                }
                entries.add(new BarEntry(borrowedAmount, 2));
                initView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                MessageOutput.showSnackbarLongDuration(ChartViewActivity2.this, databaseError.getMessage());
            }
        });
    }

    public  void initView(){

        Legend legend = chart.getLegend();
        legend.setCustom(new int[]{Color.rgb(255, 14, 81), Color.rgb(14, 255, 104), Color.rgb(232, 77, 255)},
                new String[]{"Expense", "Income", "Debt"});
        legend.setTextColor(Color.rgb(255, 255, 255));

        set = new BarDataSet(entries, "Bar");
        set.setHighlightEnabled(false);
        set.setValueTextColor(Color.rgb(255, 255, 255));

        BarData data = new BarData();
        set.setColors(new int[]{Color.rgb(255, 14, 81), Color.rgb(14, 255, 104), Color.rgb(232, 77, 255)});
        data.addDataSet(set);

        chart.setData(data);
        chart.setVisibleXRangeMinimum(entries.size());
        chart.animateX(2000);
        chart.animateY(2000);
        chart.getAxisRight().setEnabled(false);
        chart.setDescription("");
        chart.getAxisLeft().setTextColor(Color.rgb(255, 255, 255));
        chart.invalidate();

    }

}
