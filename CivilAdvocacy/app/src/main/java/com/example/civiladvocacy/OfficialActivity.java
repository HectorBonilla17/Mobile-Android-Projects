package com.example.civiladvocacy;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class OfficialActivity extends AppCompatActivity {
    private static final String TAG = "OfficialActivity";
    private Picasso picasso;

    private TextView officeView;
    private TextView officialView;
    private TextView partyView;
    private ImageView photoView;
    private ImageView partyIconView;
    private ImageView facebookView;
    private ImageView twitterView;
    private ImageView youtubeView;
    private TextView addressView;
    private TextView addressPointer;
    private TextView phoneView;
    private TextView phonePointer;
    private TextView emailView;
    private TextView emailPointer;
    private TextView websiteView;
    private TextView websitePointer;
    private View layout;
    private TextView locationView;
    private Official officialInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official);
        picasso = Picasso.get();
        picasso.setLoggingEnabled(true);

        officeView = findViewById(R.id.officeNameView);
        officialView = findViewById(R.id.officialNameView);
        partyView = findViewById(R.id.partyNameView);
        addressView = findViewById(R.id.addressView);
        addressPointer = findViewById(R.id.addressPointer);
        phoneView = findViewById(R.id.phoneView);
        phonePointer = findViewById(R.id.phonePointer);
        emailView = findViewById(R.id.emailView);
        emailPointer = findViewById(R.id.emailPointer);
        websiteView = findViewById(R.id.websiteView);
        websitePointer = findViewById(R.id.websitePointer);
        photoView = findViewById(R.id.photoView);
        layout = findViewById(R.id.contraintLayout);
        partyIconView = findViewById(R.id.partyIconView);
        facebookView = findViewById(R.id.facebookView);
        twitterView = findViewById(R.id.twitterView);
        youtubeView = findViewById(R.id.youtubeView);
        locationView = findViewById(R.id.locationViewOfficial);

        if(getIntent().hasExtra("official")) {
            officialInfo = (Official) getIntent().getSerializableExtra("official");

            String location = officialInfo.getCity() + ", " + officialInfo.getState() + " " + officialInfo.getZip();
            locationView.setText(location);

            officeView.setText(officialInfo.getOfficeName());
            officialView.setText(officialInfo.getOfficialName());
            partyView.setText("(" + officialInfo.getPartyName() + ")");

            if(officialInfo.getPhoto() != null && doNetCheck()) {
                loadRemoteImage(officialInfo.getPhoto());
            } else if (officialInfo.getPhoto() != null && !doNetCheck()){
                photoView.setImageResource(R.drawable.brokenimage);
            }

            if((officialInfo.getAddress()) != null){
                addressView.setText(officialInfo.getAddress());
            } else {
                addressPointer.setVisibility(View.GONE);
                addressView.setVisibility(View.GONE);
            }

            if ((officialInfo.getPhone() != null)) {
                phoneView.setText(officialInfo.getPhone());
            } else {
                phonePointer.setVisibility(View.GONE);
                phoneView.setVisibility(View.GONE);
            }

            if ((officialInfo.getEmail() != null)) {
                emailView.setText(officialInfo.getEmail());
            } else {
                emailPointer.setVisibility(View.GONE);
                emailView.setVisibility(View.GONE);
            }

            if ((officialInfo.getUrl() != null)) {
                websiteView.setText(officialInfo.getUrl());
            } else {
                websitePointer.setVisibility(View.GONE);
                websiteView.setVisibility(View.GONE);
            }


            if(officialInfo.getFacebook() == null) {
                facebookView.setVisibility(View.INVISIBLE);
            }

            if(officialInfo.getTwitter() == null) {
                twitterView.setVisibility(View.INVISIBLE);
            }
            if(officialInfo.getYoutube() == null) {
                youtubeView.setVisibility(View.INVISIBLE);
            }


            if(officialInfo.getPartyName().equals("Democratic Party") || officialInfo.getPartyName().equals("Democrat Party")) {
                partyIconView.setImageResource(R.drawable.dem_logo);
            } else if (officialInfo.getPartyName().equals("Republican Party")) {
                partyIconView.setImageResource(R.drawable.rep_logo);
            } else {
                partyIconView.setVisibility(View.GONE);
            }

            if(officialInfo.getPartyName().equals("Democratic Party") || officialInfo.getPartyName().equals("Democrat Party")) {
                layout.setBackgroundColor(Color.parseColor("#0001FE"));
            } else if (officialInfo.getPartyName().equals("Republican Party")) {
                layout.setBackgroundColor(Color.parseColor("#FE0000"));
            }
        }

        Linkify.addLinks(addressView, Linkify.ALL);
        Linkify.addLinks(phoneView, Linkify.ALL);
        Linkify.addLinks(emailView, Linkify.ALL);
        Linkify.addLinks(websiteView, Linkify.ALL);
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

    public void goToPhotoActivity(View v) {
        if(officialInfo.getPhoto() != null) {
            Intent intent = new Intent(this, PhotoActivity.class);
            intent.putExtra("official", officialInfo);
            startActivity(intent);
        }
    }

    private void loadRemoteImage(String imageURL) {
        long millisS = System.currentTimeMillis();

        picasso.load(imageURL)
                .error(R.drawable.brokenimage)
                .placeholder(R.drawable.placeholder)
                .into(photoView, new Callback() {
                    @Override
                    public void onSuccess() {
                        long millisE = System.currentTimeMillis();
                        Log.d(TAG, "loadRemoteImage Success: Duration: " +
                                (millisE-millisS) + " ms");
                    }

                    @Override
                    public void onError(Exception e) {
                        long millisE = System.currentTimeMillis();
                        Log.d(TAG, "loadRemoteImage Fail: Duration: " +
                                (millisE-millisS) + " ms");
                    }
                });
    }

    //-----------------------------------------------------------------------------------------------------------------
    /*Interactive Links Methods*/

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

    public void goToFacebook(View v) {
        String name = officialInfo.getFacebook();
        Intent intent = null;

        if(!doNetCheck()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Network Connection");
            builder.setMessage("Cannot load Facebook without an internet connection.");
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }

        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.facebook.katana");
            intent.setData(Uri.parse("https://www.facebook.com/" + name));
            startActivity(intent);
        }
        catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.facebook.com/" + name)));
        }
    }

    public void goToTwitter(View v) {
        String name = officialInfo.getTwitter();
        Intent intent = null;

        if(!doNetCheck()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Network Connection");
            builder.setMessage("Cannot load Twitter without an internet connection.");
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }

        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.twitter.android");
            intent.setData(Uri.parse("https://twitter.com/" + name));
            startActivity(intent);
        }
        catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://twitter.com/" + name)));
        }
    }

    public void goToYoutube(View v) {
        String name = officialInfo.getYoutube();
        Intent intent = null;

        if(!doNetCheck()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Network Connection");
            builder.setMessage("Cannot load Youtube without an internet connection.");
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }

        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.youtube");
            intent.setData(Uri.parse("https://www.youtube.com/" + name));
            startActivity(intent);
        }
        catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/" + name)));
        }
    }

}
