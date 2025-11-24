package com.jhanpier.filmbox.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jhanpier.filmbox.R;
import com.jhanpier.filmbox.database.VideoDatabaseHelper;
import com.jhanpier.filmbox.model.Movie;
import com.jhanpier.filmbox.model.MovieResponse;
import com.jhanpier.filmbox.network.MovieService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeActivity extends AppCompatActivity {

    private final String apiKey = "d9ff1c9c";
    private RecyclerView recyclerView;
    private ApiMovieAdapter movieAdapter;
    private final List<Movie> moviesList = new ArrayList<>();
    private VideoDatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        recyclerView = findViewById(R.id.rvMovies);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        movieAdapter = new ApiMovieAdapter(moviesList);
        recyclerView.setAdapter(movieAdapter);

        BottomNavigationView bottom = findViewById(R.id.bottomNavigationView);
        bottom.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) return true;
            else if (id == R.id.nav_video) {
                VideoDatabaseHelper db = new VideoDatabaseHelper(this);
                db.resetDatabaseManual();
                startActivity(new Intent(this, VideosActivity.class));
                return true;
            } else if (id == R.id.nav_favorite) {
                startActivity(new Intent(this, FavoritesActivity.class));
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return false;
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.omdbapi.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MovieService service = retrofit.create(MovieService.class);
        service.searchMovies(apiKey, "Avengers").enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getSearch() != null) {
                    moviesList.clear();
                    moviesList.addAll(response.body().getSearch());
                    movieAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Log.e("API", "Error: " + t.getMessage());
            }
        });
    }
}
