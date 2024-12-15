package com.ciscx82.finalproject;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

public class AlarmReceiver extends BroadcastReceiver {
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onReceive(Context context, Intent intent) {

        // vibration settings
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(4000);

        // this was the original method, it's just a temporary pop up
        // it works and keeps you on the main page if you want to revert to this
        /*
            Toast.makeText(context, "Alarm! Wake up! Wake up!", Toast.LENGTH_LONG).show();
            Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (alarmUri == null) {
                alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }
            Ringtone ringtone = RingtoneManager.getRingtone(getBaseContext(), alarmUri);

            ringtone.play();
         */

        // for notification window, had to make a new activity to bind the alert dialog window
        // since it won't work unless it's bound to an activity
        // i think this might have caused the problem of the alarm not returning to the home screen
        // but i assume since we're making it a game that part would've had to be changed anyway
        Intent i = new Intent(context, AlertDialogActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
