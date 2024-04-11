package com.example.gamergalaxy.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.gamergalaxy.Adapters.CustomEntranceAdapter;
import com.example.gamergalaxy.Data.EntranceData;
import com.example.gamergalaxy.Models.EntranceDataModel;
import com.example.gamergalaxy.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EntranceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EntranceFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EntranceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EntranceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EntranceFragment newInstance(String param1, String param2) {
        EntranceFragment fragment = new EntranceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private ArrayList<EntranceDataModel> dataset;
    private CustomEntranceAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entrance, container, false);

        recyclerView = view.findViewById(R.id.F_entrance_resview);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        dataset = new ArrayList<>();

        for (int i = 0; i < EntranceData.drawableArray.length; i++)
        {
            dataset.add(new EntranceDataModel(
                    EntranceData.drawableArray[i],
                    EntranceData.summaryArray[i],
                    EntranceData.idArray[i]
            ));
        }

        adapter = new CustomEntranceAdapter(dataset);
        recyclerView.setAdapter(adapter);

        Button getStartedBtn = view.findViewById(R.id.F_entrance_getStartedBTN);
        getStartedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_entranceFragment_to_registerFragment);
            }
        });

        Button alreadyGGBtn = view.findViewById(R.id.F_entrance_AlreadyGGBTN);
        alreadyGGBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_entranceFragment_to_loginFragment);
            }
        });

        return view;
    }
}