package com.ciscx82.finalproject;

import android.os.Bundle;
import android.view.View;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NewAlarm.NewAlarmListener{

    private RecyclerView alarmRecyclerView;
    private FloatingActionButton addButton;
    private AlarmAdapter alarmAdapter;
    private List<Alarm> alarmList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Initialize views
        addButton = findViewById(R.id.add_alarm_button);
        alarmRecyclerView = findViewById(R.id.alarm_recycler_view);

        // Initialize RecyclerView
        alarmList = new ArrayList<>();
        alarmAdapter = new AlarmAdapter(alarmList);
        alarmRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        alarmRecyclerView.setAdapter(alarmAdapter);

        // Set listener for add button to open the NewAlarm fragment
        addButton.setOnClickListener(view -> {
            NewAlarm newAlarmFragment = new NewAlarm();
            newAlarmFragment.setNewAlarmListener(this); // Set the listener as MainActivity

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, newAlarmFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });
    }

    // Implementing the onNewAlarmCreated method
    @Override
    public void onNewAlarmCreated(String time, String days) {
        Log.d("MainActivity", "New alarm created: Time - " + time + ", Days - " + days);

        // Create a new Alarm object and add it to the list
        Alarm newAlarm = new Alarm(time, days);
        alarmList.add(newAlarm);

        // Notify the adapter about the new item
        alarmAdapter.notifyItemInserted(alarmList.size() - 1);

        // Scroll to the bottom to show the newly added alarm
        alarmRecyclerView.scrollToPosition(alarmList.size() - 1);
    }
}