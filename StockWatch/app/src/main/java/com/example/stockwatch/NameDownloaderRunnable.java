package com.example.stockwatch;

import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NameDownloaderRunnable implements Runnable {

    private static final String TAG = "NameDownloaderRunnable";
    private static final String DATA_URL = "https://api.iextrading.com/1.0/ref-data/symbols";

    private static final HashMap<String, String> stockNames = new HashMap<>();

    //Returns list of matched stocks
    public HashMap<String, String> getMatchingStockNames(String stockSymbol){
        HashMap<String, String> matchedStocks = new HashMap<>();
        for (Map.Entry<String, String> stock : stockNames.entrySet()) {
            if (stock.getKey().startsWith(stockSymbol)) {
                matchedStocks.put(stock.getKey(), stock.getValue());
            }
        }
        return matchedStocks;
    }

    public NameDownloaderRunnable(MainActivity mainActivity) {
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

            Log.d(TAG, "run: " + sb.toString());

        } catch (Exception e) {
            Log.e(TAG, "run: ", e);
            handleResults(null);
            return;
        }

        handleResults(sb.toString());
    }

    //Should be done
    private void handleResults(String s) {
        if (s == null) {
            Log.d(TAG, "handleResults: Failure in data download");
            return;
        }

        parseJSON(s);
    }

    private void parseJSON(String s) {
        try {
            JSONArray jObjMain = new JSONArray(s);
            for (int i = 0; i < jObjMain.length(); i++) {
                JSONObject jStock = (JSONObject) jObjMain.get(i);

                String symbol = jStock.getString("symbol");
                String name = jStock.getString("name");

                stockNames.put(symbol, name);
            }

        } catch (Exception e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
