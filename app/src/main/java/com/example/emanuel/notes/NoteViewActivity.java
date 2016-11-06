package com.example.emanuel.notes;

import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class NoteViewActivity extends AppCompatActivity {

    private SQLiteDatabase db;
    private boolean textChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_view);

        final CustomEditText noteText = (CustomEditText) findViewById(R.id.text);
        TextView dateCreated = (TextView) findViewById(R.id.dateCreated);
        TextView timeCreated = (TextView) findViewById(R.id.timeCreated);

        Toolbar toolbar = (Toolbar) findViewById(R.id.noteViewToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final NoteSQLHelper sqlHelper = NoteSQLHelper.getInstance(this);

        final Intent intent = getIntent();

        if(intent.getExtras() != null) {
            final long noteId = intent.getExtras().getLong("noteId");
            Log.i("notview id" , Long.toString(noteId));

            try{
                db = sqlHelper.getReadableDatabase();
                Note note = sqlHelper.getNoteAtId(noteId, db);
                noteText.setText(note.getText());

                dateCreated.setText(note.getDateCreated());

                timeCreated.setText(note.getTimeCreated());
            } catch(SQLException e) {
                Toast.makeText(this, R.string.db_error, Toast.LENGTH_SHORT)
                        .show();
            }
        }

        noteText.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if(actionId == EditorInfo.IME_ACTION_DONE) {
                updateDb();
                return true;
            }
            return false;
        });

        noteText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textChanged = true;
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(db != null)
            db.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.red:
                Toast.makeText(this, "red", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.blue:
                Toast.makeText(this, "blue", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void updateDb() {
        Intent intent = getIntent();

        CustomEditText noteText = (CustomEditText) findViewById(R.id.text);
        TextView dateCreated = (TextView) findViewById(R.id.dateCreated);
        TextView timeCreated = (TextView) findViewById(R.id.timeCreated);

        NoteSQLHelper sqlHelper = NoteSQLHelper.getInstance(this);
        db = sqlHelper.getWritableDatabase();

        Note note = new Note(
                (intent.getExtras() != null)
                        ? intent.getExtras().getLong("noteId")
                        : sqlHelper.getAllNotes(db).size() + 1,
                noteText.getText().toString(),
                Calendar.getInstance().getTime()
        );
        Log.i("note id is ", Long.toString(note.getId()));

        if(intent.getExtras() != null) {
            long noteId = intent.getExtras().getLong("noteId");
            note.setPinned(
                    sqlHelper.getNoteAtId(noteId, db).isPinned()
            );
        }

        if(textChanged) {
            if(intent.getExtras() != null) {
                sqlHelper.updateAtId(
                        note.getId(),
                        note,
                        db);
            } else if(noteText.getText() != null){
                sqlHelper.insert(note, db);
            }

            dateCreated.setText(note.getDateCreated());
            timeCreated.setText(note.getTimeCreated());

            if(note.isPinned())
                NotificationHandler.postNotification(this, note, db);

            Toast.makeText(NoteViewActivity.this, R.string.changes_saved, Toast.LENGTH_SHORT)
                    .show();
        }
        if(textChanged)
            textChanged = false;
    }
}
