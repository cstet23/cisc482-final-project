package com.ciscx82.finalproject;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;
public class Alarm implements Serializable {
    private String time;
    private List<String> days;

    public Alarm(String time, List<String> days) {
        this.time = time;
        this.days = days;
    }

    public String getTime() {
        return time;
    }

    public String getDays() {
        return String.join(", ", days);// Now properly declared and
    }
        // New method to get the list of days
        public List<String> getDaysList() {
            return days;
        }

    public boolean isAlarmForToday() {
        // Implement logic to check if the alarm is for today
        // For simplicity, let's assume it returns true for now
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        int dayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK);
        String today;
        switch (dayOfWeek) {
            case java.util.Calendar.MONDAY:
                today = "Mon";
                break;
            case java.util.Calendar.TUESDAY:
                today = "Tue";
                break;
            case java.util.Calendar.WEDNESDAY:
                today = "Wed";
                break;
            case java.util.Calendar.THURSDAY:
                today = "Thu";
                break;
            case java.util.Calendar.FRIDAY:
                today = "Fri";
                break;
            case java.util.Calendar.SATURDAY:
                today = "Sat";
                break;
            case java.util.Calendar.SUNDAY:
                today = "Sun";
                break;
            default:
                today = "";
        }
        return days.contains(today) || days.contains("Once");
    }

    // Add the following setter methods
    public void setTime(String time) {
        this.time = time;
    }

    public void setDays(List<String> days) {
        this.days = days;
    }


    /**
     * Calculates the next occurrence of this alarm in milliseconds.
     *
     * @return Timestamp in milliseconds of the next alarm.
     */
    public long getAlarmTimeInMillis() {
        Calendar now = Calendar.getInstance();
        Calendar alarmTime = Calendar.getInstance();

        String[] parts = time.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);

        alarmTime.set(Calendar.HOUR_OF_DAY, hour);
        alarmTime.set(Calendar.MINUTE, minute);
        alarmTime.set(Calendar.SECOND, 0);
        alarmTime.set(Calendar.MILLISECOND, 0);

        // Check if the alarm time has already passed today
        if (alarmTime.before(now)) {
            alarmTime.add(Calendar.DAY_OF_MONTH, 1);
        }

        // If the alarm is set for specific days, find the next occurrence
        if (days.contains("Once")) {
            // "Once" alarms remain as scheduled
            if (alarmTime.before(now)) {
                // If time has passed, remove the alarm or handle accordingly
                // For simplicity, we'll set it for the next day
                alarmTime.add(Calendar.DAY_OF_MONTH, 1);
            }
        } else {
            // Find the next day the alarm is set
            int today = now.get(Calendar.DAY_OF_WEEK);
            int daysUntilNextAlarm = Integer.MAX_VALUE;
            for (String day : days) {
                int dayOfWeek = getDayOfWeekFromString(day);
                int delta = (dayOfWeek - today + 7) % 7;
                if (delta == 0 && alarmTime.before(now)) {
                    delta = 7; // Next week
                }
                if (delta < daysUntilNextAlarm) {
                    daysUntilNextAlarm = delta;
                }
            }
            alarmTime.add(Calendar.DAY_OF_MONTH, daysUntilNextAlarm);
        }

        return alarmTime.getTimeInMillis();
    }

    /**
     * Converts day string to Calendar.DAY_OF_WEEK integer.
     *
     * @param day Day string like "Mon", "Tue", etc.
     * @return Corresponding Calendar.DAY_OF_WEEK integer.
     */
    private int getDayOfWeekFromString(String day) {
        switch (day) {
            case "Mon":
                return Calendar.MONDAY;
            case "Tue":
                return Calendar.TUESDAY;
            case "Wed":
                return Calendar.WEDNESDAY;
            case "Thu":
                return Calendar.THURSDAY;
            case "Fri":
                return Calendar.FRIDAY;
            case "Sat":
                return Calendar.SATURDAY;
            case "Sun":
                return Calendar.SUNDAY;
            default:
                return Calendar.MONDAY; // Default fallback
        }
    }
}


