package com.expenx.expenx.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
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
import com.expenx.expenx.core.CalculatorDialog;
import com.expenx.expenx.core.MessageOutput;
import com.expenx.expenx.model.Note;
import com.expenx.expenx.model.Reminder;
import com.expenx.expenx.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class AddNoteActivity extends AppCompatActivity {

    TextView mUploadImage;
    EditText mEditTextNoteTitle, mEditTextNoteDescription, mEditTextNoteAmount;
    ImageButton mImageButtonEditAmount, mImageButtonNoteUploadImage;
    Button mButtonCancelNote, mButtonSaveNote;
    ImageView mNoteImage;

    private Uri selectedImage = null;


    private String title;
    private String description;
    private String amount;
    private String image;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;

    private static final int GALLERY_INTENT = 2;

    private String imageUrl = "";
    private String error = "";

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("note");
        mStorage = FirebaseStorage.getInstance().getReference();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mEditTextNoteTitle = (EditText) findViewById(R.id.editTextNoteTitle);
        mEditTextNoteDescription = (EditText) findViewById(R.id.editTextNoteDescription);
        mEditTextNoteAmount = (EditText) findViewById(R.id.editTextNoteAmount);

        mImageButtonEditAmount = (ImageButton) findViewById(R.id.imageButtonEditAmount);
        mImageButtonNoteUploadImage = (ImageButton) findViewById(R.id.imageButtonNoteUploadImage);

        mButtonCancelNote = (Button) findViewById(R.id.buttonCancelNote);
        mButtonSaveNote = (Button) findViewById(R.id.buttonSaveNote);

        mUploadImage = (TextView) findViewById(R.id.TextViewUploadAnImage);
        mNoteImage = (ImageView)findViewById(R.id.imageViewNoteImage);

        mImageButtonEditAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalculatorDialog calculatorDialog = new CalculatorDialog();
                calculatorDialog.showDialog(AddNoteActivity.this, mEditTextNoteAmount);
            }
        });

        mButtonCancelNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNoteActivity.this.finish();
            }
        });

        mButtonSaveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote();
            }
        });

        mImageButtonNoteUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_INTENT);
            }
        });
    }

    private boolean validateForm() {

        boolean valid = true;

        String title = mEditTextNoteTitle.getText().toString().trim();
        String desc = mEditTextNoteDescription.getText().toString().trim();
        String amount = mEditTextNoteAmount.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            Toast.makeText(this, "Title required", Toast.LENGTH_LONG).show();
            valid = false;
        }

        if (TextUtils.isEmpty(desc)) {
            Toast.makeText(this, "Description required", Toast.LENGTH_LONG).show();
            valid = false;
        }

        if (TextUtils.isEmpty(amount)) {
            mEditTextNoteAmount.setText("0.00");
        }

        return valid;
    }

    //-------------------- Image select ------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {

            // When an Image is picked
            if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {

                selectedImage = data.getData();
                mUploadImage.setText("Image Selected");
                String path = getRealPathFromURI(selectedImage);
                image = path.substring(path.lastIndexOf("/") + 1);

                Picasso.with(AddNoteActivity.this).load(selectedImage).into(mNoteImage);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }
    }
    //-----------------------------------------------------------------

    public String getRealPathFromURI(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    // ------------- Upload image to firebase storage -------------

    private void uploadImage(String UserId) {

        try {
            final Uri _uri = selectedImage;

            StorageReference filePath = mStorage.child("users").child(UserId).child("notes").child(image);

            filePath.putFile(_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                }
            });
        } catch (Exception ex) {
            if (selectedImage != null)
                Toast.makeText(this, "Something went wrong while uploading the image", Toast.LENGTH_LONG).show();
        }
    }

    private void saveNote() {

        //Check if the user have an active internet connection
        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
        if (netInfo == null) {
            Toast.makeText(this, "Check your internet connection", Toast.LENGTH_LONG).show();
            return;
        }

        //Validte the form
        if (!validateForm()) {
            return;
        }
        MessageOutput.showProgressDialog(AddNoteActivity.this, "Registering User...");

        title = mEditTextNoteTitle.getText().toString();
        description = mEditTextNoteDescription.getText().toString();
        amount = mEditTextNoteAmount.getText().toString();

        try {

            String userId = sharedPreferences.getString("uid", null);

            uploadImage(userId);

            imageUrl = "users/" + userId + "/notes/" + image;

            DatabaseReference currentUserDb = mDatabase.child(userId);

            String mGroupId = currentUserDb.push().getKey();

            Note note = new Note(title, Double.parseDouble(amount), description, imageUrl);
            currentUserDb.child(mGroupId).setValue(note);


            MessageOutput.dismissProgressDialog();
            AddNoteActivity.this.finish();

            Toast.makeText(this, "Note added successfully", Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            MessageOutput.dismissProgressDialog();
            Toast.makeText(AddNoteActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
