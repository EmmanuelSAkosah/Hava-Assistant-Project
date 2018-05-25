package edu.dartmouth.cs.havvapa;

import android.app.AlarmManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

public class AlarmManagmentActivity extends AppCompatActivity
{
    private SwitchCompat reminderSoundOptionSwitch;
    private SwitchCompat reminderVibrationSwitch;
    private TextView reminderSnoozeTimeIndicatorTv;
    private TextView reminderSnoozeTimeTv;
    private TextView alarmCloseTv;
    private KeepUserRecords userRecords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_managment);

        reminderSoundOptionSwitch = findViewById(R.id.reminder_sound_option);
        reminderVibrationSwitch = findViewById(R.id.reminder_vibration_option);
        reminderSnoozeTimeIndicatorTv = findViewById(R.id.reminder_snooze_time_indicator);
        reminderSnoozeTimeTv = findViewById(R.id.reminder_snooze_time);
        alarmCloseTv = findViewById(R.id.alarm_close_tv);
        userRecords = new KeepUserRecords(getApplicationContext());

        reminderVibrationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(isChecked)
                {
                    userRecords.setVibrationPref(true);
                }
                else {
                    userRecords.setVibrationPref(false);
                }
            }
        });

        reminderSoundOptionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    userRecords.setSoundPref(true);
                }
                else {
                    userRecords.setSoundPref(false);
                }
            }
        });

        alarmCloseTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent closeAlarmIntent = new Intent(AlarmManagmentActivity.this, ScheduleEventActivity.class);
                closeAlarmIntent.putExtra("close_alarm",true);
                startActivity(closeAlarmIntent);
                finish();
            }
        });


    }
}
