package com.example.emanuel.notes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class NoteViewActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_view);

        Intent intent = getIntent();
        intent.getExtras().getInt("noteId");

        TextView noteText = (TextView) findViewById(R.id.noteText);
    }
}
