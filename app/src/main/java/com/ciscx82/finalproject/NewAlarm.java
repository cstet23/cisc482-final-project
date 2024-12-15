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
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Date;

public class NewAlarm extends Fragment {

    private TimePicker timePicker;
    private Button saveButton, cancelButton;
    private NewAlarmListener listener;
    public AlarmManager alarmManager;

    // Define an interface for communicating with MainActivity
    public interface NewAlarmListener {
        void onNewAlarmCreated(String time, String days);  // Updated to take both time and days as Strings
    }

    public void setNewAlarmListener(NewAlarmListener listener) {
        this.listener = listener;
    }

    public NewAlarm() {
        super(R.layout.fragment_new_alarm);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_alarm, container, false);

        // Get references to the views
        timePicker = view.findViewById(R.id.timepicker);
        saveButton = view.findViewById(R.id.save_button);
        cancelButton = view.findViewById(R.id.cancel_button);

        // Set listener for save button
        saveButton.setOnClickListener(v -> {
            // Log the Save button click
            Log.d("NewAlarm", "Save button clicked");

            // Get the selected time from the TimePicker
            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();
            String time = String.format("%02d:%02d", hour, minute);

            // Placeholder for days - Replace this with actual selection from user
            String days = "Mon, Wed, Fri";

            // Notify listener (MainActivity) about the new alarm
            if (listener != null) {
                Log.d("NewAlarm", "Listener notified with time: " + time + " and days: " + days);
                listener.onNewAlarmCreated(time, days);
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