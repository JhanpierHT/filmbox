package com.jhanpier.filmbox.model;

import com.google.gson.annotations.SerializedName;

public class Movie {
    @SerializedName("Title")
    private String title;

    @SerializedName("Year")
    private String year;

    @SerializedName("Poster")
    private String imageUrl;

    private String description;
    private String authors;
    private String videoUrl;
    private String category;

    private int progress;

    public Movie(String title, String year, String description, String authors,
                 String videoUrl, String imageUrl, String category, int progress) {
        this.title = title;
        this.year = year;
        this.description = description;
        this.authors = authors;
        this.videoUrl = videoUrl;
        this.imageUrl = imageUrl;
        this.category = category;
        this.progress = progress;
    }



    public Movie(String title, String year, String description, String authors,
                 String videoUrl, String imageUrl, String category) {
        this(title, year, description, authors, videoUrl, imageUrl, category, 0);
    }


    public Movie() {
        this.title = "";
        this.year = "";
        this.description = "";
        this.authors = "";
        this.videoUrl = "";
        this.imageUrl = "";
        this.category = "";
        this.progress = 0;
    }


    public String getTitle() { return title; }
    public String getYear() { return year; }
    public String getImageUrl() { return imageUrl; }
    public String getDescription() { return description; }
    public String getAuthors() { return authors; }
    public String getVideoUrl() { return videoUrl; }
    public String getCategory() { return category; }
    public int getProgress() { return progress; }

    public void setTitle(String title) { this.title = title; }
    public void setYear(String year) { this.year = year; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setDescription(String description) { this.description = description; }
    public void setAuthors(String authors) { this.authors = authors; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }
    public void setCategory(String category) { this.category = category; }
    public void setProgress(int progress) { this.progress = progress; }
}
