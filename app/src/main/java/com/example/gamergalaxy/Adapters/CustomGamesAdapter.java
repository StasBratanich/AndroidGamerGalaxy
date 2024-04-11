package com.example.gamergalaxy.Adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gamergalaxy.Models.GameDataModel;
import com.example.gamergalaxy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CustomGamesAdapter extends RecyclerView.Adapter<CustomGamesAdapter.MyViewHolder> {

    private ArrayList<GameDataModel> gamesList;
    private Context context;
    private Set<String> likedGamesIds = new HashSet<>();

    public void setFilteredList(ArrayList<GameDataModel> filteredList)
    {
        this.gamesList = filteredList;
        notifyDataSetChanged();
    }

    public CustomGamesAdapter(android.content.Context context, ArrayList<GameDataModel> gamesList) {
        this.context = context;
        this.gamesList = gamesList;
    }

    public void setLikedGamesIds(Set<String> likedGamesIds) {
        this.likedGamesIds = likedGamesIds;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.game_card_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        GameDataModel game = gamesList.get(position);
        holder.tvGameName.setText(game.getGameName());
        Glide.with(context)
                .load(game.getImage())
                .circleCrop()
                .into(holder.ivGameImage);

        Animation fadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in);
        holder.itemView.startAnimation(fadeIn);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                View detailView = LayoutInflater.from(v.getContext()).inflate(R.layout.card_view_game_details, null, false);

                VideoView detailGameVideo = detailView.findViewById(R.id.game_details_gameVideo);
                TextView detailGameName = detailView.findViewById(R.id.LikedGame_details_gameName);
                TextView detailGameDescription = detailView.findViewById(R.id.LikedGame_details_gameDescription);
                TextView detailGameReleaseDate = detailView.findViewById(R.id.LikedGame_details_gameReleaseDate);
                TextView detailGameGenres = detailView.findViewById(R.id.LikedGame_details_gameGenres);
                TextView detailGameMetacriticScore = detailView.findViewById(R.id.LikedGame_details_gameMetacriticScore);
                CheckBox likeCheckBox = detailView.findViewById(R.id.LikedGame_details_gameCheckBox);

                ArrayList<String> genresList = game.getGameGenres();
                String genresString = TextUtils.join(", ", genresList); // Join genres with a comma and space
                detailGameGenres.setText("Genres: " + genresString);

                detailGameName.setText(game.getGameName());
                detailGameDescription.setText(game.getGameSummary());
                detailGameReleaseDate.setText(game.getGameReleaseDate());
                detailGameMetacriticScore.setText("Metacritic Score: " + game.getGameMetacriticScore());

                String videoUrl = game.getGameMp4Link();
                Uri videoUri = Uri.parse(videoUrl);
                detailGameVideo.setVideoURI(videoUri);
                detailGameVideo.start();

                likeCheckBox.setChecked(likedGamesIds.contains(game.getGameId()));

                likeCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (currentUser != null) {
                        String userId = currentUser.getUid();
                        DatabaseReference likedGamesRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("likedGames");
                        if (isChecked) {
                            likedGamesRef.child(game.getGameId()).setValue(true);
                        } else {
                            likedGamesRef.child(game.getGameId()).removeValue();
                        }
                    }
                });

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setView(detailView);
                AlertDialog dialog = builder.create();
                Window window = dialog.getWindow();
                if (window != null) {
                    window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                }
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return gamesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvGameName;
        ImageView ivGameImage;

        MyViewHolder(View itemView) {
            super(itemView);
            tvGameName = itemView.findViewById(R.id.game_card_name);
            ivGameImage = itemView.findViewById(R.id.game_card_image);
        }
    }
}
