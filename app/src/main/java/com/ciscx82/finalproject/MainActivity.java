package com.ciscx82.finalproject;


import android.annotation.SuppressLint;

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

import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import com.google.android.material.navigation.NavigationView;

import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat; // Added import for ContextCompat

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper; // Added import
import androidx.recyclerview.widget.RecyclerView;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import android.graphics.Canvas; // Added import
import androidx.core.view.WindowInsetsCompat;
import android.app.AlertDialog; // Added import
import android.content.DialogInterface; // Added import
import androidx.viewpager2.widget.ViewPager2;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.content.DialogInterface;
import android.app.AlertDialog;
import java.lang.reflect.Type;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.navigation.NavigationView;


import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.navigation.NavigationView;


import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements NewAlarm.NewAlarmListener{

    private RecyclerView alarmRecyclerView;
    private ImageButton addButton;
    private AlarmAdapter alarmAdapter;
    private List<Alarm> alarmList;

    private SettingsViewPagerAdapter svpAdapter;
    private PopupWindow settingsWindow;

    private SharedPreferences sharedPreferences; // If using SharedPreferences
    private Gson gson; // If using Gson for serialization

    private CustomClockView customClockView; // Reference to the CustomClockView

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Assuming EdgeToEdge is a utility class you've implemented
        setContentView(R.layout.activity_main);

        // Initialize views
        addButton = findViewById(R.id.add_alarm_button);
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

        // Initialize SharedPreferences and Gson for persistence
        sharedPreferences = getSharedPreferences("alarms_prefs", MODE_PRIVATE);
        gson = new Gson();

        // Load saved alarms
        loadAlarms();

        // Initialize Settings ViewPager Adapter
        svpAdapter = new SettingsViewPagerAdapter(this);
        ConstraintLayout main = findViewById(R.id.main); // Use explicit type

        ImageButton settings = findViewById(R.id.settingsButton);

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View settingsView = inflater.inflate(R.layout.settings_popup, null);
        int vWidth = LinearLayout.LayoutParams.MATCH_PARENT;
        int vHeight = LinearLayout.LayoutParams.MATCH_PARENT;

        settings.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    settingsWindow = new PopupWindow(settingsView, vWidth, vHeight);
                    settingsWindow.showAtLocation(main, Gravity.CENTER, 0, 0);

                    ImageButton closeButton = settingsView.findViewById(R.id.close_settings);
                    closeButton.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                                settingsWindow.dismiss();
                                return true;
                            }
                            return false;
                        }
                    });

                    ViewPager2 viewPager = settingsView.findViewById(R.id.view_pager);
                    viewPager.setAdapter(svpAdapter);

                    Menu settingsMenu = ((NavigationView) settingsView.findViewById(R.id.settings_options)).getMenu();

                    MenuItem alarmButton = settingsMenu.findItem(R.id.alarms_cat);
                    MenuItem gamesButton = settingsMenu.findItem(R.id.games_cat);
                    MenuItem moreButton = settingsMenu.findItem(R.id.more_cat);

                    viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                        @Override
                        public void onPageSelected(int position) {
                            if(position == 0) itemSetChecked(alarmButton, gamesButton, moreButton);
                            else if (position == 1) itemSetChecked(gamesButton, alarmButton, moreButton);
                            else if (position == 2) itemSetChecked(moreButton, alarmButton, gamesButton);
                        }
                    });

                    alarmButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(@NonNull MenuItem item) {
                            viewPager.setCurrentItem(0);
                            return true;
                        }
                    });

                    gamesButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(@NonNull MenuItem item) {
                            viewPager.setCurrentItem(1);
                            return true;
                        }
                    });

                    moreButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(@NonNull MenuItem item) {
                            viewPager.setCurrentItem(2);
                            return true;
                        }
                    });

                    return true;
                }
                return false;
            }
        });

        // Set up swipe functionality without the library
        setupRecyclerViewSwipe();

        // Update the clock view with the current alarms
        customClockView.setAlarms(alarmList);
    }

    public void itemSetChecked(MenuItem item, MenuItem uc1, MenuItem uc2) {
        item.setChecked(true);
        uc1.setChecked(false);
        uc2.setChecked(false);
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
