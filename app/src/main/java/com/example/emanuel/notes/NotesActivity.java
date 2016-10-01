package com.example.emanuel.notes;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class NotesActivity extends Activity {

    private Cursor cursor;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        ListView notesListView = (ListView) findViewById(R.id.notesListView);
        notesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(NotesActivity.this, NoteViewActivity.class);
                intent.putExtra("noteId", view.getId());
                startActivity(intent);
            }
        });

        db = NoteSQLHelper.getInstance(this).getReadableDatabase();

        cursor = db.query("notes",
                new String[]{"_id", "NOTETEXT", "DATECREATED", "TIMECREATED"},
                null, null, null, null, null);

        SimpleCursorAdapter notesListAdapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_1,
                cursor,
                new String[]{"NOTETEXT"},
                new int[]{android.R.id.text1},
                0
        );

        notesListView.setAdapter(notesListAdapter);

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        cursor.close();
        db.close();
    }
}
