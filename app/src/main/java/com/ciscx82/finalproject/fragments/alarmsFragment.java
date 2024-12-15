package com.ciscx82.finalproject.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import com.ciscx82.finalproject.R;
import com.ciscx82.finalproject.SettingsActivity;
import com.google.android.material.slider.Slider;

import java.util.Objects;

public class alarmsFragment extends Fragment {
    private String tone;
    private int volume;
    public alarmsFragment(String tone, int volume) {
        this.tone = tone;
        this.volume = volume;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View finalView = inflater.inflate(R.layout.fragment_alarms, container, false);
        SettingsActivity currAct = (SettingsActivity) getActivity();

        // set the defaults from the data bundle
        SeekBar volSlider = finalView.findViewById(R.id.volumeSlider);
        volSlider.setProgress(volume);
        volSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // change the volume value to be passed back to main activity
                if (currAct != null) currAct.setVolume(seekBar.getProgress());
                else Log.d("currAct is null", "in volume");
            }
        });

        RadioGroup toneButtons = finalView.findViewById(R.id.alarmTones);
        if(Objects.equals(tone, "Custom")) toneButtons.check(R.id.radioButtonCustomTone);
        else toneButtons.check(R.id.radioButtonDefaultTone);
        toneButtons.setOnCheckedChangeListener((group, checkedId) -> {
            //do something with that information
            if(checkedId == R.id.radioButtonCustomTone && currAct != null) currAct.setTone("Custom");
            else if(currAct != null) currAct.setTone("Default");
            else Log.d("currAct is null", "in tone");
        });

        return finalView;
    }
}