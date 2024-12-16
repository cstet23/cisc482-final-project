package com.ciscx82.finalproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import android.content.SharedPreferences;

import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
// Added import for ContextCompat

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
// Added import

// Added import
// Added import
// Added import
import java.lang.reflect.Type;


import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NewAlarm.NewAlarmListener{

    private RecyclerView alarmRecyclerView;
    private FloatingActionButton addButton;
    private AlarmAdapter alarmAdapter;
    private List<Alarm> alarmList;
    private Bundle dataBundle;
    private SettingsList settingsList;

    private SharedPreferences sharedPreferences; // If using SharedPreferences
    private Gson gson; // If using Gson for serialization

    private CustomClockView customClockView; // Reference to the CustomClockView

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
        ImageButton addButton = findViewById(R.id.add_alarm_button);
        alarmRecyclerView = findViewById(R.id.alarm_recycler_view);
        customClockView = findViewById(R.id.custom_clock_view); // Initialize the clock

        // Optionally, set a click listener on the clock
        customClockView.setOnClickListener(v -> {
            // Example: Open settings or show a toast
            Log.d("MainActivity", "Custom clock clicked!");
            // Implement any interaction here
            new AlertDialog.Builder(this)
                    .setTitle("Alarm Clock")
                    .setMessage("This is a dynamic circular clock displaying current time and next alarm.")
                    .setPositiveButton("OK", null)
                    .show();
        });

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

        // Initialize SharedPreferences and Gson for persistence
        sharedPreferences = getSharedPreferences("alarms_prefs", MODE_PRIVATE);
        gson = new Gson();

        // Load saved alarms
        loadAlarms();

        ImageButton settings = findViewById(R.id.settingsButton);

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

        // Set up swipe functionality without the library
        setupRecyclerViewSwipe();

        // Update the clock view with the current alarms
        customClockView.setAlarms(alarmList);
    }

    // Method to set up swipe functionality manually
    private void setupRecyclerViewSwipe() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                // We are not supporting move in this example
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Alarm selectedAlarm = alarmList.get(position);

                if (direction == ItemTouchHelper.LEFT) {
                    // Handle Delete Action
                    confirmDeleteAlarm(position);
                } else if (direction == ItemTouchHelper.RIGHT) {
                    // Handle Modify Action
                    modifyAlarm(position);
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c,
                                    @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY,
                                    int actionState,
                                    boolean isCurrentlyActive) {
                // Define colors and icons
                ColorDrawable deleteBackground = new ColorDrawable(Color.RED);
                ColorDrawable modifyBackground = new ColorDrawable(Color.GREEN);
                Drawable deleteIcon = ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_delete);
                Drawable modifyIcon = ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_modify);

                int iconMargin = (viewHolder.itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
                int iconTop = viewHolder.itemView.getTop() + (viewHolder.itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
                int iconBottom = iconTop + deleteIcon.getIntrinsicHeight();

                if (dX > 0) { // Swiping to the right
                    int iconLeft = viewHolder.itemView.getLeft() + iconMargin;
                    int iconRight = iconLeft + modifyIcon.getIntrinsicWidth();
                    int iconTopModify = iconTop;
                    int iconBottomModify = iconBottom;
                    modifyIcon.setBounds(iconLeft, iconTopModify, iconRight, iconBottomModify);
                    modifyBackground.setBounds(viewHolder.itemView.getLeft(), viewHolder.itemView.getTop(),
                            viewHolder.itemView.getLeft() + ((int) dX), viewHolder.itemView.getBottom());
                    modifyBackground.draw(c);
                    modifyIcon.draw(c);
                } else if (dX < 0) { // Swiping to the left
                    int iconLeft = viewHolder.itemView.getRight() - iconMargin - deleteIcon.getIntrinsicWidth();
                    int iconRight = viewHolder.itemView.getRight() - iconMargin;
                    int iconTopDelete = iconTop;
                    int iconBottomDelete = iconBottom;
                    deleteIcon.setBounds(iconLeft, iconTopDelete, iconRight, iconBottomDelete);
                    deleteBackground.setBounds(viewHolder.itemView.getRight() + ((int) dX), viewHolder.itemView.getTop(),
                            viewHolder.itemView.getRight(), viewHolder.itemView.getBottom());
                    deleteBackground.draw(c);
                    deleteIcon.draw(c);
                } else { // view is unSwiped
                    deleteBackground.setBounds(0, 0, 0, 0);
                    modifyBackground.setBounds(0, 0, 0, 0);
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(alarmRecyclerView);
    }

    // Method to confirm deletion of an alarm
    private void confirmDeleteAlarm(int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Alarm")
                .setMessage("Are you sure you want to delete this alarm?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Remove the alarm from the list
                        alarmList.remove(position);
                        Log.d("MainActivity", "Alarm removed at position: " + position);
                        Log.d("MainActivity", "Total alarms after removal: " + alarmList.size());

                        // Update the adapter with the new list
                        alarmAdapter.updateAlarms(alarmList);

                        // Update the clock view with the new alarm list
                        customClockView.setAlarms(alarmList);

                        // Save the updated list
                        saveAlarms();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // If "No", notify the adapter to redraw the item
                        alarmAdapter.notifyItemChanged(position);
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    // Method to modify an existing alarm
    private void modifyAlarm(int position) {
        Alarm alarmToModify = alarmList.get(position);

        // Create a new instance of NewAlarm fragment with existing alarm data
        NewAlarm newAlarmFragment = new NewAlarm();
        newAlarmFragment.setNewAlarmListener(new NewAlarm.NewAlarmListener() {
            @Override
            public void onNewAlarmCreated(String time, List<String> days) {
                // Update the alarm in the list
                alarmToModify.setTime(time);
                alarmToModify.setDays(days);
                Log.d("MainActivity", "Alarm modified at position: " + position);
                Log.d("MainActivity", "New time: " + time + ", New days: " + days);

                // Update the adapter with the new list
                alarmAdapter.updateAlarms(alarmList);

                // Update the clock view with the new alarm list
                customClockView.setAlarms(alarmList);

                // Save changes
                saveAlarms();
            }
        });

        // Pass existing alarm data to the fragment
        Bundle args = new Bundle();
        args.putString("time", alarmToModify.getTime());
        args.putStringArrayList("days", new ArrayList<>(alarmToModify.getDaysList()));
        newAlarmFragment.setArguments(args);

        // Replace the current fragment with NewAlarm fragment for modification
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, newAlarmFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        // Notify the adapter to redraw the item
        alarmAdapter.notifyItemChanged(position);
    }

    // Implementing the onNewAlarmCreated method
    @Override
    public void onNewAlarmCreated(String time, List<String> days) {
        Log.d("MainActivity", "New alarm created: Time - " + time + ", Days - " + days);

        // Create a new Alarm object and add it to the list
        Alarm newAlarm = new Alarm(time, days);
        alarmList.add(newAlarm);
        Log.d("MainActivity", "Alarm added. Total alarms: " + alarmList.size());

        // Update the adapter with the new list
        alarmAdapter.updateAlarms(alarmList);

        // Update the clock view with the new alarm list
        customClockView.setAlarms(alarmList);

        // Scroll to the bottom to show the newly added alarm
        alarmRecyclerView.scrollToPosition(alarmAdapter.getItemCount() - 1);

        // Save alarms after adding a new one
        saveAlarms();
    }

    // Method to save alarms using SharedPreferences
    private void saveAlarms() {
        String json = gson.toJson(alarmList);
        sharedPreferences.edit().putString("alarms", json).apply();
        Log.d("MainActivity", "Alarms saved to SharedPreferences.");
    }

    // Method to load alarms from SharedPreferences
    private void loadAlarms() {
        String json = sharedPreferences.getString("alarms", null);
        if (json != null) {
            Type type = new TypeToken<List<Alarm>>() {}.getType();
            alarmList = gson.fromJson(json, type);
            Log.d("MainActivity", "Alarms loaded from SharedPreferences. Total alarms: " + alarmList.size());
            alarmAdapter.updateAlarms(alarmList);
            // Update the clock view with the loaded alarms
            customClockView.setAlarms(alarmList);
        } else {
            Log.d("MainActivity", "No alarms found in SharedPreferences.");
        }
    }

    private List<Alarm> filterAlarmsForToday(List<Alarm> alarms) {
        List<Alarm> filteredAlarms = new ArrayList<>();
        for (Alarm alarm : alarms) {
            if (alarm.isAlarmForToday()) {
                filteredAlarms.add(alarm);
            }
        }
        return filteredAlarms;
    }
}