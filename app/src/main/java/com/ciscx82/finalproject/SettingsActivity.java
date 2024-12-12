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

public class SettingsActivity extends AppCompatActivity {
    private Bundle alarmBundle;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //getting the data passed in from main screen
        Intent intent = getIntent();
        alarmBundle = intent.getBundleExtra("ALARM_BUNDLE");

        ImageButton closeButton = findViewById(R.id.close_settings);
        closeButton.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                //passing the data back
                Intent myIntent = new Intent(SettingsActivity.this, MainActivity.class);
                myIntent.putExtra("ALARM_BUNDLE", alarmBundle);
                SettingsActivity.this.startActivity(myIntent);
                return true;
            }
            return false;
        });

        //setting up the fragment container
        SettingsViewPagerAdapter svpAdapter = new SettingsViewPagerAdapter(this);
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
    public void itemSetChecked(MenuItem item, MenuItem uc1, MenuItem uc2) {
        item.setChecked(true);
        uc1.setChecked(false);
        uc2.setChecked(false);
    }
}
