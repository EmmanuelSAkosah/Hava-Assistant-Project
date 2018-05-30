package edu.dartmouth.cs.havvapa.AlarmHelpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;

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
        int eventUniqueTimeStamp = intent.getIntExtra("EVENT_TIMESTAMP",0);
        Log.d("UniqueID  ", String.valueOf(eventUniqueTimeStamp));

        Intent ringtoneServiceIntent = new Intent(context, RingtoneMediaService.class);
        ringtoneServiceIntent.putExtra("EVENT_TIMESTAMP", eventUniqueTimeStamp);
        context.startService(ringtoneServiceIntent);


    }
}
