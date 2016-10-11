package com.example.emanuel.notes;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Note {

    private String text;
    private String dateCreated;
    private String timeCreated;

    public Note() {

    }

    public Note(String text, Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        this.text = text;
        this.dateCreated = dateFormat.format(date);
        this.timeCreated = timeFormat.format(date);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(String timeCreated) {
        this.timeCreated = timeCreated;
    }

}
