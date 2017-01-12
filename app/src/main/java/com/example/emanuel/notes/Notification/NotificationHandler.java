package com.example.emanuel.notes.Notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.example.emanuel.notes.Note;
import com.example.emanuel.notes.NoteSQLHelper;
import com.example.emanuel.notes.NoteViewActivity;
import com.example.emanuel.notes.R;

import java.util.Calendar;

public class NotificationHandler {

	public static synchronized void postNotification(Context context, Note note, SQLiteDatabase db) {
		NoteSQLHelper sqlHelper = NoteSQLHelper.getInstance(context);
		android.app.NotificationManager manager =
				(android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
				.setSmallIcon(R.drawable.note)
				.setContentTitle(note.getText())
				.setContentText(note.getDateCreated())
				.setOngoing(true);

		Intent notificationIntent = new Intent(context, NoteViewActivity.class);
		notificationIntent.putExtra("noteId", note.getId());

		PendingIntent pIntent = PendingIntent.getActivity(
				context,
				0,
				notificationIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		builder.setContentIntent(pIntent);

		note.setPinned(true);
		sqlHelper.updateAtId(note.getId(), note, db);

		manager.notify((int)note.getId(), builder.build());
	}

	public static synchronized void cancelNotification(Context context, Note note, SQLiteDatabase db) {
		android.app.NotificationManager manager =
				(android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		NoteSQLHelper sqlHelper = NoteSQLHelper.getInstance(context);

		note.setPinned(false);
		sqlHelper.updateAtId(note.getId(), note, db);

		manager.cancel((int)note.getId());
	}

	public static synchronized void setAlarm(Context context, Calendar time, Note note, SQLiteDatabase db) {
		AlarmManager aManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

		Intent intent = new Intent(context, NotificationBroadcastReceiver.class);
		PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

		aManager.set(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), pIntent);

		//postNotification(context, note, db);
		Toast.makeText(context, Long.toString(time.getTimeInMillis()), Toast.LENGTH_SHORT).show();
	}
}
