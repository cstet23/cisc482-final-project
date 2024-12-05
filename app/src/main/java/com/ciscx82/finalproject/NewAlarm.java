package com.ciscx82.finalproject;

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

public class NewAlarm extends Fragment {

    private TimePicker timePicker;
    private Button saveButton;
    private NewAlarmListener listener;

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

            // Navigate back to the previous fragment (MainPage)
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return view;
    }
}