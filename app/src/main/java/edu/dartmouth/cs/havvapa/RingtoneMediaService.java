package edu.dartmouth.cs.havvapa;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;

import edu.dartmouth.cs.havvapa.database_elements.EventReminderItemsSource;
import edu.dartmouth.cs.havvapa.models.EventReminderItem;

public class RingtoneMediaService extends Service
{
    private EventReminderItemsSource reminderItemsSource;
    private EventReminderItem reminderItem;
    private StopRingtoneReciever stopRingtoneReciever;
    private long snoozeTime;
    private boolean soundOn;
    private boolean vibrationOn;
    private MediaPlayer mediaPlayer;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        stopRingtoneReciever = new StopRingtoneReciever();
        reminderItemsSource = new EventReminderItemsSource(this);
        try {
            reminderItem = reminderItemsSource.getReminderItem();
            snoozeTime = reminderItem.getSnoozePref();
            soundOn = reminderItem.getSoundPref();
            vibrationOn = reminderItem.getVibrationPref();

        }
        catch (Exception e)
        {
            snoozeTime = 0;
            soundOn = false;
            vibrationOn = true;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {

        Intent myIntent = new Intent(this.getApplicationContext(), SnoozeAlarmActivity.class);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("STOP_ME");
        registerReceiver(stopRingtoneReciever, intentFilter);


        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,myIntent, PendingIntent.FLAG_UPDATE_CURRENT );
        NotificationChannel notificationChannel = new NotificationChannel("CHANNEL_ID",
                "channel name", NotificationManager.IMPORTANCE_HIGH);
        android.support.v4.app.NotificationCompat.Builder notificationBuilder =
                new android.support.v4.app.NotificationCompat.Builder(this, "CHANNEL_ID")
                        .setContentTitle("Alarm")
                        .setContentText("Event started")
                        .setSmallIcon(R.drawable.baseline_add_alert_24)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);
        Notification notification = notificationBuilder.build();
        notification.flags = notification.flags | Notification.FLAG_AUTO_CANCEL;
        NotificationManager notificationManager = (NotificationManager)getSystemService(ScheduleEventActivity.NOTIFICATION_SERVICE);
        if(notificationManager!=null)
        {
            notificationManager.createNotificationChannel(notificationChannel);
            notificationManager.notify(0,notification);
        }

        if(vibrationOn){
            Vibrator vibrator = (Vibrator)getSystemService(ScheduleEventActivity.VIBRATOR_SERVICE);
            vibrator.vibrate(15000);
        }


        if(soundOn){
            RingtoneManager ringtoneManager = new RingtoneManager(this);
            ringtoneManager.setType(RingtoneManager.TYPE_ALARM);
            ringtoneManager.getCursor();

            Uri ringtoneUri = ringtoneManager.getRingtoneUri(RingtoneManager.TYPE_ALL);

            AudioManager audioManager = (AudioManager)getSystemService(ScheduleEventActivity.AUDIO_SERVICE);
            audioManager.setStreamVolume(AudioManager.STREAM_ALARM,7,AudioManager.ADJUST_MUTE);
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(this, ringtoneUri);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mediaPlayer.prepare();
                mediaPlayer.start();
                mediaPlayer.setLooping(false);
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.d("SERVICE-onDestroy()", "Schedule");
        Vibrator vibrator = (Vibrator)getSystemService(ScheduleEventActivity.VIBRATOR_SERVICE);
        NotificationManager notificationManager = (NotificationManager)getSystemService(ScheduleEventActivity.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        vibrator.cancel();
    }

    public class StopRingtoneReciever extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent)
        {
            Log.d("STOP_RINGTONEEE", "Schedule");
            NotificationManager notificationManager = (NotificationManager)getSystemService(ScheduleEventActivity.NOTIFICATION_SERVICE);
            int rqs = intent.getIntExtra("STOP_RINGTONE", 0);
            if(rqs == 7)
            {
                notificationManager.cancelAll();
                Vibrator vibrator = (Vibrator)getSystemService(ScheduleEventActivity.VIBRATOR_SERVICE);
                AudioManager audioManager = (AudioManager)getSystemService(ScheduleEventActivity.AUDIO_SERVICE);
                //NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                //notificationManager.cancelAll();
                vibrator.cancel();
                audioManager.setMicrophoneMute(true);
                if(mediaPlayer!=null){
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer=null;
                }

            }
        }
    }
}
