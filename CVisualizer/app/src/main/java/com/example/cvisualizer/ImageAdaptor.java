/*
This class is the adapter that binds together the recycleview of the activity_storage.xml with the
layout_storedimages to show saved pictures from the database passed in as an arraylist from the parent class.
 */
package com.example.cvisualizer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * This body of code was borrowed from:
 * https://www.geeksforgeeks.org/how-to-view-all-the-uploaded-images-in-firebase-storage/
 */
class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private ArrayList<String> imageList;
    private Context context;

    public ImageAdapter(ArrayList<String> imageList, Context context) {
        this.imageList = imageList;
        this.context = context;
    }

    /**
     * This inflates the layout to display the the contents within the recyclerview. Initializes ViewHolder
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_storedimages,parent,false);
        return new ViewHolder(view);
    }

    /**
     * This method binds the data set values/contents (based on position in the arraylist) to the view.
     * The data is passed to the viewHolder
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ImageAdapter.ViewHolder holder, int position) {
        Glide.with(holder.itemView.getContext()).load(imageList.get(position)).into(holder.imageView);
    }

    /**
     * Gets the size of the imageList ArrayList
     * This method returns the size of the collection that contains the items we want to display.
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return imageList.size();
    }

    /**
     * Object that represents each item in our collection
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.item);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String strUri = imageList.get(getBindingAdapterPosition());
                    Intent i = new Intent(v.getContext(), ImageDetail.class);
                    Bundle bun = new Bundle();
                    bun.putString("path", strUri);
                    i.putExtras(bun);
                    v.getContext().startActivity(i);
                }
            });

        }
    }
}
