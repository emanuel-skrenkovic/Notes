package com.example.emanuel.notes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class CustomListViewAdapter extends BaseAdapter {

    private List<Note> notes;
    private Context context;

    public CustomListViewAdapter(Context context, List<Note> notes) {
        this.context = context;
        this.notes = notes;
    }

    @Override
    public int getCount() {
        return notes.size();
    }

    @Override
    public Object getItem(int i) {
        return notes.get(i);
    }

    @Override
    public long getItemId(int i) {
        return ((Note)getItem(i)).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null) {
            view = (LayoutInflater.from(context)).inflate(R.layout.notes_list, null);
        }
        Note currentNote = (Note) getItem(i);

        TextView text = (TextView) view.findViewById(R.id.noteText);
        TextView dateCreated = (TextView) view.findViewById(R.id.noteDateCreated);

        text.setText(currentNote.getText());
        dateCreated.setText(currentNote.getDateCreated());
        return view;
    }

}
