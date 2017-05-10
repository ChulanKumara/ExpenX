package com.expenx.expenx.activity;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.CalendarView;

import com.expenx.expenx.R;
import com.expenx.expenx.core.CalendarDataModel;
import com.expenx.expenx.core.DataAdapterForCalendarRecycler;
import com.expenx.expenx.core.MessageOutput;
import com.expenx.expenx.model.BorrowFrom;
import com.expenx.expenx.model.Expense;
import com.expenx.expenx.model.Income;
import com.expenx.expenx.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CalendarViewActivity extends AppCompatActivity {

    CalendarView calendar;

    private long timeStamp;

    private ArrayList<CalendarDataModel> dataSet;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private FirebaseUser loggedUser = null;
    public User user = null;
    SharedPreferences sharedPreferences;

    RecyclerView.Adapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_view);
        dataSet = new ArrayList<>();
        //Check Fire Base User
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        calendar = (CalendarView) findViewById(R.id.calendarView);

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                dataSet.clear();
                String date = dayOfMonth + "/" + (month + 1) + "/" + year;
                convertDate(date);
//                loadLendTo(date);
                loadDebt(date);
                loadExpense();
                loadIncome();
                initViews();
            }
        });

        initViews();

    }


//    public void loadLendTo(String date) {
//
//        mDatabase.child("lendTo").child(sharedPreferences.getString("uid", null)).orderByChild("date").equalTo(date).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    LendTo lendTo = snapshot.getValue(LendTo.class);
//
//                    String type = "Lend From " + lendTo.lendFrom;
//                    String info = "Amout : " + lendTo.amount;
//                    CalendarDataModel dm = new CalendarDataModel(type, info);
//                    dataSet.add(dm);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                MessageOutput.showSnackbarLongDuration(CalendarViewActivity.this, databaseError.getMessage());
//            }
//        });
//
//    }

    public void loadExpense() {

        mDatabase.child("expense").child(sharedPreferences.getString("uid", null)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Expense expense = snapshot.getValue(Expense.class);

                    //System.out.println("_________________returnConvertDate(expense.timestamp)==timeStamp) " + returnConvertDate(expense.timestamp) + "==" + timeStamp);

                    if (returnConvertDate(expense.timestamp) == timeStamp) {
                        System.out.println("_________________Expense amount " + expense.amount);
                        String type = expense.category + " Expense";;
                        String info = "Amout : " + expense.amount;
                        CalendarDataModel dm = new CalendarDataModel(type, info);
                        dataSet.add(dm);
                        initViews();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                MessageOutput.showSnackbarLongDuration(CalendarViewActivity.this, databaseError.getMessage());
            }
        });

    }

    public void loadIncome() {

        mDatabase.child("income").child(sharedPreferences.getString("uid", null)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Income income = snapshot.getValue(Income.class);

                    //System.out.println("_________________before " + income.timestamp + " now returnConvertDate(income.timestamp)==timeStamp)" + returnConvertDate(income.timestamp) + "==" + timeStamp);

                    if (returnConvertDate(income.timestamp) == timeStamp) {
                        System.out.println("_______________income amount " + income.amount);
                        String type = income.category + " Income";
                        String info = "Amout : " + income.amount;
                        CalendarDataModel dm = new CalendarDataModel(type, info);
                        dataSet.add(dm);
                        initViews();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                MessageOutput.showSnackbarLongDuration(CalendarViewActivity.this, databaseError.getMessage());
            }
        });

    }

    public void loadDebt(String date) {

        mDatabase.child("debt").child(sharedPreferences.getString("uid", null)).orderByChild("date").equalTo(date).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    BorrowFrom borrowFrom = snapshot.getValue(BorrowFrom.class);

                    String type = "Debt";
                    String info = "Amout : " + borrowFrom.amount;
                    CalendarDataModel dm = new CalendarDataModel(type, info);
                    dataSet.add(dm);
                    initViews();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                MessageOutput.showSnackbarLongDuration(CalendarViewActivity.this, databaseError.getMessage());
            }
        });

    }

    //Convert Long TimeStamp To Standara Date
    private void convertDate(String dateStr) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date date = dateFormat.parse(dateStr);
            long time = (long) date.getTime() / 1000;
            timeStamp = time;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private long returnConvertDate(long dateLng) {
        long result = 0;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateLng);

        String dateStr = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) + "/" + String.valueOf(calendar.get(Calendar.MONTH) + 1) + "/" + String.valueOf(calendar.get(Calendar.YEAR));
        try {
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date date = dateFormat.parse(dateStr);
            result = (long) date.getTime() / 1000;
        } catch (Exception ex) {
            ex.printStackTrace();
            result = 0;
        }
        return result;
    }

    private void initViews() {

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.card_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        DataAdapterForCalendarRecycler adapter = new DataAdapterForCalendarRecycler(dataSet);
        recyclerView.setAdapter(adapter);

//        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
//            GestureDetector gestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {
//
//                @Override
//                public boolean onSingleTapUp(MotionEvent e) {
//                    return true;
//                }
//
//            });
//
//            @Override
//            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
//                return false;
//            }
//
//            @Override
//            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
//
//            }
//
//            @Override
//            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
//
//            }
//        });
    }
}
