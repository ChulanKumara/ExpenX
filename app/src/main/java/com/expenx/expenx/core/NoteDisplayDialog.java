package com.expenx.expenx.core;

import android.app.Activity;
import android.app.Dialog;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.expenx.expenx.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

/**
 * Created by skaveesh on 2017-05-10.
 */

public class NoteDisplayDialog {

    private TextView mTextViewNoteDialogTitle, mTextViewNoteDialogDescription, mTextViewNoteDialogAmount;
    private ImageView mImageViewNoteDialogImage;

    public void showDialog(final Activity activity, NoteDataModel noteDataModel) {

        final Dialog dialog = new Dialog(activity);

        WindowManager.LayoutParams dialogLayoutParameters = new WindowManager.LayoutParams();
        dialogLayoutParameters.copyFrom(dialog.getWindow().getAttributes());
        dialogLayoutParameters.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialogLayoutParameters.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.notes_display_dialog);
        dialog.getWindow().setAttributes(dialogLayoutParameters);

        mTextViewNoteDialogTitle = (TextView) dialog.findViewById(R.id.textViewNoteDialogTitle);
        mTextViewNoteDialogDescription = (TextView) dialog.findViewById(R.id.textViewNoteDialogDescription);
        mTextViewNoteDialogAmount = (TextView) dialog.findViewById(R.id.textViewNoteDialogAmount);
        mImageViewNoteDialogImage = (ImageView) dialog.findViewById(R.id.imageViewNoteDialogImage);

        mTextViewNoteDialogTitle.setText("Title: "+noteDataModel.getTitle());
        mTextViewNoteDialogDescription.setText("Description: "+noteDataModel.getDescription());
        mTextViewNoteDialogAmount.setText("Amount: "+noteDataModel.getAmount());
        mImageViewNoteDialogImage.setImageResource(R.drawable.ic_broken_image);


        try {
            String imageReult = noteDataModel.getImageUrl();
            imageReult = imageReult.substring(imageReult.lastIndexOf('/') + 1, imageReult.length());

            if (!imageReult.equals("null")) {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();
                storageRef.child(noteDataModel.getImageUrl()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Toast.makeText(activity, "Loading image please wait...!", Toast.LENGTH_LONG)
                                .show();
                        Picasso.with(activity).load(uri).into(mImageViewNoteDialogImage);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(activity, "Something went wrong while loading image...!", Toast.LENGTH_LONG)
                                .show();
                        mImageViewNoteDialogImage.setImageResource(R.drawable.ic_broken_image);
                    }
                });
            }
        } catch (Exception e) {

        }

        dialog.show();
    }
}
