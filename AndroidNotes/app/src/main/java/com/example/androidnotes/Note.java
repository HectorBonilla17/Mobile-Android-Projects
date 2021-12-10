package com.example.androidnotes;

import android.util.JsonWriter;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;

public class Note implements Serializable {

    private final String title;
    private final String noteText;
    private final String latestDate;

    public Note(String name, String description, String date) {
        this.title = name;
        this.noteText = description;
        this.latestDate = date;
    }

    String getTitle() {
        return title;
    }
    String getNoteText() {
        return noteText;
    }
    String getLatestDate() { return latestDate; }

    @NonNull
    public String toString() {
        try {
            StringWriter sw = new StringWriter();
            JsonWriter jsonWriter = new JsonWriter(sw);
            jsonWriter.setIndent("  ");
            jsonWriter.beginObject();
            jsonWriter.name("title").value(getTitle());
            jsonWriter.name("noteText").value(getNoteText());
            jsonWriter.name("latestDate").value(getLatestDate());
            jsonWriter.endObject();
            jsonWriter.close();
            return sw.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
