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
import android.widget.Toast;

import com.expenx.expenx.R;
import com.expenx.expenx.core.MessageOutput;
import com.expenx.expenx.model.Reminder;
import com.expenx.expenx.model.User;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener {

    ViewGroup mLoginMainRelativeLayout;

    ImageView mCircleAroundE;

    EditText mEmailText, mPasswordText;

    TextView mExpenxText, mEText, mOrViaEmailText, mForgotPasswordText, mCreateAccountText, mAllRightsText;

    Button mLoginButton, mLoginGoogleButton;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    public DatabaseReference databaseReference;

    SharedPreferences preferences = null;
    SharedPreferences.Editor editor = null;

    int dontListenToAuthListener;

    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;

    private GoogleApiClient mGoogleApiClient;

    private DatabaseReference mDatabase;


    public static boolean isExpenxActivityLaunched = false;

    String googleName;
    String googleEmail;
    String googleImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("user");

        dontListenToAuthListener = -1;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            dontListenToAuthListener = extras.getInt("dontListenToAuthListener");
        }


        //google sign-in
        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        //google sign-in


        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null && dontListenToAuthListener == -1) {
                    // User is signed in

                    editor = preferences.edit();
                    editor.putString("uid", user.getUid());
                    editor.putString("email", user.getEmail());
                    editor.apply();

                    if (!isExpenxActivityLaunched) {
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

        mLoginGoogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInGoogle();
            }
        });

        mCreateAccountText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        mForgotPasswordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,ForgotPasswordActivity.class));
            }
        });
    }

    // [START onactivityresult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();

                googleName = account.getDisplayName();
                googleEmail = account.getEmail();
                googleImageUrl = account.getPhotoUrl().toString();

                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
            }
        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = mAuth.getCurrentUser();

                            DatabaseReference currentUserDb = mDatabase.child(user.getUid());

                            currentUserDb.orderByKey().equalTo(user.getUid()).addValueEventListener(new ValueEventListener(){

                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(!dataSnapshot.exists()) {
                                        //First time registering a user from google
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        DatabaseReference currentUserDb = mDatabase.child(user.getUid());
                                        Reminder defaultReminder = new Reminder("weekly", true, 14938269444L);
                                        User userToDb = new User(googleName, "", googleImageUrl, "USD", defaultReminder);
                                        currentUserDb.setValue(userToDb);

                                    } else {
                                        //Already registered user
                                        //Just update the name and profile image
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        DatabaseReference currentUserDb = mDatabase.child(user.getUid());

                                        HashMap<String, Object> updateUser = new HashMap<>();
                                        updateUser.put("fname", googleName);
                                        updateUser.put("profileImage", googleImageUrl);

                                        currentUserDb.updateChildren(updateUser);

                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                            editor = preferences.edit();
                            editor.putString("uid", mAuth.getCurrentUser().getUid());
                            editor.putString("email", mAuth.getCurrentUser().getEmail());
                            editor.apply();

                            LoginActivity.this.finish();
                            startActivity(new Intent(LoginActivity.this, ExpenxActivity.class));
                            isExpenxActivityLaunched = true;

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }
    // [END auth_with_google]


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

    private void signInGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signin]

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

                                if (!isExpenxActivityLaunched) {
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
}
