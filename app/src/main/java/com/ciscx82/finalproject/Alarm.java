package com.ciscx82.finalproject;


import java.io.Serializable;

public class Alarm implements Serializable {
    private String time;
    private String days; // Declare the days variable

    // Constructor for initializing the time and days
    public Alarm(String time, String days) {
        this.time = time;
        this.days = days; // Initialize the days variable
    }

    // Getter for time
    public String getTime() {
        return time;
    }

    // Getter for days
    public String getDays() {
        return days; // Now properly declared and initialized
    }
}
