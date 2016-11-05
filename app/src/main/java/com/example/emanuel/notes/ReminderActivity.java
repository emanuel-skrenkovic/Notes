package com.example.emanuel.notes;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

public class ReminderActivity extends AppCompatActivity {

    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        NoteSQLHelper sqlHelper = NoteSQLHelper.getInstance(this);

        LinearLayout clock = (LinearLayout) findViewById(R.id.clock);
        CheckBox pinToBar = (CheckBox) findViewById(R.id.pinToBar);

        Button confirm = (Button) findViewById(R.id.confirm);
        Button cancel = (Button) findViewById(R.id.cancel);

        Toolbar toolbar = (Toolbar) findViewById(R.id.reminderToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        db = sqlHelper.getReadableDatabase();
        long noteId = getIntent().getExtras().getLong("noteId");

        pinToBar.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
                clock.setVisibility(View.INVISIBLE);
            } else {
                clock.setVisibility(View.VISIBLE);
            }
        });

        Note note = sqlHelper.getNoteAtId(noteId, db);
        pinToBar.setChecked(note.isPinned());

        confirm.setOnClickListener(view -> {
            if(pinToBar.isChecked()) {
                NotificationHandler.postNotification(this, note, db);
           } else {
               NotificationHandler.cancelNotification(this, note, db);
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
