package com.jhanpier.filmbox.model;

import java.util.List;

public class MovieResponse {

    private List<Movie> movies;

    public MovieResponse(List<Movie> movies) {
        this.movies = movies;
    }

    public MovieResponse() {}

    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }
}
