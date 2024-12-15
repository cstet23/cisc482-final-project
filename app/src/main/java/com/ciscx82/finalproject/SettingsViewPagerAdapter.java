package com.ciscx82.finalproject;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.ciscx82.finalproject.fragments.alarmsFragment;
import com.ciscx82.finalproject.fragments.gamesFragment;
import com.ciscx82.finalproject.fragments.moreFragment;

public class SettingsViewPagerAdapter extends FragmentStateAdapter {
    private String tone, gamesSel;
    private int volume, hintTime;
    private boolean skip;
    public SettingsViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, SettingsList settingsList) {
        super(fragmentActivity);
        //structure: tone, volume, gamesSel, hintTime, skip

        this.tone = settingsList.getTone();
        this.volume = settingsList.getVolume();
        this.gamesSel = settingsList.getGamesSel();
        this.hintTime = settingsList.getHintTime();
        this.skip = settingsList.isSkip();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position) {
            case 1: return new gamesFragment(gamesSel, hintTime, skip);
            case 2: return new moreFragment();
            default: return new alarmsFragment(tone, volume);
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
