package com.example.gamergalaxy.Services;

import android.content.Context;
import android.os.StrictMode;
import android.util.Log;

import com.example.gamergalaxy.Models.GameDataModel;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

public class GamesAPIService {
    private static final String TAG = "GamesAPIService";
    private static final String LOCAL_JSON_FILENAME = "app_data.json";
    private static ArrayList<GameDataModel> arrGames = new ArrayList<>();

    public static ArrayList<GameDataModel> getArrGames(Context context) throws IOException {
        if (isLocalJsonValid(context)) {
            arrGames = loadAppIdsFromLocalJson(context);
            Log.d("GameData", "Number of games fetched: " + arrGames.size());
        } else {
            arrGames = fetchGameDataFromSteam(context);
            Log.d("GameData", "Number of games fetched: " + arrGames.size());
            saveAppIdsToLocalJson(context, arrGames);
        }

        return arrGames;
    }

    private static boolean isLocalJsonValid(Context context) {
        File file = new File(context.getFilesDir(), LOCAL_JSON_FILENAME);
        return file.exists();
    }

    private static ArrayList<GameDataModel> loadAppIdsFromLocalJson(Context context) throws IOException {
        ArrayList<GameDataModel> games = new ArrayList<>();
        File file = new File(context.getFilesDir(), LOCAL_JSON_FILENAME);

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                json.append(line).append('\n');
            }

            JsonParser parser = new JsonParser();
            JsonElement jsonElement = parser.parse(json.toString());
            JsonArray jsonArray = jsonElement.getAsJsonArray();

            for (JsonElement element : jsonArray) {
                JsonObject jsonObject = element.getAsJsonObject();
                String gameId = jsonObject.get("gameId").getAsString();
                String gameName = jsonObject.get("name").getAsString();
                String gameSummary = jsonObject.get("short_description").getAsString();
                String imageUrl = jsonObject.get("header_image").getAsString();
                String releaseDate = jsonObject.get("release_date").getAsString();
                String mp4Link = jsonObject.get("mp4_link").getAsString();
                String metacriticScore = jsonObject.get("metacritic_score").getAsString();


                JsonArray genresArray = jsonObject.getAsJsonArray("genres");
                ArrayList<String> genresList = new ArrayList<>();
                for (JsonElement genreElement : genresArray) {
                    String genre = genreElement.getAsString();
                    genresList.add(genre);
                }


                GameDataModel game = new GameDataModel(gameId, gameName, gameSummary, imageUrl, releaseDate, mp4Link, metacriticScore, genresList);
                games.add(game);
            }
        }

        return games;
    }

    private static ArrayList<GameDataModel> fetchGameDataFromSteam(Context context) throws IOException {
        ArrayList<String> appIdList = getSteamSpyAppIds();
        arrGames = new ArrayList<>();

        for (String appID : appIdList) {
            String sURL = "https://store.steampowered.com/api/appdetails?appids=" + appID + "&filters=basic,release_date,genres,movies,metacritic";

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            try {
                URL url = new URL(sURL);
                HttpURLConnection request = (HttpURLConnection) url.openConnection();
                request.connect();

                JsonParser jp = new JsonParser();
                JsonObject root = jp.parse(new InputStreamReader(request.getInputStream())).getAsJsonObject();
                JsonObject appDetails = root.getAsJsonObject(appID);

                if (appDetails != null && appDetails.get("success").getAsBoolean()) {
                    JsonObject data = appDetails.getAsJsonObject("data");

                    String gameId = data.get("steam_appid").getAsString();
                    String gameName = data.get("name").getAsString();
                    String gameSummary = data.get("short_description").getAsString();
                    String imageUrl = data.get("header_image").getAsString();

                    String releaseDate;
                    try {
                        releaseDate = data.getAsJsonObject("release_date").get("date").getAsString();
                        if (releaseDate.isEmpty()) {
                            releaseDate = "No release Date information";
                        }
                    } catch (Exception e) {
                        releaseDate = "No release Date information";
                    }

                    ArrayList<String> genresList = new ArrayList<>();
                    try {
                        JsonArray genresArray = data.getAsJsonArray("genres");
                        for (JsonElement genreElement : genresArray) {
                            JsonObject genreObject = genreElement.getAsJsonObject();
                            String genre = genreObject.get("description").getAsString();
                            genresList.add(genre);
                        }
                    } catch (Exception e) {
                        genresList.add("0");  // Add "0" to the list if there's no genres data
                    }

                    String mp4Link = "0";  // Default to "0"
                    try {
                        JsonArray moviesArray = data.getAsJsonArray("movies");
                        for (JsonElement movieElement : moviesArray) {
                            JsonObject movieObject = movieElement.getAsJsonObject();
                            JsonObject mp4Object = movieObject.getAsJsonObject("mp4"); // Adjust the key if it's different
                            if (mp4Object != null && mp4Object.get("480") != null) {
                                mp4Link = mp4Object.get("480").getAsString();
                                if (mp4Link.startsWith("http://")) {
                                    mp4Link = mp4Link.replaceFirst("http://", "https://");
                                }
                                break;  // Found the 480p mp4 link, no need to continue iterating
                            }
                        }
                    } catch (Exception e) {
                        // mp4Link already defaults to "0" if there's no such data
                    }

                    String metacriticScore;
                    try {
                        metacriticScore = String.valueOf(data.getAsJsonObject("metacritic").get("score").getAsInt());
                    } catch (NullPointerException | IllegalStateException e) {
                        metacriticScore = "0";
                    }

                    GameDataModel game = new GameDataModel(appID, gameName, gameSummary, imageUrl, releaseDate, mp4Link, metacriticScore, genresList);

                    arrGames.add(game);
                }
            } catch (IOException e) {
                Log.e(TAG, "Error fetching game data for app ID: " + appID, e);
                // Consider handling specific exceptions like timeouts or server errors
                throw e; // Re-throw the exception to signal potential issues
            }
        }

        return arrGames;
    }

    private static void saveAppIdsToLocalJson(Context context, ArrayList<GameDataModel> games) throws IOException {
        Gson gson = new Gson();
        JsonArray jsonArray = new JsonArray();

        for (GameDataModel game : games) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("gameId", game.getGameId());
            jsonObject.addProperty("name", game.getGameName());
            jsonObject.addProperty("short_description", game.getGameSummary());
            jsonObject.addProperty("header_image", game.getImage());
            jsonObject.addProperty("release_date", game.getGameReleaseDate());
            jsonObject.add("genres", gson.toJsonTree(game.getGameGenres()));
            jsonObject.addProperty("mp4_link", game.getGameMp4Link());
            jsonObject.addProperty("metacritic_score", game.getGameMetacriticScore());
            jsonArray.add(jsonObject);
        }
        File file = new File(context.getFilesDir(), LOCAL_JSON_FILENAME);
        boolean shouldOverwrite = true;
        if (file.exists()) {
            long lastModified = file.lastModified();
            long currentTime = System.currentTimeMillis();
            long timeDifference = currentTime - lastModified;
            long twentyFourHoursInMillis = 24 * 60 * 60 * 1000;
            if (timeDifference < twentyFourHoursInMillis) {
                shouldOverwrite = false;
            }
        }
        if (shouldOverwrite) {
            try (FileWriter writer = new FileWriter(file)) {
                gson.toJson(jsonArray, writer);
            }
        }
    }

    public static ArrayList<String> getSteamSpyAppIds() {

        String sURL = "https://steamspy.com/api.php?request=top100in2weeks";
        ArrayList<String> appIdList = new ArrayList<>();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            URL url = new URL(sURL);
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.connect();

            JsonParser jp = new JsonParser();
            JsonObject root = jp.parse(new InputStreamReader(request.getInputStream())).getAsJsonObject();

            for (Map.Entry<String, JsonElement> entry : root.entrySet()) {
                String appID = entry.getKey();
                appIdList.add(appID);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Log.d("SteamSpyAppIDs", "AppID List: " + appIdList.toString());

        return appIdList;
    }
}
