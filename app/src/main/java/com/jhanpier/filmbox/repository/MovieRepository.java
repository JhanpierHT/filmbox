package com.jhanpier.filmbox.repository;

import com.jhanpier.filmbox.model.MovieResponse;
import com.jhanpier.filmbox.network.MovieService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieRepository {

    private final MovieService movieService;

    public MovieRepository(MovieService movieService) {
        this.movieService = movieService;
    }

    public void getPopularMovies(
            String apiKey,
            OnSuccessListener onSuccess,
            OnErrorListener onError
    ) {
        Call<MovieResponse> call = movieService.getPopularMovies(apiKey, "popular");

        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    onSuccess.onSuccess(response.body());
                } else {
                    onError.onError(new Throwable("Response not successful: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                onError.onError(t);
            }
        });
    }

    // ---------- Interfaces para los callbacks ----------

    public interface OnSuccessListener {
        void onSuccess(MovieResponse response);
    }

    public interface OnErrorListener {
        void onError(Throwable t);
    }
}
