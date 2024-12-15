package com.ciscx82.finalproject;


// AlarmAdapter.java

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder> {

    private List<Alarm> alarmList;

    // Constructor
    public AlarmAdapter(List<Alarm> alarmList) {
        this.alarmList = alarmList;
    }

    // Method to update alarms
    public void updateAlarms(List<Alarm> newAlarms) {
        alarmList.clear();
        alarmList.addAll(newAlarms);
        notifyDataSetChanged(); // Notify the adapter that data has changed
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the alarm item layout
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.alarm_item, parent, false);
        return new AlarmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {
        // Bind alarm data to the ViewHolder
        Alarm alarm = alarmList.get(position);
        holder.timeTextView.setText(alarm.getTime());
        holder.daysTextView.setText(alarm.getDays());
    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }

    // ViewHolder class
    public static class AlarmViewHolder extends RecyclerView.ViewHolder {
        TextView timeTextView;
        TextView daysTextView;

        public AlarmViewHolder(@NonNull View itemView) {
            super(itemView);
            timeTextView = itemView.findViewById(R.id.alarm_time);
            daysTextView = itemView.findViewById(R.id.alarm_days);
        }
    }
}
