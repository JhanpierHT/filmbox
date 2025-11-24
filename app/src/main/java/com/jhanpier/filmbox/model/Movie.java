package com.jhanpier.filmbox.model;

import com.google.gson.annotations.SerializedName;

public class Movie {

    // ----------- Campos para API OMDb -----------
    @SerializedName("Title")
    private String title;

    @SerializedName("Year")
    private String year;

    @SerializedName("Poster")
    private String imageUrl;

    // ----------- Campos locales -----------
    private String description;
    private String authors;
    private String videoUrl;

    private String category;

    // ----------- Constructor equivalente al data class de Kotlin -----------
    public Movie(String title, String year, String description,
                 String authors, String videoUrl, String imageUrl, String category) {

        this.title = title != null ? title : "";
        this.year = year != null ? year : "";
        this.description = description != null ? description : "";
        this.authors = authors != null ? authors : "";
        this.videoUrl = videoUrl != null ? videoUrl : "";
        this.imageUrl = imageUrl != null ? imageUrl : "";
        this.category = category != null ? category : "";
    }

    // Constructor vacío opcional (útil para Firebase o Gson)
    public Movie() {
        this.title = "";
        this.year = "";
        this.description = "";
        this.authors = "";
        this.videoUrl = "";
        this.imageUrl = "";
        this.category = "";
    }

    // ----------- Getters -----------

    public String getTitle() {
        return title;
    }

    public String getYear() {
        return year;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public String getAuthors() {
        return authors;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getCategory() {
        return category;
    }

    // ----------- Setters opcionales -----------

    public void setTitle(String title) {
        this.title = title;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
