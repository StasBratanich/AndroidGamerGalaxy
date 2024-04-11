package com.example.gamergalaxy.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gamergalaxy.Models.GameDataModel;
import com.example.gamergalaxy.R;

import java.util.ArrayList;
import java.util.List;

public class LikedGamesAdapter extends RecyclerView.Adapter<LikedGamesAdapter.ViewHolder> {

    private List<GameDataModel> gamesList;
    private Context context;

    public LikedGamesAdapter(Context context, List<GameDataModel> gamesList) {
        this.context = context;
        this.gamesList = gamesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_likedgame_details, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GameDataModel game = gamesList.get(position);

        holder.gameName.setText(game.getGameName());
        Glide.with(context).load(game.getImage()).into(holder.gameImage);
        holder.gameReleaseDate.setText(game.getGameReleaseDate());
        holder.gameMetacriticScore.setText("Metacritic score:" + String.valueOf(game.getGameMetacriticScore()));
        holder.gameGenres.setText(String.join(", ", game.getGameGenres()));
    }

    @Override
    public int getItemCount() {
        return gamesList.size();
    }

    public void setGamesList(List<GameDataModel> gamesList) {
        this.gamesList = gamesList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView gameImage;
        TextView gameName;
        TextView gameReleaseDate;
        TextView gameMetacriticScore;
        TextView gameGenres;

        ViewHolder(View itemView) {
            super(itemView);
            gameImage = itemView.findViewById(R.id.LikedGame_details_gameImage);
            gameName = itemView.findViewById(R.id.LikedGame_details_gameName);
            gameReleaseDate = itemView.findViewById(R.id.LikedGame_details_gameReleaseDate);
            gameMetacriticScore = itemView.findViewById(R.id.LikedGame_details_gameMetacriticScore);
            gameGenres = itemView.findViewById(R.id.LikedGame_details_gameGenres);
        }
    }

    public void setGameDataList(List<GameDataModel> gameDataList) {
        this.gamesList.clear();
        this.gamesList.addAll(gameDataList);
        notifyDataSetChanged();
    }
}
