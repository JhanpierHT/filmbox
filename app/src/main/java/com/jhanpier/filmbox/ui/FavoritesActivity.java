package com.jhanpier.filmbox.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jhanpier.filmbox.R;
import com.jhanpier.filmbox.ui.VideoPlayerActivity;
import com.jhanpier.filmbox.database.VideoDatabaseHelper;
import com.jhanpier.filmbox.model.Movie;

import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    private VideoDatabaseHelper dbHelper;
    private VideoAdapter movieAdapter;
    private RecyclerView rvFavorites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        dbHelper = new VideoDatabaseHelper(this);
        rvFavorites = findViewById(R.id.rvFavorites);

        loadFavorites();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_favorite);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
                return true;
            } else if (id == R.id.nav_video) {
                startActivity(new Intent(this, VideosActivity.class));
                return true;
            } else if (id == R.id.nav_favorite) {
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return false;
        });
    }

    private void loadFavorites() {
        List<Movie> favorites = dbHelper.getAllFavorites();

        movieAdapter = new VideoAdapter(favorites,
                new VideoAdapter.OnMoreInfoClick() {
                    @Override
                    public void onClick(Movie movie) {
                        Intent intent = new Intent(FavoritesActivity.this, MovieDetailActivity.class);
                        intent.putExtra("title", movie.getTitle());
                        intent.putExtra("year", movie.getYear());
                        intent.putExtra("description", movie.getDescription());
                        intent.putExtra("authors", movie.getAuthors());
                        intent.putExtra("videoUrl", movie.getVideoUrl());
                        intent.putExtra("imageUrl", movie.getImageUrl());
                        startActivity(intent);
                    }
                },
                new VideoAdapter.OnFavoriteClick() {
                    @Override
                    public void onClick(Movie movie) {
                        dbHelper.removeFavorite(movie);
                        loadFavorites();
                    }
                },
                new VideoAdapter.IsFavoriteChecker() {
                    @Override
                    public boolean isFav(Movie movie) { return true; }
                },
                new VideoAdapter.OnVideoClick() {
                    @Override
                    public void onClick(Movie movie) {
                        Intent intent = new Intent(FavoritesActivity.this, VideoPlayerActivity.class);
                        intent.putExtra("videoUrl", movie.getVideoUrl());
                        startActivity(intent);
                    }
                });

        rvFavorites.setLayoutManager(new GridLayoutManager(this, 2));
        rvFavorites.setAdapter(movieAdapter);
    }
}
