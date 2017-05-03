package com.expenx.expenx.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.expenx.expenx.R;
import com.expenx.expenx.core.MessageOutput;
import com.expenx.expenx.model.Expense;
import com.expenx.expenx.model.Income;
import com.expenx.expenx.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.Calendar;


public class ExpenxActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public User user = null;

    public DatabaseReference databaseReference;

    SharedPreferences sharedPreferences;

    TextView mMonthIncomeAmount;
    TextView mMonthExpenseAmount;
    TextView mCurrentBalanceAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenx);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mMonthIncomeAmount = (TextView) findViewById(R.id.textViewThisMonthIncomeAmount);
        mMonthExpenseAmount = (TextView) findViewById(R.id.textViewThisMonthExpenseAmount);
        mCurrentBalanceAmount = (TextView) findViewById(R.id.textViewCurrentBalanceAmount);

        mCurrentBalanceAmount.setText("0.00");

        Button mButtonChartView = (Button) findViewById(R.id.buttonChartView);
        mButtonChartView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ExpenxActivity.this, ChartViewActivity.class));
            }
        });

        Button mButtonCalendarView = (Button) findViewById(R.id.buttonCalendarView);
        mButtonCalendarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ExpenxActivity.this, CalendarViewActivity.class));
            }
        });

        Button mButtonIncome = (Button) findViewById(R.id.buttonIncome);
        mButtonIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ExpenxActivity.this, IncomeActivity.class));
            }
        });

        Button mButtonExpense = (Button) findViewById(R.id.buttonExpense);
        mButtonExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ExpenxActivity.this, ExpenseActivity.class));
            }
        });

        Button mButtonDebt = (Button) findViewById(R.id.buttonDebt);
        mButtonDebt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ExpenxActivity.this, DebtActivity.class));
            }
        });


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        databaseReference = FirebaseDatabase.getInstance().getReference();


        databaseReference.child("user").child(sharedPreferences.getString("uid", null)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();

                user = dataSnapshot.getValue(User.class);
                user.userUID = dataSnapshot.getKey();

                ((TextView) findViewById(R.id.nav_textViewUserName)).setText(user.fname + " " + user.lname);
                ((TextView) findViewById(R.id.nav_textViewUserEmail)).setText(sharedPreferences.getString("email", null));

                storageRef.child(user.profileImage).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        ImageView profilePic = (ImageView) findViewById(R.id.profile_image);
                        Picasso.with(ExpenxActivity.this).load(uri).into(profilePic);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        MessageOutput.showSnackbarLongDuration(ExpenxActivity.this, "Something went wrong while loading your profile image...!");
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                MessageOutput.showSnackbarLongDuration(ExpenxActivity.this, databaseError.getMessage());
            }
        });


        databaseReference.child("income").child(sharedPreferences.getString("uid", null)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                double todayIncome = 0;
                double thisWeekIncome = 0;
                double thisMonthIncome = 0;

                Calendar incomeTimestamp = Calendar.getInstance();

                Calendar nowTimestamp = Calendar.getInstance();
                nowTimestamp.setTimeInMillis(Calendar.getInstance().getTimeInMillis());

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Income income = snapshot.getValue(Income.class);

                    incomeTimestamp.setTimeInMillis(income.timestamp);

                    if (incomeTimestamp.get(Calendar.DATE) == nowTimestamp.get(Calendar.DATE) && incomeTimestamp.get(Calendar.MONTH) == nowTimestamp.get(Calendar.MONTH) && incomeTimestamp.get(Calendar.YEAR) == nowTimestamp.get(Calendar.YEAR)) {
                        todayIncome += income.amount;
                    }

                    if (incomeTimestamp.get(Calendar.WEEK_OF_YEAR) == nowTimestamp.get(Calendar.WEEK_OF_YEAR) && incomeTimestamp.get(Calendar.YEAR) == nowTimestamp.get(Calendar.YEAR)) {
                        thisWeekIncome += income.amount;
                    }

                    if (incomeTimestamp.get(Calendar.MONTH) == nowTimestamp.get(Calendar.MONTH) && incomeTimestamp.get(Calendar.YEAR) == nowTimestamp.get(Calendar.YEAR)) {
                        thisMonthIncome += income.amount;
                    }
                }

                ((TextView) findViewById(R.id.textViewTodayIncomeAmount)).setText(new DecimalFormat("#.00").format(todayIncome));
                ((TextView) findViewById(R.id.textViewThisWeekIncomeAmount)).setText(new DecimalFormat("#.00").format(thisWeekIncome));
                ((TextView) findViewById(R.id.textViewThisMonthIncomeAmount)).setText(new DecimalFormat("#.00").format(thisMonthIncome));

                if (todayIncome == 0)
                    ((TextView) findViewById(R.id.textViewTodayIncomeAmount)).setText("0.00");
                if (thisWeekIncome == 0)
                    ((TextView) findViewById(R.id.textViewThisWeekIncomeAmount)).setText("0.00");
                if (thisMonthIncome == 0)
                    ((TextView) findViewById(R.id.textViewThisMonthIncomeAmount)).setText("0.00");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        databaseReference.child("expense").child(sharedPreferences.getString("uid", null)).addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                double todayExpense = 0;
                double thisWeekExpense = 0;
                double thisMonthExpense = 0;

                Calendar incomeTimestamp = Calendar.getInstance();

                Calendar nowTimestamp = Calendar.getInstance();
                nowTimestamp.setTimeInMillis(Calendar.getInstance().getTimeInMillis());

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Expense expense = snapshot.getValue(Expense.class);

                    incomeTimestamp.setTimeInMillis(expense.timestamp);

                    if (incomeTimestamp.get(Calendar.DATE) == nowTimestamp.get(Calendar.DATE) && incomeTimestamp.get(Calendar.MONTH) == nowTimestamp.get(Calendar.MONTH) && incomeTimestamp.get(Calendar.YEAR) == nowTimestamp.get(Calendar.YEAR)) {
                        todayExpense += expense.amount;
                    }

                    if (incomeTimestamp.get(Calendar.WEEK_OF_YEAR) == nowTimestamp.get(Calendar.WEEK_OF_YEAR) && incomeTimestamp.get(Calendar.YEAR) == nowTimestamp.get(Calendar.YEAR)) {
                        thisWeekExpense += expense.amount;
                    }

                    if (incomeTimestamp.get(Calendar.MONTH) == nowTimestamp.get(Calendar.MONTH) && incomeTimestamp.get(Calendar.YEAR) == nowTimestamp.get(Calendar.YEAR)) {
                        thisMonthExpense += expense.amount;
                    }
                }

                ((TextView) findViewById(R.id.textViewTodayExpenseAmount)).setText(new DecimalFormat("#.00").format(todayExpense));
                ((TextView) findViewById(R.id.textViewThisWeekExpenseAmount)).setText(new DecimalFormat("#.00").format(thisWeekExpense));
                ((TextView) findViewById(R.id.textViewThisMonthExpenseAmount)).setText(new DecimalFormat("#.00").format(thisMonthExpense));

                if (todayExpense == 0)
                    ((TextView) findViewById(R.id.textViewTodayExpenseAmount)).setText("0.00");
                if (thisWeekExpense == 0)
                    ((TextView) findViewById(R.id.textViewThisWeekExpenseAmount)).setText("0.00");
                if (thisMonthExpense == 0)
                    ((TextView) findViewById(R.id.textViewThisMonthExpenseAmount)).setText("0.00");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //setting current balance
        mMonthExpenseAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String monthIncome = mMonthIncomeAmount.getText().toString();
                String monthExpense = mMonthExpenseAmount.getText().toString();

                double currentBalance = 0;

                if (!monthExpense.trim().equals("") && !monthIncome.trim().equals(""))
                    currentBalance = Double.parseDouble(monthIncome) - Double.parseDouble(monthExpense);

                mCurrentBalanceAmount.setText(new DecimalFormat("#.00").format(currentBalance));

                if (currentBalance == 0)
                    mCurrentBalanceAmount.setText("0.00");
            }
        });

        //setting current balance
        mMonthIncomeAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String monthIncome = mMonthIncomeAmount.getText().toString();
                String monthExpense = mMonthExpenseAmount.getText().toString();

                double currentBalance = 0;

                if (!monthExpense.trim().equals("") && !monthIncome.trim().equals(""))
                    currentBalance = Double.parseDouble(monthIncome) - Double.parseDouble(monthExpense);

                mCurrentBalanceAmount.setText(new DecimalFormat("#.00").format(currentBalance));

                if (currentBalance == 0)
                    mCurrentBalanceAmount.setText("0.00");
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_income) {
            startActivity(new Intent(ExpenxActivity.this, IncomeActivity.class));
        } else if (id == R.id.nav_expense) {
            startActivity(new Intent(ExpenxActivity.this, ExpenseActivity.class));
        } else if (id == R.id.nav_debt) {
            startActivity(new Intent(ExpenxActivity.this, DebtActivity.class));
        } else if (id == R.id.nav_chart_view) {
            startActivity(new Intent(ExpenxActivity.this, ChartViewActivity.class));
        } else if (id == R.id.nav_calendar_view) {
            startActivity(new Intent(ExpenxActivity.this, CalendarViewActivity.class));
        } else if (id == R.id.nav_notes) {
            startActivity(new Intent(ExpenxActivity.this, NotesActivity.class));
        } else if (id == R.id.nav_forecast) {
            startActivity(new Intent(ExpenxActivity.this, ForecastActivity.class));
        } else if (id == R.id.nav_loan_calculator) {
            startActivity(new Intent(ExpenxActivity.this, LoanCalculatorActivity.class));
        } else if (id == R.id.nav_currency_converter) {
            startActivity(new Intent(ExpenxActivity.this, CurrencyConverterActivity.class));
        } else if (id == R.id.nav_reminder) {
            startActivity(new Intent(ExpenxActivity.this, ReminderActivity.class));
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(ExpenxActivity.this, SettingsActivity.class));
        } else if (id == R.id.nav_about) {
            startActivity(new Intent(ExpenxActivity.this, AboutActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
