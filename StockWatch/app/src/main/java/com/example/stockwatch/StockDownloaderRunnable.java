package com.example.stockwatch;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class StockDownloaderRunnable implements Runnable {

    private static final String TAG = "StockDownloaderRunnable";
    private final String symbol;
    private final String DATA_URL;
    private final MainActivity mainActivity;

    public StockDownloaderRunnable(MainActivity mainActivity, String symbol) {
        this.symbol = symbol;
        this.mainActivity = mainActivity;
        this.DATA_URL = "https://cloud.iexapis.com/stable/stock/" + symbol + "/quote?token=" + "pk_e01c230a62b24a7a9cca5ca0bf4abcf2";
    }

    //Gets stock JSON
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

    //Makes sure the JSON was actually downloaded before attempting to parse
    private void handleResults(String s) {
        if (s == null) {
            Log.d(TAG, "handleResults: Failure in data download");
            return;
        }

        final Stock stock = parseJSON(s);
        mainActivity.runOnUiThread(() -> mainActivity.acceptResult(stock));
    }

    private Stock parseJSON(String s) {
        Log.d(TAG, "Making stock!");
        Stock stockInfo = new Stock ("", "", 0, 0, 0);
        try {
            JSONObject jStock = new JSONObject(s);
            String symbol = jStock.getString("symbol");
            String name = jStock.getString("companyName");
            double price = jStock.getDouble("latestPrice");
            double change = jStock.getDouble("change");
            double changePercent = jStock.getDouble("changePercent");
            stockInfo = new Stock(symbol, name, price, change, changePercent);
            Log.d(TAG, "Stock Made!");

        } catch (Exception e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
        return stockInfo;
    }
}
