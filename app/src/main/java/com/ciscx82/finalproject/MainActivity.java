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
    private FloatingActionButton addButton;
    private AlarmAdapter alarmAdapter;
    private List<Alarm> alarmList;
    private Bundle alarmBundle;

    @SuppressLint("ClickableViewAccessibility")
    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        //get passed in content if coming from settings screen
        Intent intent = getIntent();
        Log.d("intent", String.valueOf(intent));
        alarmBundle = intent.getBundleExtra("ALARM_BUNDLE");

        // Initialize views
        addButton = findViewById(R.id.add_alarm_button);
        alarmRecyclerView = findViewById(R.id.alarm_recycler_view);

        // Initialize RecyclerView
        if(alarmBundle != null && !(alarmBundle.isEmpty()))
            alarmList = (ArrayList<Alarm>) alarmBundle.getSerializable("ALARM_LIST");
        else alarmList = new ArrayList<>();
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

        ImageButton settings = findViewById(R.id.settingsButton);

        settings.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                Intent myIntent = new Intent(MainActivity.this, SettingsActivity.class);
                alarmBundle = new Bundle();
                alarmBundle.putSerializable("ALARM_LIST", (Serializable) alarmList);
                myIntent.putExtra("ALARM_BUNDLE", alarmBundle);
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