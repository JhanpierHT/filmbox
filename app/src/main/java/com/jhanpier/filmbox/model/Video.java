package com.jhanpier.filmbox.model;

public class Video {

    private String title;
    private String url;
    private String thumbnailUrl;

    // Constructor equivalente al data class de Kotlin
    public Video(String title, String url, String thumbnailUrl) {
        this.title = title;
        this.url = url;
        this.thumbnailUrl = thumbnailUrl;
    }

    // Constructor vacío opcional
    public Video() {
        this.title = "";
        this.url = "";
        this.thumbnailUrl = "";
    }

    // -------- Getters --------

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    // -------- Setters --------

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }
}
