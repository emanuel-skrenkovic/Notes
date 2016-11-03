package com.example.emanuel.notes;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NotificationCompat;

public class NotificationHandler {

	public static void postNotification(Context context, Note note, SQLiteDatabase db) {
		NoteSQLHelper sqlHelper = NoteSQLHelper.getInstance(context);
		android.app.NotificationManager manager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

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
}
