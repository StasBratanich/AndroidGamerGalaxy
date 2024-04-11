package com.example.gamergalaxy.Models;

public class EntranceDataModel {
    private int image;
    private int id;
    private String summary;

    public EntranceDataModel(int image,String summary, int id) {
        this.image = image;
        this.summary = summary;
        this.id = id;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
