package com.example.civiladvocacy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class PhotoActivity extends AppCompatActivity {

    private View layout;
    private TextView title;
    private TextView name;
    private ImageView partyIcon;
    private ImageView photoView;
    private Official officialInfo;
    private TextView locationView;
    private Picasso picasso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        picasso = Picasso.get();

        layout = findViewById(R.id.photoActivityLayout);
        title = findViewById(R.id.officeView);
        name = findViewById(R.id.nameView);
        partyIcon = findViewById(R.id.partyIconPhoto);
        photoView = findViewById(R.id.photoImageView);
        locationView = findViewById(R.id.locationViewPhoto);

        if(getIntent().hasExtra("official")) {
            officialInfo = (Official) getIntent().getSerializableExtra("official");

            String location = officialInfo.getCity() + ", " + officialInfo.getState() + " " + officialInfo.getZip();
            locationView.setText(location);

            title.setText(officialInfo.getOfficeName());
            name.setText(officialInfo.getOfficialName());

            if(officialInfo.getPhoto() != null && doNetCheck()) {
                loadRemoteImage(officialInfo.getPhoto());
            } else if (!doNetCheck()){
                photoView.setImageResource(R.drawable.brokenimage);
            }


            if(officialInfo.getPartyName().equals("Democratic Party") || officialInfo.getPartyName().equals("Democrat Party")) {
                partyIcon.setImageResource(R.drawable.dem_logo);
            } else if (officialInfo.getPartyName().equals("Republican Party")) {
                partyIcon.setImageResource(R.drawable.rep_logo);
            } else {
                partyIcon.setVisibility(View.GONE);
            }

            if(officialInfo.getPartyName().equals("Democratic Party") || officialInfo.getPartyName().equals("Democrat Party")) {
                layout.setBackgroundColor(Color.parseColor("#0001FE"));
            } else if (officialInfo.getPartyName().equals("Republican Party")) {
                layout.setBackgroundColor(Color.parseColor("#FE0000"));
            }

        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("officeName", officialInfo.getOfficeName());
        outState.putString("officialName", officialInfo.getOfficialName());
        outState.putString("partyIcon", officialInfo.getPartyName());

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);

        title.setText(savedInstanceState.getString("officeName"));
        name.setText(savedInstanceState.getString("officialName"));
        String partyIconName = savedInstanceState.getString("partyIcon");

        if(partyIconName.equals("Democratic Party") || partyIconName.equals("Democrat Party")) {
            partyIcon.setImageResource(R.drawable.dem_logo);
        } else if (partyIconName.equals("Republican Party")) {
            partyIcon.setImageResource(R.drawable.rep_logo);
        } else {
            partyIcon.setVisibility(View.GONE);
        }
    }

    private boolean doNetCheck() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            Toast.makeText(this, "Cannot access ConnectivityManager", Toast.LENGTH_SHORT).show();
            return false;
        }

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private void loadRemoteImage(String imageURL) {
        picasso.load(imageURL)
                .error(R.drawable.brokenimage)
                .placeholder(R.drawable.placeholder)
                .into(photoView);
    }

    public void goToPartyWebsite (View v) {

        if(!doNetCheck()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Network Connection");
            builder.setMessage("Cannot load website without an internet connection.");
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }

        String DATA_URL;
        if(officialInfo.getPartyName().equals("Democratic Party") || officialInfo.getPartyName().equals("Democrat Party")) {
            DATA_URL = "https://democrats.org/";
        } else if (officialInfo.getPartyName().equals("Republican Party")) {
            DATA_URL = "https://www.gop.com/";
        } else {
            return;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(DATA_URL));

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
