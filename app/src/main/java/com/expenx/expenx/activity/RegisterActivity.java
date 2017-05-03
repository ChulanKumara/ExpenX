package com.expenx.expenx.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.expenx.expenx.R;
import com.expenx.expenx.core.MessageOutput;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class RegisterActivity extends AppCompatActivity {

    EditText mFirstName;
    EditText mLastName;
    EditText mEmail;
    EditText mPassword;
    Button mRegisterButton;
    ImageButton mSelectImage;

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String image;

    public static FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;

    private static int RESULT_LOAD_IMG = 1;
    private Uri selectedImage = null;
    private boolean registerSucess = false;
    private String imageUrl = "";
    private String error = "";
    private static final int GALLERY_INTENT = 2;

    //-----------------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("user");
        mStorage = FirebaseStorage.getInstance().getReference();

        mFirstName = (EditText) findViewById(R.id.txtFirstName);
        mLastName = (EditText) findViewById(R.id.txtLastName);
        mEmail = (EditText) findViewById(R.id.txtEmail);
        mPassword = (EditText) findViewById(R.id.txtPassword);
        mSelectImage = (ImageButton) findViewById(R.id.buttonLoadPicture);
        mRegisterButton = (Button) findViewById(R.id.btnRegister);

        mSelectImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,GALLERY_INTENT);
            }
        });

        //Call register user function
        mRegisterButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    //----------------------------------------------------------------------------------------------------------
    private void registerUser() {

        //Check if the user have an active internet connection
        ConnectivityManager conMgr =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
        if (netInfo == null){
            Toast.makeText(this, "Check your internet connection", Toast.LENGTH_LONG).show();
            return;
        }

        //Validte the form
        if (!validateForm()) {
            return;
        }
        MessageOutput.showProgressDialog(RegisterActivity.this, "Registering User...");

        firstName = mFirstName.getText().toString();
        lastName = mLastName.getText().toString();
        email = mEmail.getText().toString();
        password = mPassword.getText().toString();

        try {

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {

                        String userId = mAuth.getCurrentUser().getUid();

                        uploadImage(userId);

                        imageUrl = "users\\" + userId + "\\" + image;

                        DatabaseReference currentUserDb = mDatabase.child(userId);
                        currentUserDb.child("defaultCurrency").setValue("USD");
                        currentUserDb.child("fName").setValue(firstName);
                        currentUserDb.child("lName").setValue(lastName);
                        currentUserDb.child("image").setValue(imageUrl);

                        MessageOutput.dismissProgressDialog();
                        Toast.makeText(RegisterActivity.this, "Successfull.. Please Login", Toast.LENGTH_LONG).show();
                        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(loginIntent);

                    }else{
                        MessageOutput.dismissProgressDialog();
                        Toast.makeText(RegisterActivity.this, error, Toast.LENGTH_LONG).show();
                    }
                }
            });

        }catch(Exception ex){
            MessageOutput.dismissProgressDialog();
            Toast.makeText(RegisterActivity.this, "", Toast.LENGTH_LONG).show();
        }


    }
//---------------------------------------------------------------------------------------------------------------------------

    //-------------------- Image select ------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {

            // When an Image is picked
            if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {

                selectedImage = data.getData();
                mSelectImage.setBackgroundResource(R.drawable.button_image_selected);
                String path = getRealPathFromURI(selectedImage);
                image = path.substring(path.lastIndexOf("/")+1);
            }

        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }
    }
    //-----------------------------------------------------------------

    public String getRealPathFromURI(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    // ------------- Upload image to firebase storage -------------

    private void uploadImage(String UserId){

        try {
            final Uri _uri = selectedImage;

            StorageReference filePath = mStorage.child("users").child(UserId).child("profileimage").child(_uri.getLastPathSegment());

            filePath.putFile(_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                }
            });
        }catch(Exception ex){
            Toast.makeText(this, "Something went wrong while uploading the image", Toast.LENGTH_LONG).show();
        }
    }
    //-------------------------------------------------------------

    // ----------------- Form Validation --------------------------
    private boolean validateForm() {

        boolean valid = true;

        String firstName =  mFirstName.getText().toString();
        String lastName =  mLastName.getText().toString();
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();

        if (TextUtils.isEmpty(firstName)) {
            Toast.makeText(this, "First Name required", Toast.LENGTH_LONG).show();
            valid = false;
            return false;
        } else {
            mFirstName.setError(null);
        }

        if (TextUtils.isEmpty(lastName)) {
            Toast.makeText(this, "Last Name required", Toast.LENGTH_LONG).show();
            valid = false;
            return false;
        } else {
            mLastName.setError(null);
        }

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Email required", Toast.LENGTH_LONG).show();
            valid = false;
            return false;
        }else if(!isValidEmailAddress(email)){
            Toast.makeText(this, "Invalid email", Toast.LENGTH_LONG).show();
            valid = false;
            return false;
        } else if(checkUserExits(email)){
            Toast.makeText(this, "User already exists", Toast.LENGTH_LONG).show();
            valid = false;
            return false;
        }else{
            mEmail.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Password required", Toast.LENGTH_LONG).show();
            valid = false;
            return false;
        } else if(!checkPasswordStrength(password)) {
            valid = false;
            return false;
        }else{
            mPassword.setError(null);
        }

        if(selectedImage == null){
            Toast.makeText(this, "Image required", Toast.LENGTH_LONG).show();
            valid = false;
            return false;
        }

        return valid;
    }

    //------------------------------------------------------------

    private boolean checkUserExits(String email){

        final boolean[] b = new boolean[1];
        b[0] = false;

        mAuth.fetchProvidersForEmail(email).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                if(!task.getResult().getProviders().isEmpty()){
                    b[0] = true;
                }
            }
        });

        return b[0];

    }

    private boolean checkPasswordStrength(String password){

        boolean valid = true;

        Pattern pattern = Pattern.compile("[a-zA-Z0-9]*");
        Matcher matcher = pattern.matcher(password);

        if(password.length() < 6){
            Toast.makeText(this, "Password should be more than 6 characters", Toast.LENGTH_LONG).show();
            valid = false;
        }else if (matcher.matches()) {
            Toast.makeText(this, "Password should have atleast one speacial character", Toast.LENGTH_LONG).show();
            valid = false;
        }
        return valid;
    }

    public static boolean isValidEmailAddress(String email) {
        final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(email);
        return matcher.find();
    }

}