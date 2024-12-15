package com.ciscx82.finalproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;

// Sources : http://techblogon.com/alert-dialog-with-edittext-in-android-example-with-source-code/

public class AlertDialogActivity extends Activity {
    // this activity is supposed to be transparent but i think it's still blocking out the main activity page :(
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // setting default ringtone
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        Ringtone ringtone = RingtoneManager.getRingtone(getBaseContext(), alarmUri);

        // play ringtone
        ringtone.play();

        // building the alert dialog window and displaying it
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setTitle("ALARM")
                .setMessage("ALARM")
                .setCancelable(false)
                .setPositiveButton("Dismiss", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.cancel();
                        ringtone.stop();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }
}
