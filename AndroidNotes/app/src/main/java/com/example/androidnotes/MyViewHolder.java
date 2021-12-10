package com.example.androidnotes;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder {

    TextView noteTitle;
    TextView noteText;
    TextView noteTime;

    MyViewHolder(View view) {
        super(view);
        noteTitle = view.findViewById(R.id.NoteTitle);
        noteText = view.findViewById(R.id.NoteDescription);
        noteTime = view.findViewById(R.id.DateTime);
    }
}
