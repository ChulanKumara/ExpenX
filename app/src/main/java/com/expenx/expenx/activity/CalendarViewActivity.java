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
import com.expenx.expenx.core.CurrentDateDecorator;
import com.expenx.expenx.core.DataAdapterForCalendarRecycler;
import com.expenx.expenx.core.EventDateDecorator;
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
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.format.DateFormatDayFormatter;
import com.prolificinteractive.materialcalendarview.format.DayFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.prolificinteractive.materialcalendarview.R.id.month;

public class CalendarViewActivity extends AppCompatActivity {

    CalendarView calendar;
    MaterialCalendarView materialCalendarView;

    private long timeStamp;

    private ArrayList<CalendarDataModel> dataSet;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private FirebaseUser loggedUser = null;
    public User user = null;
    SharedPreferences sharedPreferences;

    RecyclerView.Adapter adapter;

    boolean incomeFinishedLoading = false;
    boolean expenseFinishedLoading = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_view);
        dataSet = new ArrayList<>();
        //Check Fire Base User
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);


        //adding material calendar view
        materialCalendarView = (MaterialCalendarView) findViewById(R.id.calendarView);

        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.MONDAY)
                .setMinimumDate(CalendarDay.from(2016, 1, 1))
                .setMaximumDate(CalendarDay.from(2026, 1, 1))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        materialCalendarView.addDecorator(new CurrentDateDecorator(this));


        //highlighting event dates on the calendar
        //this only runs only on create
        loadExpense();
        loadIncome();


        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                dataSet.clear();
                String newDate = date.getDay() + "/" + (date.getMonth() + 1) + "/" + date.getYear();
                convertDate(newDate);
//                loadLendTo(date);
//                loadDebt(date);
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

                long dataSnapshopCount = dataSnapshot.getChildrenCount();
                long i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Expense expense = snapshot.getValue(Expense.class);

                    if (!expenseFinishedLoading) { //highlighting event dates on calendar
                        Calendar expenseTimestamp = Calendar.getInstance();
                        expenseTimestamp.setTimeInMillis(expense.timestamp);

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(expenseTimestamp.get(Calendar.YEAR), expenseTimestamp.get(Calendar.MONTH), expenseTimestamp.get(Calendar.DAY_OF_MONTH));
                        materialCalendarView.addDecorator(new EventDateDecorator(CalendarViewActivity.this, CalendarDay.from(calendar)));

                        if (dataSnapshopCount == i)
                            expenseFinishedLoading = true;

                        i++;
                    }

                    if (returnConvertDate(expense.timestamp) == timeStamp) {
                        String type = expense.category + " Expense";
                        String info = "Amout : " + expense.amount;
                        CalendarDataModel dm = new CalendarDataModel(type, info, expense.description);
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

                long dataSnapshopCount = dataSnapshot.getChildrenCount();
                long i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Income income = snapshot.getValue(Income.class);

                    if (!incomeFinishedLoading) {//highlighting event dates on calendar
                        Calendar incomeTimestamp = Calendar.getInstance();
                        incomeTimestamp.setTimeInMillis(income.timestamp);

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(incomeTimestamp.get(Calendar.YEAR), incomeTimestamp.get(Calendar.MONTH), incomeTimestamp.get(Calendar.DAY_OF_MONTH));
                        materialCalendarView.addDecorator(new EventDateDecorator(CalendarViewActivity.this, CalendarDay.from(calendar)));

                        if (dataSnapshopCount == i)
                            incomeFinishedLoading = true;

                        i++;
                    }

                    if (returnConvertDate(income.timestamp) == timeStamp) {
                        String type = income.category + " Income";
                        String info = "Amout : " + income.amount;
                        CalendarDataModel dm = new CalendarDataModel(type, info, income.description);
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
                    CalendarDataModel dm = new CalendarDataModel(type, info, borrowFrom.description);
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
