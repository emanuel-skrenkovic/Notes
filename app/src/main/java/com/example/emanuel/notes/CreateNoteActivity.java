package com.example.emanuel.notes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Calendar;
import java.util.Date;

public class CreateNoteActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        final EditText note = (EditText) findViewById(R.id.noteText);

        Button submitNoteButton = (Button) findViewById(R.id.submitNoteButton);
        submitNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date currentDate = Calendar.getInstance().getTime();

                NoteSQLHelper.getInstance(getApplicationContext())
                        .insertNote(new Note(note.getText().toString(),
                                currentDate));

                Intent intent = new Intent(CreateNoteActivity.this, NotesActivity.class);
                startActivity(intent);
            }
        });
    }
}
