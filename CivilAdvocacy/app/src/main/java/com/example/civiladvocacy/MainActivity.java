package com.example.civiladvocacy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private final List<Official> officialList = new ArrayList<>();
    private TextView locationView;
    private RecyclerView recyclerView;
    private OfficialAdapter oAdapter;

    private FusedLocationProviderClient mFusedLocationClient;
    private static final int LOCATION_REQUEST = 111;
    private GoogleCivicRunnable officeNames = null;
    private boolean userRequest = false;
    private String userStringInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationView = findViewById(R.id.locationView);
        recyclerView = findViewById(R.id.recycler);
        oAdapter = new OfficialAdapter(officialList, this);
        recyclerView.setAdapter(oAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if(savedInstanceState != null) {
            List<Official> tempArray = (List<Official>) savedInstanceState.getSerializable("list");
            officialList.addAll(tempArray);
            locationView.setText(savedInstanceState.getString("location"));
            oAdapter.notifyDataSetChanged();
        }

        if (!doNetCheck()) {
            if(officialList.size() == 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("No Network Connection");
                builder.setMessage("Data cannot be accessed/loaded without an internet connection.");
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("No Network Connection");
                builder.setMessage("Loading Previously Downloaded Data.");
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable("list", (Serializable) officialList);
        outState.putString("location", locationView.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        int pos = recyclerView.getChildLayoutPosition(v);
        Official o = officialList.get(pos);
        Intent intent = new Intent(this, OfficialActivity.class);
        intent.putExtra("official", o);
        startActivity(intent);
    }

    private boolean doNetCheck() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            Toast.makeText(this, "Cannot access ConnectivityManager", Toast.LENGTH_SHORT).show();
            return false;
        }

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            if(officeNames == null && officialList.size() == 0 && !userRequest) {
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
                determineLocation();
            }
            return true;
        } else {
            return false;
        }
    }

    public void downloadFailed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Unable to get data on location");
        builder.setPositiveButton("Ok", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //-----------------------------------------------------------------------------------------------------------------
    /*Location Methods*/

    //Gets Current Location
    private void determineLocation() {
        if (checkPermission()) {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            getPlace(location);
                        }
                    })
                    .addOnFailureListener(this, e -> Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show());
        }
    }

    public void locationChanger(String location) {
        userRequest = true;
        if(doNetCheck()) {
            Log.d(TAG, "onCreate: internet up");
            officeNames = new GoogleCivicRunnable(this, location.replaceAll("\\s", ""));
            new Thread(officeNames).start();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Network Connection");
            builder.setMessage("Data cannot be accessed/loaded without an internet connection. ");
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private  void processQuery() {
        userRequest = true;
        if(doNetCheck()) {
            Log.d(TAG, "onCreate: internet up");
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addresses;
                addresses = geocoder.getFromLocationName(userStringInput, 1);
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String zip = addresses.get(0).getPostalCode();

                if(zip != null) {
                    runOnUiThread(() -> locationChanger(zip));
                } else {
                    runOnUiThread(() -> locationChanger(city + ", " + state));
                }

            } catch (IOException | IndexOutOfBoundsException e) {
                e.printStackTrace();
                if(e instanceof IOException){
                    runOnUiThread(() -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("Location Finder Error (GRPC)");
                        builder.setPositiveButton("Ok", null);
                        AlertDialog dialog = builder.create();
                        dialog.show(); }
                    );
                } else {
                    runOnUiThread(() -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("Invalid City, State or Zip Code Input");
                        builder.setPositiveButton("Ok", null);
                        AlertDialog dialog = builder.create();
                        dialog.show(); }
                    );
                }
            }
        } else {
            runOnUiThread(() -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("No Network Connection");
                builder.setMessage("Data cannot be accessed/loaded without an internet connection. ");
                AlertDialog dialog = builder.create();
                dialog.show();
                });
        }
    }

    private void getPlace(Location loc) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
            String zipCode = addresses.get(0).getPostalCode();
            officeNames = new GoogleCivicRunnable(this, zipCode);
            new Thread(officeNames).start();
        } catch (IOException e) {
            e.printStackTrace();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Location Finder Error (GRPC)");
            builder.setPositiveButton("Ok", null);
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    //-----------------------------------------------------------------------------------------------------------------
    /*Permission Methods*/

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST) {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    determineLocation();
                }
            }
        }
    }

    //-----------------------------------------------------------------------------------------------------------------
    /*Menu Methods*/

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
        } else if (item.getItemId() == R.id.searchButton) {
            LayoutInflater inflater = LayoutInflater.from(this);
            final View view = inflater.inflate(R.layout.dialog, null);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(view);
            builder.setTitle("Enter a City, State or a Zip Code:");
            builder.setPositiveButton("Ok", (dialog, id) -> {
                        EditText location = view.findViewById(R.id.locationInput);
                        userStringInput = location.getText().toString();
                        new Thread(this::processQuery).start();
                    }
            );
            builder.setNegativeButton("Cancel", null);
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void acceptResult(List<Official> oList) {
        officialList.clear();
        officialList.addAll(oList);
        String location = officialList.get(0).getCity() + ", " + officialList.get(0).getState() + " " + officialList.get(0).getZip();
        locationView.setText(location);
        oAdapter.notifyDataSetChanged();
    }
}