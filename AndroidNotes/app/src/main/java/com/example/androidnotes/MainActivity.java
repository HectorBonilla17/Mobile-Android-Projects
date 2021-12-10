package com.example.androidnotes;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, View.OnLongClickListener{

    private static final String TAG = "MainActivity";
    private final List<Note> noteList = new ArrayList<>();
    private RecyclerView recyclerView;
    private NoteAdapter nAdapter;
    private ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler);
        nAdapter = new NoteAdapter(noteList, this);
        recyclerView.setAdapter(nAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //JSON file loading method call
        noteList.addAll(loadFile());

        this.setTitle("Android Notes (" + noteList.size() + ")");
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::handleResult);
    }

    //Loads the JSON file of the program
    private ArrayList<Note> loadFile() {
        Log.d(TAG, "loadFile: Loading JSON File");
        ArrayList<Note> nList = new ArrayList<>();
        try {
            InputStream is = getApplicationContext().openFileInput(getString(R.string.file_name));
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            JSONArray jsonArray = new JSONArray(sb.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String title = jsonObject.getString("title");
                String noteText = jsonObject.getString("noteText");
                String latestDate = jsonObject.getString("latestDate");
                Note note = new Note(title, noteText, latestDate);
                nList.add(note);
            }
        } catch (FileNotFoundException e) {
            Log.d(TAG, "loadFile: No JSON File Found, Creating JSON File");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nList;
    }

    //Save the current note list into the JSON file whenever the app is paused
    @Override
    protected void onPause() {
        saveNote();
        super.onPause();
    }

    //Method called to save the current note list into the JSON file
    private void saveNote() {
        Log.d(TAG, "saveNote: Saving JSON File");
        try {
            FileOutputStream fos = getApplicationContext().
                    openFileOutput(getString(R.string.file_name), Context.MODE_PRIVATE);

            PrintWriter printWriter = new PrintWriter(fos);
            printWriter.print(noteList);
            printWriter.close();
            fos.close();

            Log.d(TAG, "saveNote: JSON:\n" + noteList.toString());

        } catch (Exception e) {
            e.getStackTrace();
        }
    }


    //Handles data from Edit Activity
    public void handleResult(ActivityResult result) {
        if (result == null || result.getData() == null) {
            Log.d(TAG, "handleResult: NULL ActivityResult received");
            return;
        }

        if (result.getResultCode() == RESULT_OK) {
            Note note;
            note = (Note) result.getData().getSerializableExtra("New_Note");
            if(note != null) {
                noteList.add(0, note);
                nAdapter.notifyItemInserted(0);
            }

            note = (Note) result.getData().getSerializableExtra("Edited_Note");
            if(note != null) {
                int notePosition = (int) result.getData().getSerializableExtra("Edited_Position");
                noteList.remove(notePosition);
                noteList.add(0, note);
                nAdapter.notifyItemRemoved(notePosition);
                nAdapter.notifyItemInserted(0);
            }

            saveNote();
            this.setTitle("Android Notes (" + noteList.size() + ")");
        } else {
            Toast.makeText(this, "OTHER result not OK!", Toast.LENGTH_SHORT).show();
        }
    }

    //Delete Note Method
    public void deleteNote(int i) {
        noteList.remove(i);
        nAdapter.notifyItemRemoved(i);
        saveNote();
        this.setTitle("Android Notes (" + noteList.size() + ")");
    }

    //Click RecycleView Methods
    @Override
    public void onClick(View v) {
        int pos = recyclerView.getChildLayoutPosition(v);
        Note n = noteList.get(pos);

        Intent data = new Intent(this, EditActivity.class);
        data.putExtra("Edit_Note", n);
        data.putExtra("Note_Position", pos);
        activityResultLauncher.launch(data);
    }

    @Override
    public boolean onLongClick(View v) {
        int pos = recyclerView.getChildLayoutPosition(v);
        Note n = noteList.get(pos);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Note '" + n.getTitle() + "'?");
        builder.setPositiveButton("Yes", (dialog, id) -> deleteNote(pos));
        builder.setNegativeButton("No", null);
        AlertDialog dialog = builder.create();
        dialog.show();
        return false;
    }

    //Menu Methods
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.opt_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.aboutButton) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.editButton) {
            Intent intent = new Intent(this, EditActivity.class);
            activityResultLauncher.launch(intent);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}