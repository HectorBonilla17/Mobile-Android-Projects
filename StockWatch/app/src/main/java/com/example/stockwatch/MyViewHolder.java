package com.example.stockwatch;

import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder{

    TextView stockSymbol;
    TextView stockName;
    TextView stockPrice;
    TextView priceChange;
    TextView changePercentage;

    MyViewHolder(View view) {
        super(view);
        stockSymbol = view.findViewById(R.id.stockSymbol);
        stockName = view.findViewById(R.id.stockName);
        stockPrice = view.findViewById(R.id.stockPrice);
        priceChange = view.findViewById(R.id.priceChange);
        changePercentage = view.findViewById(R.id.percentChange);
    }
}
