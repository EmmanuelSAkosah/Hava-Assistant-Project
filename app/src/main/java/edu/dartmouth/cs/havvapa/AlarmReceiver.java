package edu.dartmouth.cs.havvapa;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

public class AlarmReceiver extends BroadcastReceiver
{
    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";
    public static MediaPlayer mediaPlayer;


    public AlarmReceiver(){

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        /*
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        Ringtone ringtone = RingtoneManager.getRingtone(context,alarmUri);
        ringtone.play();*/

        Intent ringtoneServiceIntent = new Intent(context, RingtoneMediaService.class);
        context.startService(ringtoneServiceIntent);


    }
}
