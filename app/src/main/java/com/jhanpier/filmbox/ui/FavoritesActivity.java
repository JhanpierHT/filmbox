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

        setupBottomNavigation();
    }

    // 🌟 Aseguramos la recarga de favoritos cada vez que la actividad se hace visible
    @Override
    protected void onResume() {
        super.onResume();
        loadFavorites();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        // ... (Selección del ítem actual)
        // ✅ CORRECCIÓN AÑADIDA: Establecer el ítem de 'Favoritos' como seleccionado
        bottomNavigationView.setSelectedItemId(R.id.nav_favorite);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Intent intent = null;

            if (id == R.id.nav_home) {
                intent = new Intent(this, HomeActivity.class);
            } else if (id == R.id.nav_video) {
                intent = new Intent(this, VideosActivity.class);
            } else if (id == R.id.nav_favorite) {
                intent = new Intent(this, FavoritesActivity.class);
            } else if (id == R.id.nav_profile) {
                intent = new Intent(this, ProfileActivity.class);
            } else {
                return true; // Ya estamos en esta actividad o el ID no es de navegación.
            }

            if (intent != null) {
                // ✅ MANTENEMOS: CLEAR_TOP y NEW_TASK
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                // ❌ ELIMINAR ESTA LÍNEA en todas las pestañas:
                // finish();

                return true;
            }
            return false;
        });
    }


    private void loadFavorites() {
        List<Movie> favorites = dbHelper.getAllFavorites();

        // 💡 OPTIMIZACIÓN: Si el adaptador ya existe, solo actualiza la lista.
        if (movieAdapter == null) {
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
                            // Al eliminar, recargamos inmediatamente la lista.
                            dbHelper.removeFavorite(movie);
                            loadFavorites();
                        }
                    },
                    new VideoAdapter.IsFavoriteChecker() {
                        @Override
                        public boolean isFav(Movie movie) { return true; } // Siempre es favorito en esta vista
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
        } else {
            // Suponiendo que VideoAdapter tiene un método updateList()
            movieAdapter.updateList(favorites);
            movieAdapter.notifyDataSetChanged();
        }
    }
}