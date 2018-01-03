package com.palash.codilis.keepyournotes;

import android.widget.TextView;

public class note {

    TextView noteView;
    private String Note;

    public note(String Note) {
        this.Note = Note;
    }

    public void empty() {
        noteView = (TextView) noteView.findViewById(R.id.noteView);
        noteView.setText("List is empty");
    }

    public String getNote() {
        return Note;
    }

    public void setNote(String note) {
        Note = note;
    }
}
