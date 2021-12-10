package com.example.civiladvocacy;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder {

    TextView officeName;
    TextView officialName;
    TextView partName;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        officeName = itemView.findViewById(R.id.officeName);
        officialName = itemView.findViewById(R.id.officialName);
        partName = itemView.findViewById(R.id.partyName);
    }
}
