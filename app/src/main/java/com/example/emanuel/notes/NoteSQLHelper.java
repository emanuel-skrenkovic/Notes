package com.example.emanuel.notes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NoteSQLHelper extends SQLiteOpenHelper {

    private static NoteSQLHelper dbHelper;

    public static synchronized NoteSQLHelper getInstance(Context context) {
        if(dbHelper == null) {
            dbHelper = new NoteSQLHelper(context.getApplicationContext());
        }
        return dbHelper;
    }

    private NoteSQLHelper(Context context) {
        super(context, "notes.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE notes ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "NOTETEXT TEXT,"
                + "DATECREATED TEXT,"
                + "TIMECREATED TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues noteValues = new ContentValues();
        noteValues.put("NOTETEXT", note.getNoteText());
        noteValues.put("DATECREATED", note.getDateCreated());
        noteValues.put("TIMECREATED", note.getTimeCreated());
        db.insert("NOTES", null, noteValues);
        db.close();
    }

    public List<Note> listNotes() {
        List<Note> notesList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query("notes",
                new String[]{"NOTETEXT", "DATECREATED", "TIMECREATED"},
                null, null, null, null, null);

        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Note note = new Note();
            note.setNoteText(cursor.getString(0));
            note.setDateCreated(cursor.getString(1));
            note.setTimeCreated(cursor.getString(2));
            notesList.add(note);
        }
        cursor.close();
        db.close();
        return notesList;
    }

    public void deleteDb(Context context) {
        context.deleteDatabase("notes.db");
    }
}
