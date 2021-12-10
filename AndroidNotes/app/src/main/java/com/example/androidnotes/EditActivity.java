package com.example.androidnotes;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditActivity extends AppCompatActivity {

    private String initialTitle = "";
    private String initialDescription = "";
    private Note n;
    private EditText editTitle;
    private EditText editDescription;
    private int notePosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        editTitle = findViewById(R.id.NoteTitleInput);
        editDescription = findViewById(R.id.NoteDescriptionInput);

        //Gets Intent data if a note is going to be edited
        if(getIntent().hasExtra("Edit_Note")) {
             n = (Note) getIntent().getSerializableExtra("Edit_Note");
             notePosition = (int) getIntent().getSerializableExtra("Note_Position");
             initialTitle = n.getTitle();
             initialDescription = n.getNoteText();
             editTitle.setText(n.getTitle());
             editDescription.setText(n.getNoteText());
        } else {
            n = null;
        }
    }

    //Return edited Note data
    public void editNote(View v) {
        if(initialTitle.equals(editTitle.getText().toString()) && initialDescription.equals(editDescription.getText().toString())) {
            finish();
            return;
        }

        String title = editTitle.getText().toString();
        String description = editDescription.getText().toString();

        if(title.trim().isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Note will not be saved without a title!");
            builder.setPositiveButton("Ok", (dialog, id) -> finish());
            builder.setNegativeButton("Cancel", null);
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }

        Date unformattedDate = new Date();
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d, h:mm a");
        String formattedDate = dateFormat.format(unformattedDate);

        n = new Note(title, description, formattedDate);
        Intent data = new Intent();
        data.putExtra("Edited_Note", n);
        data.putExtra("Edited_Position", notePosition);
        setResult(RESULT_OK, data);
        finish();
    }

    //Return new Note data
    public void doReturnData(View v) {
        if(initialTitle.equals(editTitle.getText().toString()) && initialDescription.equals(editDescription.getText().toString())) {
            finish();
            return;
        }

        String title = editTitle.getText().toString();
        String description = editDescription.getText().toString();

        if(title.trim().isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Note will not be saved without a title!");
            builder.setPositiveButton("Ok", (dialog, id) -> finish());
            builder.setNegativeButton("Cancel", null);
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }

        Date unformattedDate = new Date();
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d, h:mm a");
        String formattedDate = dateFormat.format(unformattedDate);

        n = new Note(title, description, formattedDate);
        Intent data = new Intent();
        data.putExtra("New_Note", n);
        setResult(RESULT_OK, data);
        finish();
    }

    //Back Button Method
    @Override
    public void onBackPressed() {
        String title = editTitle.getText().toString();

        if(initialTitle.equals(editTitle.getText().toString()) && initialDescription.equals(editDescription.getText().toString())) {
            finish();
        } else if(title.trim().isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Note will not be saved without a title!");
            builder.setPositiveButton("Ok", (dialog, id) -> finish());
            builder.setNegativeButton("Cancel", null);
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Your note is not saved!");
            builder.setMessage("Save note '" + editTitle.getText().toString() + "'?");
            builder.setPositiveButton("Yes", (dialog, id) -> choiceSaveMethod());
            builder.setNegativeButton("No", (dialog, id) -> finish());
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    //Menu Methods
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //Decides which data return method to use based on whether its a new or edited not
    public void choiceSaveMethod(){
        if(n == null) {
            doReturnData(null);
        } else {
            editNote(null);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.saveButton) {
            choiceSaveMethod();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
