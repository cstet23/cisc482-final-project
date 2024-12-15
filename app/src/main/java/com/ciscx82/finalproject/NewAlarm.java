package com.ciscx82.finalproject;

import static android.app.PendingIntent.FLAG_IMMUTABLE;
import static android.content.Context.ALARM_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Date;

public class NewAlarm extends Fragment {

    private TimePicker timePicker;
    private Button saveButton, cancelButton;
    private NewAlarmListener listener;
    public AlarmManager alarmManager;

    private CheckBox monday, tuesday, wednesday, thursday, friday, saturday, sunday;

    // Define an interface for communicating with MainActivity
    public interface NewAlarmListener {
        void onNewAlarmCreated(String time, List<String> days);
    }

    public void setNewAlarmListener(NewAlarmListener listener) {
        this.listener = listener;
    }

    public NewAlarm() {
        super(R.layout.fragment_new_alarm);
    }

    // Call this method when a new alarm is created
    private void createNewAlarm(String time, List<String> days) {
        if (listener != null) {
            listener.onNewAlarmCreated(time, days);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_alarm, container, false);

        // Get references to the views
        timePicker = view.findViewById(R.id.timepicker);
        saveButton = view.findViewById(R.id.save_button);
        cancelButton = view.findViewById(R.id.cancel_button);

        // Get references to day CheckBoxes
        monday = view.findViewById(R.id.monday);
        tuesday = view.findViewById(R.id.tuesday);
        wednesday = view.findViewById(R.id.wednesday);
        thursday = view.findViewById(R.id.thursday);
        friday = view.findViewById(R.id.friday);
        saturday = view.findViewById(R.id.saturday);
        sunday = view.findViewById(R.id.sunday);

        // Check if there are arguments passed for modifying an existing alarm
        if (getArguments() != null) {
            String existingTime = getArguments().getString("time");
            ArrayList<String> existingDays = getArguments().getStringArrayList("days");

            if (existingTime != null) {
                String[] timeParts = existingTime.split(":");
                if (timeParts.length == 2) {
                    int hour = Integer.parseInt(timeParts[0]);
                    int minute = Integer.parseInt(timeParts[1]);
                    timePicker.setHour(hour);
                    timePicker.setMinute(minute);
                }
            }

            if (existingDays != null) {
                // Uncheck all first
                monday.setChecked(false);
                tuesday.setChecked(false);
                wednesday.setChecked(false);
                thursday.setChecked(false);
                friday.setChecked(false);
                saturday.setChecked(false);
                sunday.setChecked(false);

                // Check the days that were previously selected
                for (String day : existingDays) {
                    switch (day) {
                        case "Mon":
                            monday.setChecked(true);
                            break;
                        case "Tue":
                            tuesday.setChecked(true);
                            break;
                        case "Wed":
                            wednesday.setChecked(true);
                            break;
                        case "Thu":
                            thursday.setChecked(true);
                            break;
                        case "Fri":
                            friday.setChecked(true);
                            break;
                        case "Sat":
                            saturday.setChecked(true);
                            break;
                        case "Sun":
                            sunday.setChecked(true);
                            break;
                        default:
                            // Handle "Once" or other cases if necessary
                            break;
                    }
                }
            }
        }

        // Set listener for save button
        saveButton.setOnClickListener(v -> {
            // Log the Save button click
            Log.d("NewAlarm", "Save button clicked");

            // Get the selected time from the TimePicker
            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();
            String time = String.format("%02d:%02d", hour, minute);

            // Collect selected days
            List<String> selectedDays = new ArrayList<>();
            if (monday.isChecked()) selectedDays.add("Mon");
            if (tuesday.isChecked()) selectedDays.add("Tue");
            if (wednesday.isChecked()) selectedDays.add("Wed");
            if (thursday.isChecked()) selectedDays.add("Thu");
            if (friday.isChecked()) selectedDays.add("Fri");
            if (saturday.isChecked()) selectedDays.add("Sat");
            if (sunday.isChecked()) selectedDays.add("Sun");

            if (selectedDays.isEmpty()) {
                // If no days are selected, default to "Once" or prompt the user
                selectedDays.add("Once");
            }

            // Notify listener (MainActivity) about the new alarm
            if (listener != null) {
                Log.d("NewAlarm", "Listener notified with time: " + time + " and days: " + selectedDays);
                listener.onNewAlarmCreated(time, selectedDays);
            } else {
                Log.e("NewAlarm", "Listener is null");
            }

            // add alarm to alarm manager
            Date date = new Date();
            Calendar calNow = Calendar.getInstance();
            Calendar calAlarm = Calendar.getInstance();
            alarmManager = (AlarmManager) requireActivity().getSystemService(Context.ALARM_SERVICE);

            // calendar is called to get current time in hour and minute
            calNow.setTime(date);
            calAlarm.setTime(date);

            // set calAlarm based on this most recently set alarm
            calAlarm.set(Calendar.HOUR_OF_DAY, hour);
            calAlarm.set(Calendar.MINUTE, minute);
            calAlarm.set(Calendar.SECOND, 0);

            if (calAlarm.before(calNow)) {
                calAlarm.add(Calendar.DATE, 1);
            }

            // call broadcast receiver using intent
            Intent intent = new Intent(getContext(), AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 24444, intent, FLAG_IMMUTABLE);

            // Alarm rings continuously until toggle button is turned off
            alarmManager.set(AlarmManager.RTC_WAKEUP, calAlarm.getTimeInMillis(), pendingIntent);

            // Navigate back to the previous fragment (MainPage)
            requireActivity().getSupportFragmentManager().popBackStack();
        });


        // set listener for cancel button
        cancelButton.setOnClickListener(v -> {
            // Navigate back to the previous fragment (MainPage)
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return view;
    }
}