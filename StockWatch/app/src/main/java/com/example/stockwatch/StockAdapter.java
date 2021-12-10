package com.example.stockwatch;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.List;

public class StockAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private static final String TAG = "StockAdapter";
    private final List<Stock> stockList;
    private final MainActivity mainAct;

    public StockAdapter(List<Stock> stockList, MainActivity mainActivity) {
        this.stockList = stockList;
        mainAct = mainActivity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: MAKING NEW MyViewHolder");

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_list_row, parent, false);

        itemView.setOnClickListener(mainAct);
        itemView.setOnLongClickListener(mainAct);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: FILLING VIEW HOLDER Note " + position);

        Stock currentStock = stockList.get(position);

        holder.stockSymbol.setText(currentStock.getSymbol());
        holder.stockName.setText(currentStock.getCompanyName());
        holder.stockPrice.setText(Double.toString(currentStock.getPrice()));

        String arrow = (currentStock.getPriceChange() > 0) ? "▲ " : "▼ ";
        holder.priceChange.setText(arrow + currentStock.getPriceChange());

        double changePercent = currentStock.getChangePercentage() * 100;
        holder.changePercentage.setText(String.format("(%.2f%%)", changePercent));

        if(currentStock.getPriceChange() > 0) {
            holder.stockSymbol.setTextColor(Color.parseColor("#00FF00"));
            holder.stockName.setTextColor(Color.parseColor("#00FF00"));
            holder.stockPrice.setTextColor(Color.parseColor("#00FF00"));
            holder.priceChange.setTextColor(Color.parseColor("#00FF00"));
            holder.changePercentage.setTextColor(Color.parseColor("#00FF00"));
        } else {
            holder.stockSymbol.setTextColor(Color.parseColor("#FF0000"));
            holder.stockName.setTextColor(Color.parseColor("#FF0000"));
            holder.stockPrice.setTextColor(Color.parseColor("#FF0000"));
            holder.priceChange.setTextColor(Color.parseColor("#FF0000"));
            holder.changePercentage.setTextColor(Color.parseColor("#FF0000"));
        }


    }

    @Override
    public int getItemCount() {
        return stockList.size();
    }
}
