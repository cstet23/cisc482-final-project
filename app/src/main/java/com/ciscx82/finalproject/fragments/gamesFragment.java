package com.ciscx82.finalproject.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.ciscx82.finalproject.R;

public class gamesFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        var finalView = inflater.inflate(R.layout.fragment_games, container, false);
        RadioGroup incGames = finalView.findViewById(R.id.includedGames);
        incGames.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //do something with that information
            }
        });

        Spinner hintDropdown = finalView.findViewById(R.id.hintsDropdown);
        String[] items = new String[]{"No hints", "30 sec", "1 min", "2 min"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(finalView.getContext(), android.R.layout.simple_spinner_dropdown_item, items);
        hintDropdown.setAdapter(adapter);
        return finalView;
    }


}