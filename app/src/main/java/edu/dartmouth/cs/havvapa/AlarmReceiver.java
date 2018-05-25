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

        Intent myIntent = new Intent();
        myIntent.setAction("STOP_ALARM");
        myIntent.putExtra("STOP_ALARM",2);
        context.sendBroadcast(myIntent);

        PendingIntent pendingIntent = PendingIntent.getActivity(context,1,myIntent, PendingIntent.FLAG_UPDATE_CURRENT );
        NotificationChannel notificationChannel = new NotificationChannel("CHANNEL_ID",
                "channel name", NotificationManager.IMPORTANCE_HIGH);
        android.support.v4.app.NotificationCompat.Builder notificationBuilder =
                new android.support.v4.app.NotificationCompat.Builder(context, "CHANNEL_ID")
                        .setContentTitle("Alarm")
                        .setContentText("Event started")
                        .setSmallIcon(R.drawable.baseline_add_alert_24)
                        .setContentIntent(pendingIntent);
        Notification notification = notificationBuilder.build();
        notification.flags = notification.flags | Notification.FLAG_AUTO_CANCEL;
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(notificationManager!=null)
        {
            notificationManager.createNotificationChannel(notificationChannel);
            notificationManager.notify(0,notification);
        }

        Vibrator vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(5000);

        RingtoneManager ringtoneManager = new RingtoneManager(context);
        ringtoneManager.setType(RingtoneManager.TYPE_ALARM);
        ringtoneManager.getCursor();

        Uri ringtoneUri = ringtoneManager.getRingtoneUri(RingtoneManager.TYPE_ALL);

        AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_ALARM,7,AudioManager.ADJUST_MUTE);
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(context, ringtoneUri);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            mediaPlayer.prepare();
            mediaPlayer.start();
            mediaPlayer.setLooping(false);
        }
        catch (IOException e){
            e.printStackTrace();
        }

    }
}
