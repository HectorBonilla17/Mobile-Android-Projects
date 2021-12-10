package com.example.stockwatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private static final String TAG = "MainActivity";

    List<Stock> stockList = new ArrayList<>();

    private NameDownloaderRunnable stockNames = null;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swiper;
    private StockAdapter sAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler);
        sAdapter = new StockAdapter(stockList, this);
        recyclerView.setAdapter(sAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        swiper = findViewById(R.id.swiper);
        swiper.setOnRefreshListener(this::doRefresh);

        loadFile();
    }

    //----------------------------------------------------------------
    /*Network Checker Method*/

    //Returns true or false, based on whether the device has a network connection
    private boolean doNetCheck() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            Toast.makeText(this, "Cannot access ConnectivityManager", Toast.LENGTH_SHORT).show();
            return false;
        }

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            if(stockNames == null){
                stockNames = new NameDownloaderRunnable(this);
                new Thread(stockNames).start();
            }
            return true;
        } else {
            return false;
        }
    }

    //----------------------------------------------------------------
    /*JSON Related Methods (i.e loading, refreshing, and saving stock information)*/

    //Refreshes the stocks with update-to-date data on swipe-down
    private void doRefresh() {
        swiper.setRefreshing(false);
        Log.d(TAG, "Updating");

        HashMap<String, String> tempStockList = new HashMap<>();
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
                String symbol = jsonObject.getString("symbol");
                String name = jsonObject.getString("name");
                tempStockList.put(symbol, name);
            }

        } catch (FileNotFoundException e) {
            Log.d(TAG, "loadFile: No JSON File Found, Creating JSON File");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(doNetCheck()) {
            stockList.clear();
            for (Map.Entry<String, String> stock : tempStockList.entrySet()) {
                processStock(stock.getKey());
            }
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Network Connection");
            builder.setMessage("Stocks Cannot Be Updated Without A Network Connection");
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    //Loads the JSON file of the program, loads stocks differently depending if there is an internet connection
    private void loadFile() {
        Log.d(TAG, "loadFile: Loading JSON File");

        HashMap<String, String> tempStockList = new HashMap<>();
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
                String symbol = jsonObject.getString("symbol");
                String name = jsonObject.getString("name");
                tempStockList.put(symbol, name);
            }

        } catch (FileNotFoundException e) {
            Log.d(TAG, "loadFile: No JSON File Found, Creating JSON File");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(doNetCheck()) {
            for (Map.Entry<String, String> stock : tempStockList.entrySet()) {
                processStock(stock.getKey());
            }
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Network Connection");
            builder.setMessage("Stocks Cannot Be Loaded Properly Without A Network Connection");
            AlertDialog dialog = builder.create();
            dialog.show();

            for (Map.Entry<String, String> stock : tempStockList.entrySet()) {
                stockList.add(new Stock(stock.getKey(), stock.getValue(), 0, 0, 0));
            }

            Collections.sort(stockList);
            sAdapter.notifyDataSetChanged();
        }
    }

    @Override //Save the current stock list into the JSON file whenever the app is paused
    protected void onPause() {
        saveStock();
        super.onPause();
    }

    //Method called to save the current stock list into the JSON file
    private void saveStock() {
        Log.d(TAG, "saveStock: Saving JSON File");
        try {
            FileOutputStream fos = getApplicationContext().openFileOutput(getString(R.string.file_name), Context.MODE_PRIVATE);
            PrintWriter printWriter = new PrintWriter(fos);
            printWriter.print(stockList);
            printWriter.close();
            fos.close();
            Log.d(TAG, "saveStock: JSON:\n" + stockList.toString());
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    //----------------------------------------------------------------
    /*Menu Button Methods*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.opt_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override //Menu Button method to prompt addStock method
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.addStockButton) {
            LayoutInflater inflater = LayoutInflater.from(this);
            final View view = inflater.inflate(R.layout.dialog, null);

            if(doNetCheck()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setView(view);
                builder.setTitle("Stock Selection");
                builder.setMessage("Please enter a Stock Symbol:");
                builder.setPositiveButton("Ok", (dialog, id) -> {
                            EditText symbol = view.findViewById(R.id.stockInput);
                            char [] symbolText = symbol.getText().toString().toCharArray();
                            for(Character i: symbolText){
                                if(Character.isLowerCase(i)){
                                    invalidStockInput();
                                    return;
                                }
                            }
                            addStock(symbol.getText().toString());
                        }
                );
                builder.setNegativeButton("Cancel", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("No Network Connection");
                builder.setMessage("Stocks Cannot Be Added Without A Network Connection");
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    //Dialog that alerts the user of invalid symbol input, that is if a lowercase character was inputted in addStockButton's EditText
    public void invalidStockInput() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Can't Accept Symbol Input With Lowercase Letter");
        builder.setPositiveButton("OK", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //----------------------------------------------------------------
    /*Screen Click Methods*/

    @Override //Goes to stock's MarketWatch page
    public void onClick(View v) {
        int pos = recyclerView.getChildLayoutPosition(v);
        Stock s = stockList.get(pos);

        if(doNetCheck()) {
            String DATA_URL = "http://www.marketwatch.com/investing/stock/" + s.getSymbol();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(DATA_URL));

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Network Connection");
            builder.setMessage("Cannot Go To Stock's MarketWatch Without A Network Connection");
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    @Override //Delete Stock Method
    public boolean onLongClick(View v) {
        int pos = recyclerView.getChildLayoutPosition(v);
        Stock s = stockList.get(pos);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Stock");
        builder.setMessage("Delete Stock Symbol " + s.getSymbol() + "?");
        builder.setIcon(R.drawable.ic_action_delete);
        builder.setPositiveButton("Yes", (dialog, id) -> {
            stockList.remove(pos);
            saveStock();
            sAdapter.notifyItemRemoved(pos);
        });
        builder.setNegativeButton("No", null);
        AlertDialog dialog = builder.create();
        dialog.show();
        return false;
    }

    //----------------------------------------------------------------
    /*Stock Data Processing Methods*/

    //Gets String stockSymbol and then get the stocks whose getSymbol matches stockSymbol, then, based on the size of the hashmap returned, either returns on error,
    //processes the stockSymbol, or gives a list stocks with the same starting symbol letters to be chosen from and then the chosen one is processed
    public void addStock (String stockSymbol){
        HashMap<String, String> matchedStocks = stockNames.getMatchingStockNames(stockSymbol);
        int stockCount = matchedStocks.size();

        if(stockCount == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Symbol Not Found: " + stockSymbol);
            builder.setMessage("No data found for stock symbol");
            AlertDialog dialog = builder.create();
            dialog.show();
        } else if (stockCount == 1) {
            processStock(stockSymbol);
        } else {
            //Covert Hashmap matchedStocks to String [] dialogStockArray
            int arrayCount = 0;
            String [] dialogStockArray = new String[matchedStocks.size()];
            for (Map.Entry<String, String> i : matchedStocks.entrySet()) {
                String stockName = i.getValue();
                if (stockName.length() == 0){
                    stockName = "N/A";
                }
                dialogStockArray[arrayCount] = i.getKey() + " - " + stockName;
                arrayCount += 1;
            }
            Arrays.sort(dialogStockArray);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Choice Stock");
            builder.setItems(dialogStockArray, (dialog, which) -> {
                String choice = dialogStockArray[which];
                String [] split = choice.split(" -");
                processStock(split[0]);
            });

            builder.setNegativeButton("Nevermind",  null);
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    //Calls thread object to get stock information
    public void processStock(String stockSymbol) {
        StockDownloaderRunnable st = new StockDownloaderRunnable(this, stockSymbol);
        new Thread(st).start();
        Log.d(TAG, "execRunnable: Getting Stock Information");
    }

    //Gets stock from StockDownloaderRunnable
    public void acceptResult(Stock stock) {
        for(Stock s: stockList){
            if(s.getSymbol().equals(stock.getSymbol())){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Duplicate Stock");
                builder.setIcon(R.drawable.ic_action_warning);
                builder.setMessage("Stock Symbol " + stock.getSymbol() + " is already displayed");
                AlertDialog dialog = builder.create();
                dialog.show();
                return;
            }
        }

        stockList.add(stock);
        Collections.sort(stockList);
        saveStock();
        sAdapter.notifyDataSetChanged();
        Log.d(TAG, "Got stock");
    }

}