package com.example.emanuel.notes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class NoteSQLHelper extends SQLiteOpenHelper {

    private static NoteSQLHelper dbHelper;
    private static final String DB_NAME = "notes.db";
    private static final String TABLE = "notes";
    public static final String ROW_ID = "_id";
    public static final String NOTETEXT= "NOTETEXT";
    public static final String DATECREATED = "DATECREATED";
    public static final String TIMECREATED = "TIMECREATED";
    public static final String PINNED = "PINNED";

    public static synchronized NoteSQLHelper getInstance(Context context) {
        if(dbHelper == null) {
            dbHelper = new NoteSQLHelper(context.getApplicationContext());
        }
        return dbHelper;
    }

    private NoteSQLHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE + "("
                + ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + NOTETEXT + " TEXT,"
                + DATECREATED + " TEXT,"
                + TIMECREATED + " TEXT,"
                + PINNED + " INTEGER DEFAULT 0);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(Note note, SQLiteDatabase db) {
        ContentValues noteValues = new ContentValues();
        noteValues.put(NOTETEXT, note.getText());
        noteValues.put(DATECREATED, note.getDateCreated());
        noteValues.put(TIMECREATED, note.getTimeCreated());
        noteValues.put(PINNED, note.isPinned());
        db.insert(TABLE, null, noteValues);
    }

    public void updateAtId(long row_id , Note note, SQLiteDatabase db) {
        ContentValues noteValues = new ContentValues();
        noteValues.put(NOTETEXT, note.getText());
        noteValues.put(DATECREATED, note.getDateCreated());
        noteValues.put(TIMECREATED, note.getTimeCreated());
        noteValues.put(PINNED, note.isPinned());
        db.update(TABLE, noteValues, (ROW_ID + "=" + row_id), null);
    }

    public void deleteAtId(long row_id , SQLiteDatabase db) {
        db.delete(TABLE,
                ROW_ID + "= ?",
                new String[]{Long.toString(row_id)});
    }

    public Note getNoteAtId(long noteId, SQLiteDatabase db) {
        Cursor cursor = db.query(
                TABLE,
                new String[]{ROW_ID, NOTETEXT, DATECREATED, TIMECREATED, PINNED},
                ROW_ID + " = ?",
                new String[] {Long.toString(noteId)},
                null, null, null
        );
        cursor.moveToFirst();

        Note note = new Note();
        note.setId(cursor.getLong(0));
        note.setText(cursor.getString(1));
        note.setDateCreated(cursor.getString(2));
        note.setTimeCreated(cursor.getString(3));
        note.setPinned(cursor.getInt(4) != 0);

        cursor.close();
        return note;
    }

    public List<Note> getAllNotes(SQLiteDatabase db) {
        List<Note> notesList = new ArrayList<>();

        Cursor cursor = db.query(TABLE,
                new String[]{ROW_ID, NOTETEXT, DATECREATED, TIMECREATED, PINNED},
                null, null, null, null, null);

        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Note note = new Note();
            note.setId(cursor.getLong(0));
            note.setText(cursor.getString(1));
            note.setDateCreated(cursor.getString(2));
            note.setTimeCreated(cursor.getString(3));
            note.setPinned(cursor.getInt(4) != 0);
            notesList.add(note);
        }
        cursor.close();
        return notesList;
    }

    //here for testing
    public void deleteDb(Context context) {
        context.deleteDatabase("notes.db");
    }
}
