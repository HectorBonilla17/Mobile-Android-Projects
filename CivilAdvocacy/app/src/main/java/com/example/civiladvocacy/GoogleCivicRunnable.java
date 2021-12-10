package com.example.civiladvocacy;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GoogleCivicRunnable implements Runnable {

    private static final String TAG = "GoogleCivicRunnable";
    private final String DATA_URL;
    private final MainActivity mainActivity;


    public GoogleCivicRunnable(MainActivity mainActivity, String location) {
        this.mainActivity = mainActivity;
        Log.d(TAG, "GoogleCivicRunnable: " + location);
        DATA_URL = "https://www.googleapis.com/civicinfo/v2/representatives?key=AIzaSyDTKGNPaEO-gYp3uUWcqNLxslj9p358-4k&address=" + location;
    }

    @Override
    public void run() {
        Uri dataUri = Uri.parse(DATA_URL);
        String urlToUse = dataUri.toString();
        Log.d(TAG, "run: " + urlToUse);

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.d(TAG, "run: HTTP ResponseCode NOT OK: " + conn.getResponseCode());
                handleResults(null);
                return;
            }

            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }


        } catch (Exception e) {
            Log.e(TAG, "run: ", e);
            handleResults(null);
            return;
        }

        handleResults(sb.toString());
    }


    private void handleResults(String s) {
        if (s == null) {
            Log.d(TAG, "handleResults: Failure in data download");
            mainActivity.runOnUiThread(mainActivity::downloadFailed);
            return;
        }

        final List<Official> officialList = parseJSON(s);
        mainActivity.runOnUiThread(() -> mainActivity.acceptResult(officialList));
    }

    private List<Official> parseJSON(String s) {
        List<Official> officialList = new ArrayList<>();

        try {
            JSONObject jOffices = new JSONObject(s);

            JSONObject normalInput = jOffices.getJSONObject("normalizedInput");
            String city = normalInput.getString("city");
            String state = normalInput.getString("state");
            String zip = normalInput.getString("zip");

            JSONArray officialsArray = jOffices.getJSONArray("officials");
            JSONArray officesArray = jOffices.getJSONArray("offices");

            for(int i = 0; i < officesArray.length(); i++) {
                String officeTitle;
                String name;
                String party;
                String address;
                String websiteUrl = null;
                String phone = null;
                String email = null;
                String photoUrl = null;
                String youtubeUrl = null;
                String twitterUrl = null;
                String facebookUrl = null;

                JSONObject currentIndex = officesArray.getJSONObject(i);
                officeTitle = currentIndex.getString("name");

                String officialIndex = currentIndex.getString("officialIndices");
                String [] separatedStrings = officialIndex.replaceAll("\\[", "").replaceAll("]", "").split(",");
                int [] officialIndices = new int[separatedStrings.length];
                for (int j = 0; j < separatedStrings.length; j++) {
                    officialIndices[j] = Integer.parseInt(separatedStrings[j]);
                }

                for(int k = 0; k < officialIndices.length; k++) {
                    JSONObject currentOfficial = officialsArray.getJSONObject(officialIndices[k]);
                    name = currentOfficial.getString("name");

                    if (currentOfficial.has("party")) {
                        party = currentOfficial.getString("party");
                    } else {
                        party = "Unknown";
                    }

                    String addressNotePad = null;
                    if(currentOfficial.has("address")) {
                        JSONArray addressArray = currentOfficial.getJSONArray("address");
                        addressNotePad = addressArray.getJSONObject(0).getString("line1");

                        if(addressArray.getJSONObject(0).has("line2") && addressArray.getJSONObject(0).getString("line2").length() != 0) {
                            addressNotePad += ", " + addressArray.getJSONObject(0).getString("line2");
                        }

                        if(addressArray.getJSONObject(0).has("line3" ) && addressArray.getJSONObject(0).getString("line3").length() != 0) {
                            addressNotePad += ", " + addressArray.getJSONObject(0).getString("line3");
                        }

                        if(addressArray.getJSONObject(0).has("city")) {
                            addressNotePad += ", " + addressArray.getJSONObject(0).getString("city");
                        }

                        if(addressArray.getJSONObject(0).has("state")) {
                            addressNotePad += ", " + addressArray.getJSONObject(0).getString("state");
                        }

                        if(addressArray.getJSONObject(0).has("zip")) {
                            addressNotePad += " " + addressArray.getJSONObject(0).getString("zip");
                        }
                    }
                    address = addressNotePad;


                    if(currentOfficial.has("phones")) {
                        JSONArray phoneNumbers = currentOfficial.getJSONArray("phones");
                        phone = phoneNumbers.getString(0);
                    }

                    if(currentOfficial.has("urls")) {
                        JSONArray urls = currentOfficial.getJSONArray("urls");
                        websiteUrl = urls.getString(0);
                    }


                    if(currentOfficial.has("emails")) {
                        JSONArray emails = currentOfficial.getJSONArray("emails");
                        email = emails.getString(0);
                    }
                    
                    if(currentOfficial.has("photoUrl")) {
                        photoUrl = currentOfficial.getString("photoUrl");
                    }

                    if(currentOfficial.has("channels")) {
                        JSONArray possibleChannels = currentOfficial.getJSONArray("channels");
                        for(int j = 0; j < possibleChannels.length(); j++) {
                            JSONObject tempChannel = possibleChannels.getJSONObject(j);

                            String type = tempChannel.getString("type");
                            if(type.equals("Facebook")) {
                                facebookUrl = tempChannel.getString("id");
                            } else if (type.equals("Twitter")) {
                                twitterUrl = tempChannel.getString("id");
                            } else if (type.equals("YouTube")) {
                                youtubeUrl = tempChannel.getString("id");
                            }
                        }

                    }

                    officialList.add(new Official(city, state, zip, officeTitle, name, party, address, phone, websiteUrl, email, photoUrl, facebookUrl, twitterUrl, youtubeUrl));
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "exception parseJSON: " + e.getMessage());
            e.printStackTrace();
        }

        return officialList;
    }
}
