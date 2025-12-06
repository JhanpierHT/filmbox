package com.jhanpier.filmbox.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jhanpier.filmbox.R;
import com.jhanpier.filmbox.model.Movie;

import java.util.List;

public class SliderAdapterHome extends RecyclerView.Adapter<SliderAdapterHome.SliderViewHolder> {

    public interface OnSliderClick {
        void onClick(Movie movie);
    }

    private final List<Movie> movies;
    private final OnSliderClick listener;

    public SliderAdapterHome(List<Movie> movies, OnSliderClick listener) {
        this.movies = movies;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_slider_home, parent, false);
        return new SliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        Movie movie = movies.get(position);

        // Cargar imagen del slider
        Glide.with(holder.itemView.getContext())
                .load(movie.getImageUrl())
                .into(holder.imgSlider);

        // Click en botón play
        holder.btnPlay.setOnClickListener(v -> {
            if (listener != null) listener.onClick(movie);
        });

        // Click general
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(movie);
        });
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    static class SliderViewHolder extends RecyclerView.ViewHolder {

        ImageView imgSlider;
        ImageButton btnPlay;

        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);

            imgSlider = itemView.findViewById(R.id.imgSlider);
            btnPlay = itemView.findViewById(R.id.btnPlaySlider);
        }
    }
}
