package com.jhanpier.filmbox.network;

import com.jhanpier.filmbox.model.MovieResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MovieService {

    @GET("/")
    Call<MovieResponse> searchMovies(
            @Query("apikey") String apiKey,
            @Query("s") String query
    );

    @GET("/")
    Call<MovieResponse> getPopularMovies(
            @Query("apikey") String apiKey,
            @Query("s") String query
    );
}
