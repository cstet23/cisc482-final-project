package com.ciscx82.finalproject;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainPage extends Fragment {

    private RecyclerView alarmRecyclerView;
    private ImageButton addButton;
    private AlarmAdapter alarmAdapter;
    private List<Alarm> alarmList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_page, container, false);

        // Initialize views
        addButton = view.findViewById(R.id.add_alarm_button);
        alarmRecyclerView = view.findViewById(R.id.alarm_recycler_view);

        // Initialize RecyclerView
        alarmList = new ArrayList<>();
        alarmAdapter = new AlarmAdapter(alarmList);
        alarmRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        alarmRecyclerView.setAdapter(alarmAdapter);

        // Set listener for the add button to open the SetAlarmFragment
        addButton.setOnClickListener(v -> {
            NewAlarm newAlarmFragment = new NewAlarm();
            newAlarmFragment.setNewAlarmListener((String time, String days) -> {
                // Create a new Alarm object with the provided time and days and add it to the list
                Alarm newAlarm = new Alarm(time, days);
                alarmList.add(newAlarm);
                alarmAdapter.notifyItemInserted(alarmList.size() - 1);
            });

            // Replace the current fragment with NewAlarm fragment
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, newAlarmFragment)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }
}
