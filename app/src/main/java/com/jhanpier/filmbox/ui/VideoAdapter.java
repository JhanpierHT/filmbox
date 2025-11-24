package com.jhanpier.filmbox.ui;

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

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.MovieViewHolder> {

    private List<Movie> movies;
    private OnMoreInfoClick onMoreInfoClick;
    private OnFavoriteClick onFavoriteClick;
    private IsFavoriteChecker isFavoriteChecker;
    private OnVideoClick onVideoClick;

    public interface OnMoreInfoClick { void onClick(Movie movie); }
    public interface OnFavoriteClick { void onClick(Movie movie); }
    public interface IsFavoriteChecker { boolean isFav(Movie movie); }
    public interface OnVideoClick { void onClick(Movie movie); }

    public VideoAdapter(List<Movie> movies,
                        OnMoreInfoClick onMoreInfoClick,
                        OnFavoriteClick onFavoriteClick,
                        IsFavoriteChecker isFavoriteChecker,
                        OnVideoClick onVideoClick) {
        this.movies = movies;
        this.onMoreInfoClick = onMoreInfoClick;
        this.onFavoriteClick = onFavoriteClick;
        this.isFavoriteChecker = isFavoriteChecker;
        this.onVideoClick = onVideoClick;
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPoster;
        ImageButton btnFavorite, btnMoreInfo;
        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPoster = itemView.findViewById(R.id.imgThumbnail);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
            btnMoreInfo = itemView.findViewById(R.id.btnMoreInfo);
        }
    }

    @NonNull
    @Override
    public VideoAdapter.MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
        return new MovieViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoAdapter.MovieViewHolder holder, int position) {
        final Movie movie = movies.get(position);

        holder.imgPoster.setOnClickListener(v -> onVideoClick.onClick(movie));
        Glide.with(holder.itemView.getContext()).load(movie.getImageUrl()).into(holder.imgPoster);

        boolean isFav = isFavoriteChecker.isFav(movie);
        holder.btnFavorite.setImageResource(isFav ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);

        holder.btnFavorite.setOnClickListener(v -> onFavoriteClick.onClick(movie));
        holder.btnMoreInfo.setOnClickListener(v -> onMoreInfoClick.onClick(movie));
    }

    @Override
    public int getItemCount() { return movies.size(); }

    public void updateList(List<Movie> newMovies) {
        this.movies = newMovies;
        notifyDataSetChanged();
    }
}
