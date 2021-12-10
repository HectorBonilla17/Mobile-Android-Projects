package com.example.androidnotes;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private static final String TAG = "NoteAdapter";
    private final List<Note> noteList;
    private final MainActivity mainAct;

    NoteAdapter(List<Note> nList, MainActivity ma) {
        this.noteList = nList;
        mainAct = ma;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: MAKING NEW MyViewHolder");

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_list_row, parent, false);

        itemView.setOnClickListener(mainAct);
        itemView.setOnLongClickListener(mainAct);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: FILLING VIEW HOLDER Note " + position);

        Note currentNote = noteList.get(position);

        String textDisplay = currentNote.getNoteText();
        if(textDisplay.length() > 80) {
            textDisplay = textDisplay.substring(0,80) + "...";
        }

        holder.noteTitle.setText(currentNote.getTitle());
        holder.noteText.setText(textDisplay);
        holder.noteTime.setText(currentNote.getLatestDate());
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }
}
