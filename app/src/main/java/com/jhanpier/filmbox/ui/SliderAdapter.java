package com.jhanpier.filmbox.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.jhanpier.filmbox.R;

import java.util.List;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder> {

    private List<Integer> images;

    public SliderAdapter(List<Integer> images) { this.images = images; }

    public static class SliderViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.sliderImage);
        }
    }

    @NonNull
    @Override
    public SliderAdapter.SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_slider, parent, false);
        return new SliderViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderAdapter.SliderViewHolder holder, int position) {
        holder.image.setImageResource(images.get(position));
    }

    @Override
    public int getItemCount() { return images.size(); }
}
