package com.ciscx82.finalproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.navigation.NavigationView;


import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NewAlarm.NewAlarmListener{

    private RecyclerView alarmRecyclerView;
    private AlarmAdapter alarmAdapter;
    private List<Alarm> alarmList;
    private Bundle dataBundle;
    private SettingsList settingsList;

    @SuppressLint("ClickableViewAccessibility")
    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        //get passed in content if coming from settings screen
        Intent intent = getIntent();
        dataBundle = intent.getBundleExtra("DATA_BUNDLE");

        // Initialize views
        FloatingActionButton addButton = findViewById(R.id.add_alarm_button);
        alarmRecyclerView = findViewById(R.id.alarm_recycler_view);

        // Initialize RecyclerView
        if(dataBundle != null && !(dataBundle.isEmpty())) {
            alarmList = (ArrayList<Alarm>) dataBundle.getSerializable("ALARM_LIST");
            settingsList = (SettingsList) dataBundle.getSerializable("SETTINGS_LIST");
        }
        else {
            alarmList = new ArrayList<>();
            settingsList = new SettingsList();
        }
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

        FloatingActionButton settings = findViewById(R.id.settingsButton);

        settings.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                Intent myIntent = new Intent(MainActivity.this, SettingsActivity.class);
                dataBundle = new Bundle();
                dataBundle.putSerializable("ALARM_LIST", (Serializable) alarmList);
                dataBundle.putSerializable("SETTINGS_LIST", settingsList);
                myIntent.putExtra("DATA_BUNDLE", dataBundle);
                MainActivity.this.startActivity(myIntent);
                return true;
            }
            return false;
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