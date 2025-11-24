package com.jhanpier.filmbox.ui;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jhanpier.filmbox.R;
import com.jhanpier.filmbox.ui.VideoPlayerActivity;
import com.jhanpier.filmbox.model.Movie;

import java.util.List;

public class ApiMovieAdapter extends RecyclerView.Adapter<ApiMovieAdapter.ViewHolder> {

    private final List<Movie> movies;

    public ApiMovieAdapter(List<Movie> movies) {
        this.movies = movies;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView img;
        public TextView title;

        public ViewHolder(@NonNull View v) {
            super(v);
            img = v.findViewById(R.id.imgThumbnail);
            title = v.findViewById(R.id.tvTitle);
        }
    }

    @NonNull
    @Override
    public ApiMovieAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ApiMovieAdapter.ViewHolder holder, int position) {
        final Movie m = movies.get(position);
        holder.title.setText(m.getTitle());

        Glide.with(holder.itemView.getContext()).load(m.getImageUrl()).into(holder.img);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ApiMovieAdapter", "Clic en: " + m.getTitle() + ", videoUrl: " + m.getVideoUrl());
                if (m.getVideoUrl() != null && !m.getVideoUrl().trim().isEmpty()) {
                    Context context = holder.itemView.getContext();
                    Intent intent = new Intent(context, VideoPlayerActivity.class);
                    intent.putExtra("videoUrl", m.getVideoUrl());
                    intent.putExtra("title", m.getTitle());
                    intent.putExtra("description", m.getDescription() != null ? m.getDescription() : "Sin descripción");
                    context.startActivity(intent);
                } else {
                    Toast.makeText(holder.itemView.getContext(), "Video no disponible", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }
}
