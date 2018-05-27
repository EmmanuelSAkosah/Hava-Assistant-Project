package edu.dartmouth.cs.havvapa;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.widget.CompoundButton;
import android.widget.Toast;

import java.util.Calendar;

import edu.dartmouth.cs.havvapa.database_elements.EventReminderItemsSource;
import edu.dartmouth.cs.havvapa.models.EventReminderItem;

public class SnoozeAlarmActivity extends AppCompatActivity
{
    private SwitchCompat alarmOffSwitch;
    private SwitchCompat snoozeSwitch;
    private EventReminderItemsSource reminderItemsSource;
    private EventReminderItem reminderItem;
    private long snoozeTime;
    private AlarmManager alarmManager;
    private Intent myIntent;
    private PendingIntent pendingIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snooze_alarm);

        alarmOffSwitch = findViewById(R.id.alarm_close_switch);
        snoozeSwitch =findViewById(R.id.snooze_switch);
        reminderItemsSource = new EventReminderItemsSource(this);
        try{
            reminderItem = reminderItemsSource.getReminderItem();
            snoozeTime = reminderItem.getSnoozePref();

        }
        catch (Exception e) {
            snoozeTime = 0;
        }

        alarmOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    Intent closeAlarmIntent = new Intent();
                    closeAlarmIntent.setAction("STOP_ME");
                    closeAlarmIntent.putExtra("STOP_RINGTONE", 7);
                    sendBroadcast(closeAlarmIntent);
                    Toast.makeText(getApplicationContext(), "Sleep Well my friend!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        snoozeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked && snoozeTime!=0)
                {
                    Intent closeAlarmIntent = new Intent();
                    closeAlarmIntent.setAction("STOP_ME");
                    closeAlarmIntent.putExtra("STOP_RINGTONE", 7);
                    sendBroadcast(closeAlarmIntent);

                    alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
                    myIntent = new Intent(SnoozeAlarmActivity.this, AlarmReceiver.class);
                    pendingIntent = PendingIntent.getBroadcast(SnoozeAlarmActivity.this, 0,myIntent,PendingIntent.FLAG_UPDATE_CURRENT);

                    Calendar currCal = Calendar.getInstance();
                    long snoozeTimeInMillis = currCal.getTimeInMillis() + snoozeTime;
                    Toast.makeText(getApplicationContext(), "Woow you are gonna try eh!", Toast.LENGTH_SHORT).show();
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, snoozeTimeInMillis, pendingIntent);

                }
                finish();
            }
        });



    }
}
