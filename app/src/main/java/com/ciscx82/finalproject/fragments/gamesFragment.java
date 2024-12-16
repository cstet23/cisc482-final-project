package com.ciscx82.finalproject.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.ciscx82.finalproject.R;
import com.ciscx82.finalproject.SettingsActivity;

public class gamesFragment extends Fragment {
    private String gamesSel;
    private int hintTime;
    private boolean skip;

    public gamesFragment(String gamesSel, int hintTime, boolean skip) {
        this.gamesSel = gamesSel;
        this.hintTime = hintTime;
        this.skip = skip;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View finalView = inflater.inflate(R.layout.fragment_games, container, false);
        SettingsActivity currAct = (SettingsActivity) getActivity();

        // set the defaults from the data bundle
        RadioGroup incGames = finalView.findViewById(R.id.includedGames);
        switch(gamesSel) {
            case "Puzzle": incGames.check(R.id.radioButtonPuzzleGames); break;
            case "Gyro Only": incGames.check(R.id.radioButtonGyroGames); break;
            case "No Gyro": incGames.check(R.id.radioButtonNoGyroGames); break;
            default: incGames.check(R.id.radioButtonDefaultGames); break;
        }
        incGames.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //do something with that information
                if(checkedId == R.id.radioButtonPuzzleGames && currAct != null) currAct.setGamesSel("Puzzle");
                else if(checkedId == R.id.radioButtonGyroGames && currAct != null) currAct.setGamesSel("Gyro Only");
                else if(checkedId == R.id.radioButtonNoGyroGames && currAct != null) currAct.setGamesSel("No Gyro");
                else if(currAct != null) currAct.setGamesSel("Default");
                else Log.d("currAct is null", "in gamesSel");
            }
        });

        Spinner hintDropdown = finalView.findViewById(R.id.hintsDropdown);
        String[] items = new String[]{"No hints", "30 sec", "1 min", "2 min"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(finalView.getContext(), android.R.layout.simple_spinner_dropdown_item, items);
        hintDropdown.setAdapter(adapter);
        hintDropdown.setSelection((int) (double) (hintTime / 30));
        hintDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (currAct != null) currAct.setHintTime(position * 30);
                else Log.d("currAct is null", "in hints");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        CheckBox checkBox = finalView.findViewById(R.id.skipBox);
        checkBox.setChecked(skip);
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (currAct != null) currAct.setSkip(isChecked);
            else Log.d("currAct is null", "in skip");
        });
        return finalView;
    }


}