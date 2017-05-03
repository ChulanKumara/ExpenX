package com.expenx.expenx.activity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.expenx.expenx.R;
import com.expenx.expenx.core.MessageOutput;
import com.expenx.expenx.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    ViewGroup mLoginMainRelativeLayout;

    ImageView mCircleAroundE;

    EditText mEmailText, mPasswordText;

    TextView mExpenxText, mEText, mOrViaEmailText, mForgotPasswordText, mCreateAccountText, mAllRightsText;

    Button mLoginButton, mLoginGoogleButton;

    public FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    public DatabaseReference databaseReference;

    private String TAG = "expenxtag";

    SharedPreferences preferences = null;
    SharedPreferences.Editor editor = null;

    private static boolean isExpenxActivityLaunched = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);


        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                    editor = preferences.edit();
                    editor.putString("uid", user.getUid());
                    editor.putString("email", user.getEmail());
                    editor.apply();

                    if(!isExpenxActivityLaunched) {
                        startActivity(new Intent(LoginActivity.this, ExpenxActivity.class));
                        isExpenxActivityLaunched = true;
                    }
                    LoginActivity.this.finish();
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };


        databaseReference = FirebaseDatabase.getInstance().getReference();

        mLoginMainRelativeLayout = (ViewGroup) findViewById(R.id.activity_login);

        mEmailText = (EditText) findViewById(R.id.editTextEmail);
        mPasswordText = (EditText) findViewById(R.id.editTextPassword);

        mLoginButton = (Button) findViewById(R.id.buttonLogin);
        mLoginGoogleButton = (Button) findViewById(R.id.buttonLoginGoogle);

        mEText = (TextView) findViewById(R.id.textViewE);
        mExpenxText = (TextView) findViewById(R.id.textViewExpenx);
        mOrViaEmailText = (TextView) findViewById(R.id.textViewOrViaEmail);
        mForgotPasswordText = (TextView) findViewById(R.id.textViewForgotPassword);
        mCreateAccountText = (TextView) findViewById(R.id.textViewCreateAccount);
        mAllRightsText = (TextView) findViewById(R.id.textViewAllRightsLoginPage);

        mCircleAroundE = (ImageView) findViewById(R.id.circleAroundE);


        //animation -- start
        final RotateAnimation rotateAnimationCircle = new RotateAnimation(0, 90, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        rotateAnimationCircle.setRepeatCount(Animation.INFINITE);
        rotateAnimationCircle.setDuration(6000);
        rotateAnimationCircle.setInterpolator(new LinearInterpolator());
        rotateAnimationCircle.start();

        Animation scaleAnimationCircle = new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimationCircle.setDuration(500);
        scaleAnimationCircle.setInterpolator(new DecelerateInterpolator());
        scaleAnimationCircle.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                View[] Views = new View[]{mExpenxText, mEText};

                for (View v : Views) {
                    v.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mCircleAroundE.setAnimation(rotateAnimationCircle);

                View[] scaleInViews = new View[]{mExpenxText, mEText};

                for (View v : scaleInViews) {
                    Animation scale = new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    scale.setDuration(500);
                    scale.setInterpolator(new DecelerateInterpolator());
                    v.setAnimation(scale);
                    scale.start();
                    v.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mCircleAroundE.setAnimation(scaleAnimationCircle);
        scaleAnimationCircle.start();


        final View[] views = new View[]{mLoginGoogleButton, mOrViaEmailText, mEmailText, mPasswordText, mLoginButton, mForgotPasswordText, mCreateAccountText, mAllRightsText};

        long delayBetweenAnimations = 100l;

        for (final View view : views) {
            view.setVisibility(View.INVISIBLE);
        }

        for (int i = views.length - 1; i >= 0; i--) {
            final View view = views[i];

            long delay = i * delayBetweenAnimations;

            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Animation fadeInAnimation = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.fade_in_animation);
                    Animation translateAnimation1 = new TranslateAnimation(0, 0, 1000, 0);
                    translateAnimation1.setInterpolator(new AccelerateDecelerateInterpolator());
                    translateAnimation1.setDuration(1000);
                    translateAnimation1.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            view.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    view.startAnimation(fadeInAnimation);
                    view.setAnimation(translateAnimation1);
                    translateAnimation1.start();
                }
            }, delay);
        }

        //animation -- end

        mLoginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                signIn(mEmailText.getText().toString().trim(), mPasswordText.getText().toString().trim());
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailText.getText().toString();
        if (TextUtils.isEmpty(email)) {
            MessageOutput.showSnackbarLongDuration(LoginActivity.this, "Email required..!");
            valid = false;
        } else {
            mEmailText.setError(null);
        }

        String password = mPasswordText.getText().toString();
        if (TextUtils.isEmpty(password)) {
            MessageOutput.showSnackbarLongDuration(LoginActivity.this, "Password required..!");
            valid = false;
        } else {
            mPasswordText.setError(null);
        }

        return valid;
    }

    private void signIn(String email, String password) {
        if (!validateForm()) {
            return;
        }

        MessageOutput.showProgressDialog(LoginActivity.this, "Logging in...");

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        try {
                            if (!task.isSuccessful()) {
                                MessageOutput.showSnackbarLongDuration(LoginActivity.this, task.getException().getMessage());
                            }

                            if (task.isSuccessful()) {
                                editor = preferences.edit();
                                editor.putString("uid", mAuth.getCurrentUser().getUid());
                                editor.putString("email", mAuth.getCurrentUser().getEmail());
                                editor.apply();

                                if(!isExpenxActivityLaunched) {
                                    startActivity(new Intent(LoginActivity.this, ExpenxActivity.class));
                                    isExpenxActivityLaunched = true;
                                }
                                LoginActivity.this.finish();
                            }
                        } catch (NullPointerException e) {
                            MessageOutput.showSnackbarLongDuration(LoginActivity.this, "Something went wrong...!");
                        }
                        MessageOutput.dismissProgressDialog();
                    }
                });

    }
}
