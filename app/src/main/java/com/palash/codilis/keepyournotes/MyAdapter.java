package com.palash.codilis.keepyournotes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Codilis on 05/06/2017.
 */

public class MyAdapter extends ArrayAdapter<note> {

    private final Context context;
    private final ArrayList<note> messages;

    public MyAdapter(Context context, ArrayList<note> messages) {
        super(context, R.layout.note, messages);
        this.context = context;
        this.messages = messages;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View noteView = null;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        noteView = inflater.inflate(R.layout.note, parent, false);
        TextView msgView = (TextView) noteView.findViewById(R.id.noteView);
        msgView.setText(messages.get(position).getNote());

        return noteView;
    }
}
