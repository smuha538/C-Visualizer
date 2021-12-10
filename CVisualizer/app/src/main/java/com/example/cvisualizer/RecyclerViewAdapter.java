package com.example.cvisualizer;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/*
 * This class is the adapter that binds together the recycleview of the activity_color_edit.xml with the
 * layout_storedimages to show favourite colors from the database passed in as a list from the parent class.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private ArrayList<Integer> favColors;
    private Context context;
    private int colors;

    public RecyclerViewAdapter(Context context, ArrayList<Integer> favColors) {
        this.favColors = favColors;
        this.context = context;

    }

    /**
     * This inflates the layout to display the the contents within the recyclerview. Initializes ViewHolder
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_color_fav, parent, false);
        return new ViewHolder(view);
    }

    /**
     * This method binds the data set values/contents (based on position in the arraylist) to the view.
     * The data is passed to the viewHolder
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        colors = favColors.get(position);
        holder.color.setBackgroundColor(colors);
    }

    /**
     * Gets the size of the favColors ArrayList
     * This method returns the size of the collection that contains the items we want to display.
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return favColors.size();
    }

    /**
     * Object that represents each item in the collection
     */
    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView color;


        public ViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  int color = favColors.get(getBindingAdapterPosition());
                    int r = (color>>16)&0xff;
                    int g = (color>>8)&0xff;
                    int b = color&0xff;
                    String currentColour = r + "," + g + "," + b;
                    Toast.makeText(context, currentColour, Toast.LENGTH_LONG).show();
                }
            });
            color = itemView.findViewById(R.id.favColor);
        }
    }
}
