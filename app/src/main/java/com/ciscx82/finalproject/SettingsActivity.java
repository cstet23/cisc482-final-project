package com.ciscx82.finalproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.navigation.NavigationView;

import java.io.Serializable;
import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {
    private Bundle dataBundle;
    private ArrayList<Alarm> alarmList; //for storage
    private SettingsList settingsList; //to be accessed

    @SuppressWarnings("unchecked")
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //getting the data passed in from main screen
        Intent intent = getIntent();
        dataBundle = intent.getBundleExtra("DATA_BUNDLE");
        assert dataBundle != null;
        alarmList = (ArrayList<Alarm>) dataBundle.getSerializable("ALARM_LIST");
        settingsList = (SettingsList) dataBundle.getSerializable("SETTINGS_LIST");

        ImageButton closeButton = findViewById(R.id.close_settings);
        closeButton.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                //passing the data back
                Intent myIntent = new Intent(SettingsActivity.this, MainActivity.class);
                dataBundle = new Bundle();
                dataBundle.putSerializable("ALARM_LIST", (Serializable) alarmList);
                dataBundle.putSerializable("SETTINGS_LIST", settingsList);
                myIntent.putExtra("DATA_BUNDLE", dataBundle);
                SettingsActivity.this.startActivity(myIntent);
                return true;
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
}
