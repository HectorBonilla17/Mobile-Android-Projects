package com.example.civiladvocacy;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OfficialAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private static final String TAG = "OfficialAdapter";
    private final List<Official> officialList;
    private final MainActivity mainAct;

    public OfficialAdapter(List<Official> oList, MainActivity mainActivity) {
        this.officialList = oList;
        this.mainAct = mainActivity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: MAKING NEW MyViewHolder");

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.official_list_row, parent, false);

        itemView.setOnClickListener(mainAct);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: FILLING VIEW HOLDER Official " + position);
        Official currentOfficial = officialList.get(position);

        holder.officeName.setText(currentOfficial.getOfficeName());
        holder.officialName.setText(currentOfficial.getOfficialName());
        holder.partName.setText("(" + currentOfficial.getPartyName() + ")");
    }

    @Override
    public int getItemCount() {
        return officialList.size();
    }
}
