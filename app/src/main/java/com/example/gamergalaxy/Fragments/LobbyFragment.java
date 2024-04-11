package com.example.gamergalaxy.Fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.SearchEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gamergalaxy.Adapters.CustomGamesAdapter;
import com.example.gamergalaxy.Adapters.LikedGamesAdapter;
import com.example.gamergalaxy.Models.GameDataModel;
import com.example.gamergalaxy.R;
import com.example.gamergalaxy.Services.GamesAPIService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LobbyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LobbyFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LobbyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LobbyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LobbyFragment newInstance(String param1, String param2) {
        LobbyFragment fragment = new LobbyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private RecyclerView rvGames;
    private RecyclerView rvLikedGames;
    private LinearLayoutManager layoutManager;
    private CustomGamesAdapter adapter;
    private LikedGamesAdapter likedGamesAdapter;
    private ArrayList<GameDataModel> gameDataList;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lobby, container, false);

        Bundle bundle = getArguments();

        if (bundle != null) {
            String userEmail = bundle.getString("email");
            String profileImage = bundle.getString("profileImage");
            FirebaseUser currentUser = bundle.getParcelable("currentUser");

            TextView emailDisplay = view.findViewById(R.id.F_lobby_emailDisplay);
            ImageView imageView = view.findViewById(R.id.F_lobby_profileImage);

            if (currentUser != null) {
                String userID = currentUser.getUid();

                if (userEmail != null) {
                    emailDisplay.setText("Welcome " + userEmail);
                } else {
                    emailDisplay.setText("Welcome Guest");
                }
                loadProfileImage(userID, imageView);
            }else {
                if (userEmail != null) {
                    emailDisplay.setText("Welcome " + userEmail);
                } else {
                    emailDisplay.setText("Welcome Guest");
                }

                Glide.with(requireContext())
                        .load(profileImage)
                        .circleCrop()
                        .into(imageView);
            }

            SearchView searchView = view.findViewById(R.id.F_lobby_seachView);
            searchView.clearFocus();

            int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
            TextView textView = searchView.findViewById(id);
            textView.setTextColor(Color.WHITE);
            textView.setHintTextColor(Color.WHITE);


            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    filterList(newText);
                    return true;
                }
            });

            rvGames = view.findViewById(R.id.F_lobby_resview);
            layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            rvGames.setLayoutManager(layoutManager);
            rvGames.setItemAnimator(new DefaultItemAnimator());

            gameDataList = new ArrayList<>();
            try {
                gameDataList = GamesAPIService.getArrGames(getContext());
            } catch (IOException e) {
                e.printStackTrace();
            }

            adapter = new CustomGamesAdapter(getContext(), gameDataList);
            rvGames.setAdapter(adapter);

            rvLikedGames = view.findViewById(R.id.F_lobby_likedGamesResView);
            RecyclerView.LayoutManager likedGamesLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            rvLikedGames.setLayoutManager(likedGamesLayoutManager);
            rvLikedGames.setItemAnimator(new DefaultItemAnimator());

            likedGamesAdapter = new LikedGamesAdapter(getContext(), new ArrayList<>());
            rvLikedGames.setAdapter(likedGamesAdapter);

            fetchLikedGames();

        }
        return view;
    }

    private void fetchLikedGames() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference likedGamesRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("likedGames");
            likedGamesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Set<String> likedGamesIds = new HashSet<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        likedGamesIds.add(snapshot.getKey());
                    }
                    updateLikedGamesList(likedGamesIds);
                    adapter.setLikedGamesIds(likedGamesIds);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("Firebase", "Error fetching liked games", databaseError.toException());
                }
            });
        }
    }

    private void updateLikedGamesList(Set<String> likedGamesIds) {
        List<GameDataModel> likedGames = new ArrayList<>();
        for (String id : likedGamesIds) {
            for (GameDataModel game : gameDataList) {
                if (game.getGameId().equals(id)) {
                    likedGames.add(game);
                    break;
                }
            }
        }
        likedGamesAdapter.setGameDataList(likedGames);
    }


    private void filterList(String text){
        ArrayList<GameDataModel> filteredList = new ArrayList<>();
        String searchText = text.toLowerCase();
        for (GameDataModel game : gameDataList) {
            if (game.getGameName().toLowerCase().contains(searchText) || game.getGameReleaseDate().toLowerCase().contains(searchText)) {
                filteredList.add(game);
            } else {
                for (String genre : game.getGameGenres()) {
                    if (genre.toLowerCase().contains(searchText)) {
                        filteredList.add(game);
                        break;
                    }
                }
            }
        }

        if(!filteredList.isEmpty()){
            adapter.setFilteredList(filteredList);
        }
        else{
            adapter.setFilteredList(new ArrayList<GameDataModel>());
        }
    }

    private void loadProfileImage(String userId, ImageView imageView) {
        if (!userId.isEmpty()) {
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
            usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String imageUrl = dataSnapshot.child("profileImage").getValue(String.class);
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            Glide.with(requireContext())
                                    .load(imageUrl)
                                    .circleCrop()
                                    .into(imageView);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getContext(), "Error loading profile picture", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}