package edu.dartmouth.cs.havvapa.AlarmHelpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;

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
