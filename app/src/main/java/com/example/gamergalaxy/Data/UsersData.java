package com.example.gamergalaxy.Data;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class UsersData {

    public String email;
    public String password;
    public String id;
    public String profileImage;
    private Map<String, Boolean> likedGames;

    public UsersData(){
    }
    public UsersData(String email, String id, String imageUrl) {
        this.email = email;
        this.id = id;
        this.profileImage = imageUrl;
        this.likedGames = new HashMap<>(); // Initialize with an empty map
    }
    public UsersData(String email, String id) {
        this.email = email;
        this.id = id;
    }
    public void likeGame(String gameId) {
        likedGames.put(gameId, true);
    }
    public void unlikeGame(String gameId) {
        likedGames.remove(gameId);
    }
    public Map<String, Boolean> getLikedGames() {
        return likedGames;
    }
    public void setLikedGames(Map<String, Boolean> likedGames) {
        this.likedGames = likedGames;
    }
    @Override
    public String toString() {
        return "Data{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", imageUrl='" + profileImage + '\'' +
                '}';
    }
}
