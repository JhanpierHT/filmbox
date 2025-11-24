package com.jhanpier.filmbox.model;

public class Profile {
    private int id;
    private String name;
    private int avatarResId;

    public Profile() {}

    public Profile(int id, String name, int avatarResId) {
        this.id = id;
        this.name = name;
        this.avatarResId = avatarResId;
    }

    public Profile(String name, int avatarResId) {
        this.name = name;
        this.avatarResId = avatarResId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAvatarResId() { return avatarResId; }
    public void setAvatarResId(int avatarResId) { this.avatarResId = avatarResId; }
}
