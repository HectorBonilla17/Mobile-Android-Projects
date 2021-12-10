package com.example.stockwatch;

import android.util.JsonWriter;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.StringWriter;

public class Stock implements Comparable<Stock>{

    private final String symbol;
    private final String companyName;
    private final double price;
    private final double priceChange;
    private final double changePercentage;

    public Stock(String symbol, String companyName, double price, double priceChange, double changePercentage) {
        this.symbol = symbol;
        this.companyName = companyName;
        this.price = price;
        this.priceChange = priceChange;
        this.changePercentage = changePercentage;
    }

    String getSymbol() {
        return symbol;
    }
    String getCompanyName() {
        return companyName;
    }
    double getPrice()  {return price; }
    double getPriceChange() { return priceChange; }
    double getChangePercentage() { return changePercentage; }

    @NonNull
    public String toString() {
        try {
            StringWriter sw = new StringWriter();
            JsonWriter jsonWriter = new JsonWriter(sw);
            jsonWriter.setIndent("  ");
            jsonWriter.beginObject();

            jsonWriter.name("symbol").value(getSymbol());
            jsonWriter.name("name").value(getCompanyName());

            jsonWriter.endObject();
            jsonWriter.close();
            return sw.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public int compareTo(Stock o) {
        return getSymbol().compareTo(o.getSymbol());
    }
}
