package com.jhanpier.filmbox.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MovieResponse {

    @SerializedName("Search")
    private List<Movie> search;

    public MovieResponse(List<Movie> search) {
        this.search = search;
    }

    public MovieResponse() {
        this.search = null;
    }

    public List<Movie> getSearch() {
        return search;
    }

    public void setSearch(List<Movie> search) {
        this.search = search;
    }
}
