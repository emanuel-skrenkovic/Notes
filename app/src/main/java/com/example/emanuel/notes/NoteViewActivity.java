package com.example.emanuel.notes;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class NoteViewActivity extends AppCompatActivity {

    private SQLiteDatabase db;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_view);

        final CustomEditText noteText = (CustomEditText) findViewById(R.id.text);
        TextView dateCreated = (TextView) findViewById(R.id.dateCreated);
        TextView timeCreated = (TextView) findViewById(R.id.timeCreated);

        final NoteSQLHelper sqlHelper = NoteSQLHelper.getInstance(this);

        final Intent intent = getIntent();

        if(intent.getExtras() != null) {
            final int noteId = intent.getExtras().getInt("noteId");

            try{
                db = sqlHelper.getReadableDatabase();
                cursor = sqlHelper.getNoteAtId(noteId, db);
                noteText.setText(cursor.getString(
                        cursor.getColumnIndex(sqlHelper.NOTETEXT)
                ));

                dateCreated.setText(cursor.getString(
                        cursor.getColumnIndex(sqlHelper.DATECREATED)
                ));

                timeCreated.setText(cursor.getString(
                        cursor.getColumnIndex(sqlHelper.TIMECREATED)
                ));
            } catch(SQLException e) {
                Toast.makeText(this, "Database error", Toast.LENGTH_SHORT)
                        .show();
            }
        }

        noteText.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if(actionId == EditorInfo.IME_ACTION_DONE) {
                Note note = new Note(noteText.getText().toString(),
                        Calendar.getInstance().getTime());
                if(intent.getExtras() != null) {
                    sqlHelper.updateAtId(
                            intent.getExtras().getInt("noteId"),
                            note);
                } else if(note.getText() != null){
                    sqlHelper.insert(note);
                }

                dateCreated.setText(note.getDateCreated());
                timeCreated.setText(note.getTimeCreated());

                Toast.makeText(NoteViewActivity.this, "Changes saved", Toast.LENGTH_SHORT)
                        .show();
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(cursor != null)
            cursor.close();
        if(db != null)
            db.close();
    }
}
