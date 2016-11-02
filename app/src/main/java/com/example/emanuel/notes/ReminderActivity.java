package com.example.emanuel.notes;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

public class ReminderActivity extends AppCompatActivity {

    private SQLiteDatabase db;
    private long noteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        NoteSQLHelper sqlHelper = NoteSQLHelper.getInstance(this);

        LinearLayout clock = (LinearLayout) findViewById(R.id.clock);
        CheckBox pinToBar = (CheckBox) findViewById(R.id.pinToBar);

        Button done = (Button) findViewById(R.id.done);
        Button cancel = (Button) findViewById(R.id.cancel);

        Toolbar toolbar = (Toolbar) findViewById(R.id.reminderToolbar);
        setSupportActionBar(toolbar);

        db = sqlHelper.getReadableDatabase();
        noteId = getIntent().getExtras().getLong("noteId");

        pinToBar.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
                clock.setVisibility(View.INVISIBLE);
            } else {
                clock.setVisibility(View.VISIBLE);
            }
        });

        Note note = sqlHelper.getNoteAtId(noteId, db);
        pinToBar.setChecked(note.isPinned());

        done.setOnClickListener(view -> {
           if(pinToBar.isChecked()) {

               NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                       .setSmallIcon(R.drawable.note)
                       .setContentTitle(note.getText())
                       .setContentText(note.getDateCreated())
                       .setOngoing(true);

               Intent notificationIntent = new Intent(this, NoteViewActivity.class);
               notificationIntent.putExtra("noteId", noteId);

               PendingIntent pIntent = PendingIntent.getActivity(
                       this,
                       0,
                       notificationIntent,
                       PendingIntent.FLAG_UPDATE_CURRENT);

               builder.setContentIntent(pIntent);

               note.setPinned(true);
               sqlHelper.updateAtId(noteId, note, db);

               NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
               manager.notify((int)noteId, builder.build());
           } else {
               note.setPinned(false);
               sqlHelper.updateAtId(noteId, note, db);
               NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
               manager.cancel((int) noteId);
           }
           onBackPressed();
        });

        cancel.setOnClickListener(view -> onBackPressed());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(db != null)
            db.close();
    }
}
