package com.expenx.expenx.activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.expenx.expenx.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText mEditTextForgotEmail;
    Button mButtonResetForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mEditTextForgotEmail = (EditText) findViewById(R.id.editTextForgotEmail);
        mButtonResetForgotPassword = (Button) findViewById(R.id.buttonResetForgotPassword);


        mButtonResetForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                String emailAddress = mEditTextForgotEmail.getText().toString().trim();

                if (TextUtils.isEmpty(emailAddress)) {
                    Toast.makeText(ForgotPasswordActivity.this, "Email required", Toast.LENGTH_LONG).show();
                } else if (!isValidEmailAddress(emailAddress)) {
                    Toast.makeText(ForgotPasswordActivity.this, "Invalid email", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Please wait", Toast.LENGTH_LONG).show();
                    auth.sendPasswordResetEmail(emailAddress)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        ForgotPasswordActivity.this.finish();
                                        Toast.makeText(ForgotPasswordActivity.this, "Check your E-mail inbox", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(ForgotPasswordActivity.this, "Something went wrong! Email may not exists", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }

            }
        });
    }

    public static boolean isValidEmailAddress(String email) {
        final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return matcher.find();
    }
}
