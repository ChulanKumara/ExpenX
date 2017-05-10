package com.expenx.expenx.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.expenx.expenx.R;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity {

    Button mButtonChangePassword, mButtonSetDefaultCurrency, mButtonLogout;

    SharedPreferences preferences = null;
    SharedPreferences.Editor editor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        preferences = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);

        mButtonChangePassword  = (Button) findViewById(R.id.buttonChangePassword);
        mButtonSetDefaultCurrency = (Button) findViewById(R.id.buttonSetDefaultCurrency);
        mButtonLogout = (Button) findViewById(R.id.buttonLogout);

        mButtonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editor = preferences.edit();
                editor.putString("uid", null);
                editor.putString("email", null);
                editor.apply();

                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();

                finishAffinity();
                startActivity(new Intent(SettingsActivity.this,LoginActivity.class));
            }
        });
    }
}
