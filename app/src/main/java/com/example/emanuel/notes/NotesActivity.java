package com.example.emanuel.notes;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.util.List;

public class NotesActivity extends AppCompatActivity {

    private Cursor cursor;
    private SQLiteDatabase db;
    private SimpleCursorAdapter notesListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        Log.i("test: ", "onCreate called");

        NoteSQLHelper sqlHelper = NoteSQLHelper.getInstance(this);
        ListView notesListView = (ListView) findViewById(R.id.notesListView);

        Toolbar toolbar = (Toolbar) findViewById(R.id.notesToolbar);
        setSupportActionBar(toolbar);

        notesListView.setOnItemClickListener((adapterView, view, i, l) ->{
            Cursor cursor = (Cursor) adapterView.getItemAtPosition(i);
            Intent intent = new Intent(NotesActivity.this, NoteViewActivity.class);
            intent.putExtra("noteId", cursor.getInt(cursor.getColumnIndex(sqlHelper.ROW_ID)));
            startActivity(intent);
        });

        notesListView.setOnCreateContextMenuListener((contextMenu, view, contextMenuInfo) ->{
            super.onCreateContextMenu(contextMenu, view, contextMenuInfo);
            getMenuInflater().inflate(R.menu.hold_menu, contextMenu);
        });

        // here just for testing
        Button deleteDb = (Button) findViewById(R.id.deletedb);
        deleteDb.setOnClickListener(view ->
                NoteSQLHelper.getInstance(getApplicationContext())
                        .deleteDb(getApplicationContext())
        );
    }

    @Override
    protected void onStart() {
        super.onStart();
        ListView notesListView = (ListView) findViewById(R.id.notesListView);
        NoteSQLHelper sqlHelper = NoteSQLHelper.getInstance(this);

        try {
            db = sqlHelper.getReadableDatabase();
            cursor = sqlHelper.getAllNotes(db);

            notesListAdapter = new SimpleCursorAdapter(
                    this,
                    android.R.layout.simple_list_item_1,
                    cursor,
                    new String[]{sqlHelper.NOTETEXT},
                    new int[]{android.R.id.text1},
                    0
            );
            notesListView.setAdapter(notesListAdapter);
        } catch(SQLException e) {
            Toast.makeText(this, "Database error", Toast.LENGTH_SHORT)
                    .show();
        }

        // also testing
        Cursor test = sqlHelper.getAllNotes(db);
        List<Note> list = sqlHelper.listNotes();
        for(Note note : list) {
            Log.i("text: ", note.getText());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_create_note) {
            Intent intent = new Intent(this, NoteViewActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {
            case R.id.action_delete_note:
                NoteSQLHelper sqlHelper = NoteSQLHelper.getInstance(this);
                sqlHelper.deleteAtId(info.id);
                notesListAdapter.notifyDataSetChanged();
                ListView notesView = (ListView) findViewById(R.id.notesListView);
                ((SimpleCursorAdapter)notesView.getAdapter()).notifyDataSetChanged();

                Toast.makeText(this, R.string.deleted, Toast.LENGTH_SHORT)
                        .show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(cursor != null)
            cursor.close();
        if(db != null)
            db.close();
    }
}
