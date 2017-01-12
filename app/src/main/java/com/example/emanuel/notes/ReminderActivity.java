package com.example.emanuel.notes;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.emanuel.notes.Notification.NotificationHandler;

import java.util.Calendar;

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

        TimePicker timePicker = (TimePicker) findViewById(R.id.timePicker);
        DatePicker datePicker = (DatePicker) findViewById(R.id.datePicker);
        datePicker.setCalendarViewShown(true);
        datePicker.setSpinnersShown(false);

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
                if(sqlHelper.getNoteAtId(noteId, db).isPinned())
                    NotificationHandler.cancelNotification(this, note, db);

                int hour = timePicker.getCurrentHour();
                int minutes = timePicker.getCurrentMinute();

                Log.i("minute ", timePicker.getCurrentMinute().toString());
                Log.i("hour ", timePicker.getCurrentHour().toString());

                Calendar time = Calendar.getInstance();
                time.set(Calendar.HOUR_OF_DAY, hour);
                time.set(Calendar.MINUTE, minutes);
                time.set(Calendar.SECOND, 0);

                NotificationHandler.setAlarm(this, time, note, db);
                Toast.makeText(this, "confirm", Toast.LENGTH_SHORT).show();
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
