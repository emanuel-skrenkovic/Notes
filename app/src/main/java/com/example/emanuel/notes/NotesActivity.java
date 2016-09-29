package com.example.emanuel.notes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class NotesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.activity_notes_linear);

        List<Note> notesList = NoteSQLHelper.getInstance(getApplicationContext())
                .listNotes();

        int i = 0;
        for (Note note : notesList) {
            TextView noteView = new TextView(getApplicationContext());
            noteView.setText(note.getNoteText());
            noteView.setId(i);
            linearLayout.addView(noteView);
            i++;
        }

        /*linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch(view.getId()) {
                    case 1:

                }
            }
        });*/

        Button newNote = (Button) findViewById(R.id.newNoteButton);
        newNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NotesActivity.this, CreateNoteActivity.class);
                startActivity(intent);
            }
        });

        Button deleteDb = (Button) findViewById(R.id.deletedb);
        deleteDb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NoteSQLHelper.getInstance(getApplicationContext())
                        .deleteDb(getApplicationContext());
            }
        });
    }
}
