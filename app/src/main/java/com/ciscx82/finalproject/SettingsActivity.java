package com.ciscx82.finalproject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {
    private SettingsList settingsList; //to be accessed
    private boolean settingsUpdated; //for save purposes
    private CoordinatorLayout layout; //for snackbar
    private SharedPreferences sharedPreferences; //to save between sessions
    private Gson gson; //for serializing, used with SharedPreferences

    @SuppressWarnings("unchecked")
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        layout = findViewById(R.id.layout);

        //set settingsUpdated to false, nothing has been changed yet
        settingsUpdated = false;

        //Setting up SharedPreferences
        sharedPreferences = getSharedPreferences("saved_settings", MODE_PRIVATE);
        gson = new Gson();

        loadSettings();

        Button saveButton = findViewById(R.id.save_settings);
        saveButton.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                saveSettings();
                Snackbar snackbar = Snackbar.make(layout, "Settings saved", Snackbar.LENGTH_SHORT);
                snackbar.show();
                return true;
            }
            return false;
        });

        ImageButton closeButton = findViewById(R.id.close_settings);
        closeButton.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                //check to see if any settings have changed without saving, if so display warning
                if (!settingsUpdated) {
                    //passing the data back
                    backToMain();
                    return true;
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder
                            .setTitle("Save before continuing")
                            .setMessage("The changes made to your settings are unsaved. Would you like to save before continuing?")
                            .setPositiveButton("Save", new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int id)
                                {
                                    dialog.cancel();
                                    saveSettings();
                                    backToMain();
                                }
                            })
                            .setNegativeButton("Proceed Anyway", new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int id)
                                {
                                    dialog.cancel();
                                    backToMain();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
            return false;
        });

        //setting up the fragment container
        SettingsViewPagerAdapter svpAdapter = new SettingsViewPagerAdapter(this, settingsList);
        ViewPager2 viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(svpAdapter);

        Menu settingsMenu = ((NavigationView) findViewById(R.id.settings_options)).getMenu();

        var alarmButton = settingsMenu.findItem(R.id.alarms_cat);
        var gamesButton = settingsMenu.findItem(R.id.games_cat);
        var moreButton = settingsMenu.findItem(R.id.more_cat);

        //changing fragments when menu options are selected
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if(position == 0) itemSetChecked(alarmButton, gamesButton, moreButton);
                else if (position == 1) itemSetChecked(gamesButton, alarmButton, moreButton);
                else if (position == 2) itemSetChecked(moreButton, alarmButton, gamesButton);
            }
        });

        alarmButton.setOnMenuItemClickListener(item -> {
            viewPager.setCurrentItem(0);
            return true;
        });

        gamesButton.setOnMenuItemClickListener(item -> {
            viewPager.setCurrentItem(1);
            return true;
        });

        moreButton.setOnMenuItemClickListener(item -> {
            viewPager.setCurrentItem(2);
            return true;
        });

    }
    public void itemSetChecked(MenuItem checked, MenuItem uc1, MenuItem uc2) {
        checked.setChecked(true);
        uc1.setChecked(false);
        uc2.setChecked(false);
    }

    public void backToMain() {
        Intent myIntent = new Intent(SettingsActivity.this, MainActivity.class);
        SettingsActivity.this.startActivity(myIntent);
    }

    public void setTone(String tone) {
        settingsList.setTone(tone);
    }
    public void setVolume(int vol) {
        settingsList.setVolume(vol);
    }
    public void setGamesSel(String gamesSel) {
        settingsList.setGamesSel(gamesSel);
    }
    public void setHintTime(int hintTime) {
        settingsList.setHintTime(hintTime);
    }
    public void setSkip(boolean skip) {
        settingsList.setSkip(skip);
    }
    public void setSettingsUpdated(boolean updated) {
        settingsUpdated = updated;
    }
    public void saveSettings() {
        String json = gson.toJson(settingsList);
        sharedPreferences.edit().putString("settings", json).apply();
        Log.d("SettingsActivity", "Settings saved to SharedPreferences.");
        setSettingsUpdated(false);
    }
    // Method to load settings from SharedPreferences
    private void loadSettings() {
        String json = sharedPreferences.getString("settings", null);
        if (json != null) {
            Type type = new TypeToken<SettingsList>() {}.getType();
            settingsList = gson.fromJson(json, type);
        } else {
            Log.d("SettingsActivity", "No settings found in SharedPreferences.");
            settingsList = new SettingsList();
        }
    }
}
