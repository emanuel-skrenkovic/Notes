package com.example.emanuel.notes;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class NotesActivity extends AppCompatActivity {

    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        Log.i("test: ", "onCreate called");

        ListView notesListView = (ListView) findViewById(R.id.notesListView);

        Toolbar toolbar = (Toolbar) findViewById(R.id.notesToolbar);
        setSupportActionBar(toolbar);

        notesListView.setOnItemClickListener((adapterView, view, pos, id) ->{
            Intent intent = new Intent(NotesActivity.this, NoteViewActivity.class);
            Log.i("listener id: ", Long.toString(notesListView.getAdapter().getItemId(pos)));
            intent.putExtra("noteId",
                    notesListView.getAdapter().getItemId(pos));
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
        NoteSQLHelper sqlHelper = NoteSQLHelper.getInstance(this);
        switch(item.getItemId()) {
            case R.id.action_delete_note:
                db = sqlHelper.getWritableDatabase();

                ListView notesView = (ListView) findViewById(R.id.notesListView);

                sqlHelper.deleteAtId(
                        notesView.getAdapter().getItemId(info.position),
                        db);

                ((CustomListViewAdapter)notesView.getAdapter())
                        .refreshList(sqlHelper.
                                getAllNotes(db));

                Toast.makeText(this, R.string.deleted, Toast.LENGTH_SHORT)
                        .show();
                return true;
            case R.id.action_reminder:
                db = sqlHelper.getReadableDatabase();

                ListView view = (ListView) findViewById(R.id.notesListView);

                Note note = sqlHelper.getNoteAtId(
                        view.getAdapter().getItemId(info.position),
                        db);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.plus)
                        .setContentTitle(note.getText())
                        .setContentText(note.getDateCreated());
                Intent notificationIntent = new Intent(this, NoteViewActivity.class);
                notificationIntent.putExtra("noteId", view.getAdapter().getItemId(info.position));
                PendingIntent pIntent = PendingIntent.getActivity(
                        this,
                        0,
                        notificationIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                builder.setContentIntent(pIntent);

                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                manager.notify(0, builder.build());
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}
