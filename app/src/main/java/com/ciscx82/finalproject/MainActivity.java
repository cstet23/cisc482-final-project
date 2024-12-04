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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private SettingsViewPagerAdapter svpAdapter;
    private PopupWindow settingsWindow;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
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
}