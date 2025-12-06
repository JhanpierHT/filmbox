package com.jhanpier.filmbox.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jhanpier.filmbox.R;
import com.jhanpier.filmbox.model.Movie;
import com.jhanpier.filmbox.ui.MovieDetailActivity;

import java.util.List;

public class TopAdapter extends RecyclerView.Adapter<TopAdapter.ViewHolder> {

    public interface OnMovieClick {
        void onClick(Movie movie);
    }

    private List<Movie> movieList;
    private Context context;
    private OnMovieClick listener;

    public TopAdapter(Context context, List<Movie> movieList, OnMovieClick listener) {
        this.context = context;
        this.movieList = movieList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_top_movie, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Movie movie = movieList.get(position);

        Glide.with(context)
                .load(movie.getImageUrl())
                .into(holder.imgTopMovie);


        holder.itemView.setOnClickListener(v -> listener.onClick(movie));
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgTopMovie;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgTopMovie = itemView.findViewById(R.id.imgTopMovie);
        }
    }
}
