package com.ciscx82.finalproject;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.ciscx82.finalproject.fragments.alarmsFragment;
import com.ciscx82.finalproject.fragments.gamesFragment;
import com.ciscx82.finalproject.fragments.moreFragment;

public class SettingsViewPagerAdapter extends FragmentStateAdapter {
    public SettingsViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position) {
            case 1: return new gamesFragment();
            case 2: return new moreFragment();
            default: return new alarmsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
