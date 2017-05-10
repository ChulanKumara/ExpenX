package com.expenx.expenx.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.expenx.expenx.R;
import com.expenx.expenx.core.CalendarDataModel;
import com.expenx.expenx.core.DataAdapterForNotesRecycler;
import com.expenx.expenx.core.MessageOutput;
import com.expenx.expenx.core.NoteDataModel;
import com.expenx.expenx.model.Note;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NotesActivity extends AppCompatActivity {

    private ArrayList<NoteDataModel> dataSet;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    SharedPreferences sharedPreferences;

    Button mButtonAddNote;

    DataAdapterForNotesRecycler adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        dataSet = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);


//        RecyclerView initialize
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.card_recycler_view_notes);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new DataAdapterForNotesRecycler(dataSet, NotesActivity.this);
        recyclerView.setAdapter(adapter);

        loadExpense();

        mButtonAddNote = (Button) findViewById(R.id.buttonAddNote);
        mButtonAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NotesActivity.this, AddNoteActivity.class));
                NotesActivity.this.finish();
            }
        });
    }

    public void loadExpense() {

        mDatabase.child("note").child(sharedPreferences.getString("uid", null)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Note note = snapshot.getValue(Note.class);

                    NoteDataModel dm = new NoteDataModel(note.title, note.description, note.noteImage, "Amount: " + note.amount);
                    dataSet.add(dm);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                MessageOutput.showSnackbarLongDuration(NotesActivity.this, databaseError.getMessage());
            }
        });

    }
}
