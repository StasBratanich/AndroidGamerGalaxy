package com.example.gamergalaxy.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gamergalaxy.Models.EntranceDataModel;
import com.example.gamergalaxy.R;

import java.util.ArrayList;

public class CustomEntranceAdapter extends RecyclerView.Adapter<CustomEntranceAdapter.MyViewHolder> {

    ArrayList<EntranceDataModel> dataset;

    public CustomEntranceAdapter(ArrayList<EntranceDataModel> dataset) {
        this.dataset = dataset;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textView);
        }
    }

    @NonNull
    @Override
    public CustomEntranceAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.entrance_card_layout, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomEntranceAdapter.MyViewHolder holder, int position) {
        ImageView imageView = holder.imageView;
        TextView textViewSummary = holder.textView;

        imageView.setImageResource(dataset.get(position).getImage());
        textViewSummary.setText(dataset.get(position).getSummary());
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }
}
