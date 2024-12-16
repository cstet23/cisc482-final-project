package com.ciscx82.finalproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.annotation.Nullable;

import java.lang.reflect.Array;

// Sources : http://techblogon.com/alert-dialog-with-edittext-in-android-example-with-source-code/

public class AlertDialogActivity extends Activity {
    // this activity is supposed to be transparent but i think it's still blocking out the main activity page :(
    private Ringtone ringtone;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // setting default ringtone
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        ringtone = RingtoneManager.getRingtone(getBaseContext(), alarmUri);

        // play ringtone
        ringtone.play();
        Vibrator vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(VibrationEffect.createWaveform(
                new long[]{0, 500, 500}, // timings
                1 // start repeating from index 1
        ));

        // building the alert dialog window and displaying it
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setTitle("ALARM")
                .setMessage("Your alarm is ringing! Click Dismiss to go to the game")
                .setCancelable(false)
                .setPositiveButton("Dismiss", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        stopRingtone();
                        vibrator.cancel();
                        dialog.cancel();
                        Intent i = new Intent(AlertDialogActivity.this, GyroGame.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        AlertDialogActivity.this.startActivity(i);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }

    public void stopRingtone() {
        this.ringtone.stop();
    }
}
