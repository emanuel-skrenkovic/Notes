package com.example.emanuel.notes;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
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

        final EditText noteText = (EditText) findViewById(R.id.text);
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

        noteText.setOnEditorActionListener((textView, i, keyEvent) -> {
                if(i == EditorInfo.IME_ACTION_DONE) {
                    Note note = new Note(noteText.getText().toString(),
                            Calendar.getInstance().getTime());
                    if(intent.getExtras() != null) {
                        sqlHelper.updateNote(
                                intent.getExtras().getInt("noteId"),
                                note);
                        Log.i("update: ", "Note updated");
                    } else {
                        if(note.getText() != null)
                            sqlHelper.insertNote(note);
                        Log.i("insert: ", "Note inserted");
                    }

                    Toast.makeText(NoteViewActivity.this, "Changes saved", Toast.LENGTH_SHORT)
                            .show();

                    return true;
                }
                return false;
        });
        /*noteText.setOnFocusChangeListener((view, b) ->{
                if(!b) {
                    Note note = new Note(noteText.getText().toString(),
                            Calendar.getInstance().getTime());
                    if(intent.getExtras() != null) {
                        sqlHelper.updateNote(
                                intent.getExtras().getInt("noteId"),
                                note);
                        Log.i("update: ", "Note updated");
                    } else {
                        if(note.getText() != null)
                            sqlHelper.insertNote(note);
                        Log.i("insert: ", "Note inserted");
                    }

                    Toast.makeText(NoteViewActivity.this, "Changes saved", Toast.LENGTH_SHORT)
                            .show();
                }
        });*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(cursor != null && db != null) {
            cursor.close();
            db.close();
        }
    }
}
