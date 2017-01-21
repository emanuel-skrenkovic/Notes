package com.example.emanuel.notes;

import android.content.Intent;
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
import android.widget.Toast;

import com.example.emanuel.notes.Notification.NotificationHandler;

public class NotesActivity extends AppCompatActivity {

    private NoteSQLHelper sqlHelper;
    private SQLiteDatabase db;
    private ListView notesListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        sqlHelper = NoteSQLHelper.getInstance(this);

        notesListView = (ListView) findViewById(R.id.notesListView);

        Toolbar toolbar = (Toolbar) findViewById(R.id.notesToolbar);
        setSupportActionBar(toolbar);

        notesListView.setOnItemClickListener((adapterView, view, pos, id) -> {
            Intent intent = new Intent(NotesActivity.this, NoteViewActivity.class);
            Log.i("listener id: ", Long.toString(notesListView.getAdapter().getItemId(pos)));
            intent.putExtra("noteId",
                    notesListView.getAdapter().getItemId(pos));
            startActivity(intent);
        });

        notesListView.setOnCreateContextMenuListener((contextMenu, view, contextMenuInfo) -> {
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

            CustomListViewAdapter adapter = new CustomListViewAdapter(
                    getApplicationContext(),
                    sqlHelper.getAllNotes(db));

            notesListView.setAdapter(adapter);
        } catch(SQLException e) {
            Toast.makeText(this, R.string.db_error, Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(db != null)
            db.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_create_note:
                Intent intent = new Intent(this, NoteViewActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        long id = notesListView.getAdapter().getItemId(info.position);

        switch(item.getItemId()) {
            case R.id.action_delete_note:
                deleteNote(id);
                Toast.makeText(this, R.string.deleted, Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_reminder:
                goToReminderActivity(id);
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void deleteNote(long id) {
        db = sqlHelper.getWritableDatabase();

        Note note = sqlHelper.getNoteAtId(id, db);

        if(note.isPinned()) {
            NotificationHandler.cancelNotification(this, note, db);
        }

        sqlHelper.deleteAtId(id,db);

        ((CustomListViewAdapter)notesListView.getAdapter())
                .refreshList(sqlHelper.
                        getAllNotes(db));
    }

    private void goToReminderActivity(long id) {
        Intent reminderIntent = new Intent(this, ReminderActivity.class);
        reminderIntent.putExtra("noteId", id);
        startActivity(reminderIntent);
    }
}
