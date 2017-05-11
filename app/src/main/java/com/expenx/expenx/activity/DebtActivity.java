package com.expenx.expenx.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.expenx.expenx.R;
import com.expenx.expenx.core.MessageOutput;
import com.expenx.expenx.model.BorrowFrom;
import com.expenx.expenx.model.Expense;
import com.expenx.expenx.model.Income;
import com.expenx.expenx.model.LendTo;
import com.expenx.expenx.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DebtActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    public FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String TAG = "expenxtag";
    SharedPreferences sharedPreferences;
    final Context context = this;
    double credit,balance,debt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debt);


        final TextView txt_balance=(TextView)findViewById(R.id.text_balance) ;
        final TextView txt_credit=(TextView)findViewById(R.id.text_credit) ;
        final TextView txt_debit=(TextView)findViewById(R.id.text_debt) ;

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    SharedPreferences preferences = null;
                    SharedPreferences.Editor editor = null;
                    editor = preferences.edit();
                    editor.putString("uid", user.getUid());
                    editor.putString("email", user.getEmail());
                    editor.apply();

                    startActivity(new Intent(DebtActivity.this, ExpenxActivity.class));
                    DebtActivity.this.finish();
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        Button lendtobtn =(Button)findViewById(R.id.lend_to_button);
        Button borrowbtn =(Button)findViewById(R.id.button_borrow);
        //lend to activity
        lendtobtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(DebtActivity.this,LendToActivity.class);
                        startActivity(intent);
                    }
                }
        );

        //borrowfrom activity
        borrowbtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(DebtActivity.this,BorrowFromActivity.class);
                        startActivity(intent);
                    }
                }
        );

        //retrieve data from firebase
        mDatabase = (DatabaseReference) FirebaseDatabase.getInstance().getReference().child("debt").child(mAuth.getCurrentUser().getUid());
        mDatabase.addValueEventListener(new ValueEventListener() {

            ListView lv =(ListView) findViewById(R.id.listView) ;
            List<LendTo> items= new ArrayList<LendTo>();
            DebtArrayAdapter adapter;

            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                items.clear();


                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    final LendTo lend = snapshot.getValue(LendTo.class);
                    final String uid = snapshot.getKey();
                    lend.pushId=uid;
                    items.add(lend);
                    adapter = new DebtArrayAdapter(DebtActivity.this, items);
                    lv.setAdapter(adapter);
                    registerForContextMenu(lv);
                    //when click on row
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            final LendTo listItem = (LendTo) lv.getItemAtPosition(position);
                            final Dialog dialog = new Dialog(context);

                            dialog.setContentView(R.layout.custom);
                            dialog.setTitle("Title...");

                            //custom dialog popup
                            TextView text = (TextView) dialog.findViewById(R.id.text);
                            Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonDelete);
                            EditText custom_name=(EditText)dialog.findViewById(R.id.custom_name) ;
                            EditText custom_amount=(EditText)dialog.findViewById(R.id.custom_amount);
                            EditText custom_date=(EditText)dialog.findViewById(R.id.custom_date);
                            EditText custom_due_date=(EditText)dialog.findViewById(R.id.custom_due_date);
                            EditText custom_ref=(EditText)dialog.findViewById(R.id.custom__ref);
                            EditText custom_desc=(EditText)dialog.findViewById(R.id.custom_desc);
                            Spinner custom_spinner=(Spinner)dialog.findViewById(R.id.custom_spinner_payment) ;

                            custom_name.setText(listItem.name);
                            custom_amount.setText(""+listItem.amount);
                            custom_date.setText(getDate(listItem.date));
                            custom_due_date.setText(getDate(listItem.dueDate));
                            custom_desc.setText(listItem.description);
                            custom_ref.setText(listItem.refCheckNo);



                            dialog.show();
                            //delete data
                            dialogButton.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    mDatabase.child(listItem.pushId).removeValue();
                                    Toast.makeText(DebtActivity.this,"Delete Successful",Toast.LENGTH_LONG).show();
                                    adapter.notifyDataSetChanged();
                                    dialog.dismiss();
                                }


                            });
                        }
                    });
                }
                balance=0;
                credit=0;
                debt=0;
                if(!items.isEmpty()){
                    for(int i=0;i<lv.getAdapter().getCount();i++){
                        final LendTo lnd = (LendTo) lv.getItemAtPosition(i);

                        if(lnd.type.equals("lend")){
                            debt=debt+lnd.amount;

                        }
                        if(lnd.type.equals("borrow")){
                            credit=credit+lnd.amount;

                        }
                        balance=credit-debt;
                    }}
                txt_balance.setText(""+balance);
                txt_credit.setText(""+credit);
                txt_debit.setText(""+debt);



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });
    }


    public void onClickLendButton(){
        //your code go here...
        Intent intent = new Intent(this, LendToActivity.class);
        startActivity(intent);//you can start the new activity at any time with this line
    }
    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("dd/MM/yyyy", cal).toString();
        return date;
    }
}
