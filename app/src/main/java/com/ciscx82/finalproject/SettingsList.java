package com.ciscx82.finalproject;

import java.io.Serializable;
import java.util.ArrayList;

public class SettingsList implements Serializable {
    private String tone, gamesSel;
    private int volume, hintTime;
    private boolean skip;
    //structure: tone, volume, gamesSel, hintTime, skip
    public SettingsList(String tone, int volume, String gamesSelected, int hintTime, boolean skip) {
        this.tone = tone;
        this.volume = volume;
        this.gamesSel = gamesSelected;
        this.hintTime = hintTime;
        this.skip = skip;
    }

    public SettingsList() {
        this.tone = "Default";
        this.volume = 100;
        this.gamesSel = "Default";
        this.hintTime = 0;
        this.skip = false;
    }

    public String getTone() {
        return tone;
    }

    public void setTone(String tone) {
        this.tone = tone;
    }

    public String getGamesSel() {
        return gamesSel;
    }

    public void setGamesSel(String gamesSel) {
        this.gamesSel = gamesSel;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public int getHintTime() {
        return hintTime;
    }

    public void setHintTime(int hintTime) {
        this.hintTime = hintTime;
    }

    public boolean isSkip() {
        return skip;
    }

    public void setSkip(boolean skip) {
        this.skip = skip;
    }
}
