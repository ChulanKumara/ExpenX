package com.expenx.expenx.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.expenx.expenx.R;
import com.expenx.expenx.core.DefaultCurrencyInitializer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SettingsActivity extends AppCompatActivity {

    Button mButtonChangePassword, mButtonSetDefaultCurrency, mButtonLogout;

    SharedPreferences preferences = null;
    SharedPreferences.Editor editor = null;

    private String defaultCurrency = "";

    private DatabaseReference mDatabase;
    FirebaseAuth mAuth;

    FirebaseUser userChnagePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        preferences = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);

        mAuth = FirebaseAuth.getInstance();


        mDatabase = FirebaseDatabase.getInstance().getReference().child("user");

        mButtonChangePassword = (Button) findViewById(R.id.buttonChangePassword);
        mButtonSetDefaultCurrency = (Button) findViewById(R.id.buttonSetDefaultCurrency);
        mButtonLogout = (Button) findViewById(R.id.buttonLogout);

        mButtonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editor = preferences.edit();
                editor.putString("uid", null);
                editor.putString("email", null);
                editor.apply();

                mAuth.signOut();

                finishAffinity();
                startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
            }
        });

        mButtonSetDefaultCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(SettingsActivity.this, android.R.style.DeviceDefault_ButtonBar_AlertDialog));
                builder.setTitle("Set Default Currency");

                final EditText input = new EditText(SettingsActivity.this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        defaultCurrency = input.getText().toString().toUpperCase();

                        int i = 0;
                        List<String> currencyList = DefaultCurrencyInitializer.initialize();

                        for (String curr : currencyList) {
                            if (curr.trim().contains(defaultCurrency)) {

                                FirebaseUser user = mAuth.getCurrentUser();
                                DatabaseReference currentUserDb = mDatabase.child(user.getUid());

                                HashMap<String, Object> updateUser = new HashMap<>();
                                updateUser.put("defaultCurrency", defaultCurrency);

                                currentUserDb.updateChildren(updateUser);

                                Toast.makeText(SettingsActivity.this, "Default currency set to " + defaultCurrency, Toast.LENGTH_LONG).show();

                                break;
                            }
                            i++;
                        }

                        if (i == currencyList.size())
                            Toast.makeText(SettingsActivity.this, "Invalid Input", Toast.LENGTH_LONG).show();

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        FirebaseUser user = mAuth.getCurrentUser();
        for (UserInfo s : user.getProviderData()) {
            if (s.getProviderId().equalsIgnoreCase("google.com")) {
                mButtonChangePassword.setVisibility(View.GONE);
            }
        }

        //change password
        mButtonChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(SettingsActivity.this, android.R.style.DeviceDefault_ButtonBar_AlertDialog));
                builder.setTitle("Enter Old Password");

                final EditText oldPassword = new EditText(SettingsActivity.this);
                oldPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                builder.setView(oldPassword);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        //enter new password
                        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(SettingsActivity.this, android.R.style.DeviceDefault_ButtonBar_AlertDialog));
                        builder.setTitle("Enter New Password");

                        final EditText newPassword = new EditText(SettingsActivity.this);
                        newPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        builder.setView(newPassword);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                //confirm new password
                                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(SettingsActivity.this, android.R.style.DeviceDefault_ButtonBar_AlertDialog));
                                builder.setTitle("Confirm New Password");

                                final EditText confirmedNewPassword = new EditText(SettingsActivity.this);
                                confirmedNewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD );
                                builder.setView(confirmedNewPassword);
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        if (newPassword.getText().toString().equals(confirmedNewPassword.getText().toString()) && checkPasswordStrength(confirmedNewPassword.getText().toString())) {


                                            //changing passwords from firebase

                                            userChnagePassword = mAuth.getCurrentUser();
                                            final String email = userChnagePassword.getEmail();
                                            AuthCredential credential = EmailAuthProvider.getCredential(email,oldPassword.getText().toString());

                                            Toast.makeText(SettingsActivity.this,"Please Wait", Toast.LENGTH_LONG).show();

                                            userChnagePassword.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){

                                                        userChnagePassword.updatePassword(newPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(!task.isSuccessful()){
                                                                    Toast.makeText(SettingsActivity.this,"Something went wrong. Please try again later", Toast.LENGTH_LONG).show();

                                                                }else {
                                                                    Toast.makeText(SettingsActivity.this,"Password Successfully Modified", Toast.LENGTH_LONG).show();
                                                                }
                                                            }
                                                        });
                                                    }else {
                                                        Toast.makeText(SettingsActivity.this,"Authentication Failed", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });

                                            //changing passwords from firebase


                                        }else{
                                            Toast.makeText(SettingsActivity.this,"Confirming passwords failed", Toast.LENGTH_LONG).show();
                                        }

                                    }
                                });
                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });

                                builder.show();
                                //confirm new password


                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        builder.show();
                        //enter new password


                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
    }

    private boolean checkPasswordStrength(String password) {

        boolean valid = true;

        Pattern pattern = Pattern.compile("[a-zA-Z0-9]*");
        Matcher matcher = pattern.matcher(password);

        if (password.length() < 6) {
            Toast.makeText(this, "Password should be more than 6 characters", Toast.LENGTH_LONG).show();
            valid = false;
        } else if (matcher.matches()) {
            Toast.makeText(this, "Password should have atleast one speacial character", Toast.LENGTH_LONG).show();
            valid = false;
        }
        return valid;
    }
}
