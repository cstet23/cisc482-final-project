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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NewAlarm.NewAlarmListener{

    private RecyclerView alarmRecyclerView;
    private ImageButton addButton;
    private AlarmAdapter alarmAdapter;
    private List<Alarm> alarmList;

    private SettingsViewPagerAdapter svpAdapter;
    private PopupWindow settingsWindow;

    @SuppressLint("ClickableViewAccessibility")
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

        svpAdapter = new SettingsViewPagerAdapter(this);
        var main = findViewById(R.id.main);

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

                    var alarmButton = settingsMenu.findItem(R.id.alarms_cat);
                    var gamesButton = settingsMenu.findItem(R.id.games_cat);
                    var moreButton = settingsMenu.findItem(R.id.more_cat);

                    viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                        @Override
                        public void onPageSelected(int position) {
                            //super.onPageSelected(position);
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
    }

    public void itemSetChecked(MenuItem item, MenuItem uc1, MenuItem uc2) {
        item.setChecked(true);
        uc1.setChecked(false);
        uc2.setChecked(false);
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