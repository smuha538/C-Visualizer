package com.example.cvisualizer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private ArrayList<Integer> favColors;
    private Context context;
    private int colors;

    public RecyclerViewAdapter(Context context, ArrayList<Integer> favColors) {
        this.favColors = favColors;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_color_fav, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        colors = favColors.get(position);
        holder.color.setBackgroundColor(colors);
    }

    @Override
    public int getItemCount() {
        return favColors.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView color;

        public ViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent i = new Intent(v.getContext(),ColorSelector.class);
//                    i.putExtras("color",favColors.get(getBindingAdapterPosition()));


                }
            });
            color = itemView.findViewById(R.id.favColor);
        }
    }
}
