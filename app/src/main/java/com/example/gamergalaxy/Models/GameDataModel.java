package com.example.gamergalaxy.Models;

import java.util.ArrayList;

public class GameDataModel {
    private String gameId;
    private String gameName;
    private String gameSummary;
    private String image;
    private String gameReleaseDate;
    private String gameMp4Link;
    private String gameMetacriticScore;
    private ArrayList<String> gameGenres;
    private boolean isLiked;


    public GameDataModel(String gameId, String gameName, String gameSummary, String image, String gameReleaseDate, String gameMp4Link, String gameMetacriticScore, ArrayList<String> gameGenres) {
        this.gameId = gameId; // Initialize gameId in the constructor
        this.gameName = gameName;
        this.gameSummary = gameSummary;
        this.image = image;
        this.gameReleaseDate = gameReleaseDate;
        this.gameMp4Link = gameMp4Link;
        this.gameMetacriticScore = gameMetacriticScore;
        this.gameGenres = gameGenres;
    }
    public String getGameId() {return gameId;}
    public String getGameName() {
        return gameName;
    }

    public String getGameSummary() {
        return gameSummary;
    }

    public String getImage() {
        return image;
    }

    public String getGameReleaseDate() {
        return gameReleaseDate;
    }

    public String getGameMp4Link() {
        return gameMp4Link;
    }

    public String getGameMetacriticScore() {
        return gameMetacriticScore;
    }

    public ArrayList<String> getGameGenres() {
        return gameGenres;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    @Override
    public String toString() {
        return "GameDataModel{" +
                "gameName='" + gameName + '\'' +
                ", gameSummary='" + gameSummary + '\'' +
                ", imageUrl='" + image + '\'' +
                ", releaseDate='" + gameReleaseDate + '\'' +
                ", webm480Link='" + gameMp4Link + '\'' +
                ", metacriticScore='" + gameMetacriticScore + '\'' +
                '}';
    }
}
