package com.jhanpier.filmbox.model;

public class Video {

    private String title;
    private String url;
    private String thumbnailUrl;

    public Video(String title, String url, String thumbnailUrl) {
        this.title = title;
        this.url = url;
        this.thumbnailUrl = thumbnailUrl;
    }

    public Video() {
        this.title = "";
        this.url = "";
        this.thumbnailUrl = "";
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }


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
