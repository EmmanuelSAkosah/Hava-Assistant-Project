package edu.dartmouth.cs.havvapa;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import java.util.Calendar;

import edu.dartmouth.cs.havvapa.AlarmHelpers.AlarmManagmentActivity;
import edu.dartmouth.cs.havvapa.AlarmHelpers.AlarmReceiver;
import edu.dartmouth.cs.havvapa.database_elements.ToDoItemsSource;
import edu.dartmouth.cs.havvapa.models.ToDoEntry;

public class ScheduleEventActivity extends AppCompatActivity
{

    private String eventTitle;
    private String eventDescription;
    private String eventLocation;
    private Calendar startDateTime;
    private Calendar endDateTime;

    private  EditText eventTitleEt;
    private EditText eventLocationEt;
    private EditText eventDescriptionEt;
    private TextView eventDateStartTv;
    private TextView eventDateEndTv;
    private TextView eventTimeStartTv;
    private TextView eventTimeEndTv;
    private TextView eventReminder1Tv;
    private TextView eventReminder2Tv;
    private RadioButton eventReminder1RadBtn;

    private String mSelectedTime;
    private String mSelectedDate;
    private String mSelectedStartDate;
    private String mSelectedEndDate;
    private String mSelectedStartTime;
    private String mSelectedEndTime;
    private String mReminderOption1;
    private long eventReminderInterval;
    private long snoozeTime;

    private ToDoItemsSource database;
    //private EventReminderItemsSource reminderItemsDatabase;
    //private EventReminderItem eventReminderItem;
    private ToDoEntry eventToDisplay;
    private boolean modifyEvent;
    private Intent myIntent;

    boolean flag = true;

    private StopAlarmServiceReciever stopAlarmServiceReciever;
    private AddEventTask addEventTask = null;
    private DeleteEventTask deleteEventTask = null;

    public AlarmManager alarmManager;
    private PendingIntent pendingIntent;


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.schedule_event_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.schedule_event_btn:

                eventTitle = eventTitleEt.getText().toString();
                eventLocation = eventLocationEt.getText().toString();
                eventDescription = eventDescriptionEt.getText().toString();
                Log.d("TITLE", eventTitle);
                Log.d("DESCRIPTION", eventDescription);

                if(modifyEvent)
                {
                    String eventDuration = eventToDisplay.calculateEventDuration(startDateTime, endDateTime);
                    database.modifyScheduledEvent(eventToDisplay.getId(), eventTitle,eventLocation, eventDescription, startDateTime.getTimeInMillis(), endDateTime.getTimeInMillis(), eventDuration);
                    Intent modifyEventIntent = new Intent(ScheduleEventActivity.this, MainActivity.class);
                    startActivity(modifyEventIntent);
                    finish();

                }
                else {
                    addEventTask = new AddEventTask();
                    addEventTask.execute();

                    Intent scheduledEventIntent = new Intent(this, MainActivity.class);
                    startActivity(scheduledEventIntent);
                    finish();
                }


                return true;

            case R.id.delete_event_btn:

                deleteEventTask = new DeleteEventTask();
                deleteEventTask.execute();
                Intent deleteEventIntent = new Intent(this, MainActivity.class);
                startActivity(deleteEventIntent);
                finish();
                return true;


            case R.id.menuitem_settings:
                startActivity(new Intent(ScheduleEventActivity.this, AlarmManagmentActivity.class));
                return true;

            case R.id.menuitem_editProfile:
                startActivity(new Intent(this, SignUpActivity.class));
                return true;

            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("EVENT_TITLE", eventTitle);
        outState.putString("EVENT_LOCATION", eventLocation);
        outState.putString("EVENT_DESCRIPTION", eventDescription);
        outState.putSerializable("CALENDAR_KEY_START", startDateTime);
        outState.putSerializable("CALENDAR_KEY_END", endDateTime);
        outState.putString("EVENT_TIME_START", mSelectedStartTime);
        outState.putString("EVENT_TIME_END", mSelectedEndTime);
        outState.putString("EVENT_DATE_START", mSelectedStartDate);
        outState.putString("EVENT_DATE_END", mSelectedEndDate);
        outState.putString("DATE_KEY", mSelectedDate);
        outState.putString("TIME_KEY", mSelectedTime);
        outState.putBoolean("MODIFY_EVENT", modifyEvent);
        outState.putString("REMINDER_1", mReminderOption1);
        outState.putLong("EVENT_REMINDER_INTERVAL", eventReminderInterval);
        outState.putBoolean("BOOLEAN_FLAG", false);
        outState.putLong("SNOOZE_TIME", snoozeTime);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d("OnCreate of Schedule", "Schedule");
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_schedule_event);
        setContentView(R.layout.activity_schedule_event_vol2);

        getSupportActionBar().setTitle("Schedule your event");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        eventTitleEt = findViewById(R.id.event_title);
        eventLocationEt = findViewById(R.id.event_location);
        eventDescriptionEt = findViewById(R.id.event_description);
        eventDateStartTv = findViewById(R.id.event_start_date);
        eventDateEndTv = findViewById(R.id.event_end_date);
        eventTimeStartTv = findViewById(R.id.event_start_time);
        eventTimeEndTv = findViewById(R.id.event_end_time);
        eventReminder1Tv = findViewById(R.id.event_reminder_1);
        eventReminder2Tv = findViewById(R.id.event_reminder_2);

        database = new ToDoItemsSource(this);
        //reminderItemsDatabase = new EventReminderItemsSource(this);

        if(savedInstanceState!=null){
            eventTitle = savedInstanceState.getString("EVENT_TITLE");
            eventDescription = savedInstanceState.getString("EVENT_DESCRIPTION");
            eventLocation = savedInstanceState.getString("EVENT_LOCATION");
            mSelectedStartTime = savedInstanceState.getString("EVENT_TIME_START");
            mSelectedEndTime = savedInstanceState.getString("EVENT_TIME_END");
            mSelectedStartDate = savedInstanceState.getString("EVENT_DATE_START");
            mSelectedEndDate = savedInstanceState.getString("EVENT_DATE_END");
            startDateTime = (Calendar)savedInstanceState.getSerializable("CALENDAR_KEY_START");
            endDateTime = (Calendar)savedInstanceState.getSerializable("CALENDAR_KEY_END");
            mSelectedDate = savedInstanceState.getString("DATE_KEY");
            mSelectedTime = savedInstanceState.getString("TIME_KEY");
            modifyEvent = savedInstanceState.getBoolean("MODIFY_EVENT");
            mReminderOption1 = savedInstanceState.getString("REMINDER_1");
            eventReminderInterval = savedInstanceState.getLong("EVENT_REMINDER_INTERVAL");
            flag = savedInstanceState.getBoolean("BOOLEAN_FLAG");
            //snoozeTime = savedInstanceState.getLong("SNOOZE_TIME");

            eventTimeStartTv.setText(mSelectedStartTime);
            eventTimeEndTv.setText(mSelectedEndTime);
            eventDateStartTv.setText(mSelectedStartDate);
            eventDateEndTv.setText(mSelectedEndDate);
            eventReminder1Tv.setText(mReminderOption1);
        }

        if(flag)
        {
            try
            {
                long selectedEventId = getIntent().getExtras().getLong("ENTRY_ROW_ID");
                eventToDisplay = database.fetchEntryByIndex(selectedEventId);
                //eventReminderItem = reminderItemsDatabase.getReminderItem();
                //snoozeTime = eventReminderItem.getSnoozePref();
                startDateTime = eventToDisplay.getStartDateTime();
                endDateTime = eventToDisplay.getEndDateTime();
                updateStartDateTime();
                updateEndDateTime();

                eventTimeStartTv.setText(mSelectedStartTime);
                eventTimeEndTv.setText(mSelectedEndTime);
                eventDateStartTv.setText(mSelectedStartDate);
                eventDateEndTv.setText(mSelectedEndDate);

                eventTitle = eventToDisplay.getEventTitle();
                eventLocation = eventToDisplay.getEventLocation();
                eventDescription = eventToDisplay.getEventDescription();
                Log.d("TITLE1", eventTitle);
                Log.d("DESCRIPTION1", eventDescription);

                eventTitleEt.setText(eventTitle);
                eventDescriptionEt.setText(eventDescription);
                eventLocationEt.setText(eventLocation);


                modifyEvent = true;


            }
            catch (Exception e)
            {
                modifyEvent = false;
                snoozeTime = 0;
                startDateTime = Calendar.getInstance();
                endDateTime = Calendar.getInstance();

            }
        }


        eventDateStartTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                new DatePickerDialog(ScheduleEventActivity.this, mDateListenerStart, startDateTime.get(Calendar.YEAR),
                        startDateTime.get(Calendar.MONTH), startDateTime.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        eventDateEndTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ScheduleEventActivity.this, mDateListenerEnd, endDateTime.get(Calendar.YEAR),
                        endDateTime.get(Calendar.MONTH), endDateTime.get(Calendar.DAY_OF_MONTH)).show();

            }
        });

        eventTimeStartTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(ScheduleEventActivity.this, mTimeListenerStart, startDateTime.get(Calendar.HOUR_OF_DAY), startDateTime.get(Calendar.MINUTE), true).show();
            }
        });

        eventTimeEndTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(ScheduleEventActivity.this, mTimeListenerEnd, endDateTime.get(Calendar.HOUR_OF_DAY), endDateTime.get(Calendar.MINUTE), true).show();
            }
        });



        eventReminder1Tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                //RadioGroup rg = findViewById(R.id.event_reminder_options_radio_group);
                final AlertDialog.Builder builder = new AlertDialog.Builder(ScheduleEventActivity.this);

                LayoutInflater inflater = getLayoutInflater();
                //RadioGroup rgf = new RadioGroup(ScheduleEventActivity.this);
                //rgf.addView(inflater.inflate(R.layout.activity_event_reminder_options, (ViewGroup)findViewById(R.id.event_reminder_options_radio_group)));
                final View view = (View)inflater.inflate(R.layout.activity_event_reminder_options,null);
                //RadioGroup grp = (RadioGroup)view.findViewById(R.id.event_reminder_options_radio_group) ;
                final RadioGroup rgg = view.findViewById(R.id.event_reminder_options_radio_group);
                rgg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId)
                    {
                        Log.d("ITEM_CHECKED_radio", String.valueOf(checkedId));
                        eventReminder1RadBtn =(RadioButton)view.findViewById(checkedId);


                    }
                });

                builder.setView(view);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Log.d("ITEM_CHECKED_builder", String.valueOf(which));
                        Log.d("IN ALARM", "ALARM");
                        int checkedId = rgg.getCheckedRadioButtonId();

                        if(checkedId!=-1)
                        {

                            switch (eventReminder1RadBtn.getId())
                            {
                                case R.id.event_reminder_none_radio:
                                    if(eventReminder1RadBtn.isChecked())
                                    {
                                        mReminderOption1 = eventReminder1RadBtn.getText().toString();
                                        eventReminder1Tv.setText(mReminderOption1);
                                        if (alarmManager != null){
                                            alarmManager.cancel(pendingIntent);
                                        }

                                    }
                                    break;

                                case R.id.event_reminder_start_radio:
                                    if(eventReminder1RadBtn.isChecked())
                                    {
                                        mReminderOption1 = eventReminder1RadBtn.getText().toString();
                                        eventReminder1Tv.setText(mReminderOption1);
                                        eventReminderInterval=0;
                                        Log.d("WHICH REMINDER OPTION?", String.valueOf(eventReminderInterval));
                                    }
                                    break;

                                case R.id.event_reminder_1_minute_before_radio:
                                    if(eventReminder1RadBtn.isChecked())
                                    {
                                        mReminderOption1 = eventReminder1RadBtn.getText().toString();
                                        eventReminder1Tv.setText(mReminderOption1);
                                        eventReminderInterval = 60*1000;
                                        Log.d("WHICH REMINDER OPTION?", String.valueOf(eventReminderInterval));
                                    }
                                    break;
                                case R.id.event_reminder_5_minutes_before_radio:
                                    if(eventReminder1RadBtn.isChecked())
                                    {
                                        mReminderOption1 = eventReminder1RadBtn.getText().toString();
                                        eventReminder1Tv.setText(mReminderOption1);
                                        eventReminderInterval = 5 * 60 *1000;
                                        Log.d("WHICH REMINDER OPTION?", String.valueOf(eventReminderInterval));
                                    }
                                    break;
                                case R.id.event_reminder_10_minutes_before_radio:
                                    if(eventReminder1RadBtn.isChecked())
                                    {
                                        mReminderOption1 = eventReminder1RadBtn.getText().toString();
                                        eventReminder1Tv.setText(mReminderOption1);
                                        eventReminderInterval = 10*60*1000;
                                        Log.d("WHICH REMINDER OPTION?", String.valueOf(eventReminderInterval));
                                    }
                                    break;
                                case R.id.event_reminder_20_minutes_before_radio:
                                    if(eventReminder1RadBtn.isChecked())
                                    {
                                        mReminderOption1 = eventReminder1RadBtn.getText().toString();
                                        eventReminder1Tv.setText(mReminderOption1);
                                        eventReminderInterval = 20*60*1000;
                                        Log.d("WHICH REMINDER OPTION?", String.valueOf(eventReminderInterval));

                                    }
                                    break;
                                case R.id.event_reminder_30_minutes_before_radio:
                                    if(eventReminder1RadBtn.isChecked())
                                    {
                                        mReminderOption1 = eventReminder1RadBtn.getText().toString();
                                        eventReminder1Tv.setText(mReminderOption1);
                                        eventReminderInterval = 30*60*1000;
                                        Log.d("WHICH REMINDER OPTION?", String.valueOf(eventReminderInterval));
                                    }
                                    break;
                                case R.id.event_reminder_45_minutes_before_radio:
                                    if(eventReminder1RadBtn.isChecked())
                                    {
                                        mReminderOption1 = eventReminder1RadBtn.getText().toString();
                                        eventReminder1Tv.setText(mReminderOption1);
                                        eventReminderInterval = 45*60*1000;
                                        Log.d("WHICH REMINDER OPTION?", String.valueOf(eventReminderInterval));
                                    }
                                    break;

                                case R.id.event_reminder_1_hour_before_radio:
                                    if(eventReminder1RadBtn.isChecked())
                                    {
                                        mReminderOption1 = eventReminder1RadBtn.getText().toString();
                                        eventReminder1Tv.setText(mReminderOption1);
                                        eventReminderInterval = 60*60*1000;
                                        Log.d("WHICH REMINDER OPTION?", String.valueOf(eventReminderInterval));
                                    }
                                    break;

                                case R.id.custom_reminder_radio:
                                    if(eventReminder1RadBtn.isChecked())
                                    {
                                        goToCustomOptionView();
                                    }
                                    break;


                            }
                            if(eventReminder1RadBtn.getId() != R.id.event_reminder_none_radio && eventReminder1RadBtn.getId() != R.id.custom_reminder_radio){

                                alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
                                myIntent = new Intent(ScheduleEventActivity.this, AlarmReceiver.class);
                                //myIntent.putExtra(AlarmReceiver.NOTIFICATION_ID,1);
                                //myIntent.putExtra(AlarmReceiver.NOTIFICATION, createAlarmNotification());

                                pendingIntent = PendingIntent.getBroadcast(ScheduleEventActivity.this, 0,myIntent,PendingIntent.FLAG_UPDATE_CURRENT);
                                Calendar alarmCalendar = Calendar.getInstance();
                                long alarmTimeInMillis = startDateTime.getTimeInMillis() - eventReminderInterval;
                                if(alarmTimeInMillis <= startDateTime.getTimeInMillis()){

                                    alarmCalendar.setTimeInMillis(alarmTimeInMillis);
                                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmCalendar.getTimeInMillis(), pendingIntent);
                                }
                            }
                        }

                        }
                });

                builder.show();

                }
        });



    }


    public Notification createAlarmNotification(){
        NotificationChannel notificationChannel = new NotificationChannel("CHANNEL_ID",
                "channel name", NotificationManager.IMPORTANCE_DEFAULT);
        android.support.v4.app.NotificationCompat.Builder notificationBuilder =
                new android.support.v4.app.NotificationCompat.Builder(this, "CHANNEL_ID")
                        .setContentTitle("Alarm")
                        .setContentText("Event started")
                        .setSmallIcon(R.drawable.baseline_add_alert_24);
        Notification notification = notificationBuilder.build();
        notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT;
        //NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        return notification;
    }

    public void goToCustomOptionView()
    {
        final AlertDialog.Builder customOptionBuilder = new AlertDialog.Builder(ScheduleEventActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View customOptionsView = (View)inflater.inflate(R.layout.activity_event_reminder_custom_option,null);

        final RadioGroup customOptionRg = customOptionsView.findViewById(R.id.event_reminder_custom_radio_group);
        customOptionRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                Log.d("Custom-radio-checked", "radio button checked");
            }
        });

        customOptionBuilder.setView(customOptionsView);
        customOptionBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                EditText customOptionEt = (EditText)customOptionsView.findViewById(R.id.event_reminder_custom_et);
                int checkedId = customOptionRg.getCheckedRadioButtonId();

                if(!customOptionEt.getText().toString().equals("") && checkedId != -1)
                {

                    switch (checkedId)
                    {
                        case R.id.event_reminder_custom_days_radio_btn:
                            String customReminderOption = customOptionEt.getText().toString();
                            int customReminderInterval = Integer.parseInt(customReminderOption);
                            eventReminderInterval = customReminderInterval*24*60*60*1000;
                            mReminderOption1 = String.valueOf(customReminderInterval) + " days before";
                            eventReminder1Tv.setText(mReminderOption1);

                        case R.id.event_reminder_custom_hours_radio_btn:
                            String customReminderOption2 = customOptionEt.getText().toString();
                            int customReminderInterval2 = Integer.parseInt(customReminderOption2);
                            eventReminderInterval = customReminderInterval2*60*60*1000;
                            mReminderOption1 = String.valueOf(customReminderInterval2) + " hours before";
                            eventReminder1Tv.setText(mReminderOption1);

                        case R.id.event_reminder_custom_minutes_radio_btn:
                            String customReminderOption3 = customOptionEt.getText().toString();
                            int customReminderInterval3 = Integer.parseInt(customReminderOption3);
                            eventReminderInterval = customReminderInterval3*60*1000;
                            mReminderOption1 = String.valueOf(customReminderInterval3) + " minutes before";
                            eventReminder1Tv.setText(mReminderOption1);
                    }





                    alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
                    myIntent = new Intent(ScheduleEventActivity.this, AlarmReceiver.class);
                    myIntent.putExtra(AlarmReceiver.NOTIFICATION_ID,1);
                    myIntent.putExtra(AlarmReceiver.NOTIFICATION, createAlarmNotification());

                    pendingIntent = PendingIntent.getBroadcast(ScheduleEventActivity.this, 0,myIntent,PendingIntent.FLAG_UPDATE_CURRENT);
                    Calendar alarmCalendar = Calendar.getInstance();
                    long alarmTimeInMillis = startDateTime.getTimeInMillis() - eventReminderInterval;
                    if(alarmTimeInMillis <= startDateTime.getTimeInMillis()){

                        alarmCalendar.setTimeInMillis(alarmTimeInMillis);
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmCalendar.getTimeInMillis(), pendingIntent);
                    }
                }

            }
        });

        customOptionBuilder.show();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("onResume","Schedule");
    }

    DatePickerDialog.OnDateSetListener mDateListenerStart = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
        {
            startDateTime.set(Calendar.YEAR, year);
            startDateTime.set(Calendar.MONTH, month);
            startDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            if(startDateTime.getTimeInMillis() <= endDateTime.getTimeInMillis()){
                updateStartDateTime();
                eventDateStartTv.setText(mSelectedStartDate);
            }
            else {
                if(flag){
                    updateStartDateTime();
                    eventDateStartTv.setText(mSelectedStartDate);

                }
                else {
                    Toast.makeText(ScheduleEventActivity.this, "Wrong date-time selection", Toast.LENGTH_SHORT).show();
                }
            }



        }
    };

    public void stopAlarm()
    {
        Log.d("STOPALARM of Schedule", "Schedule");
        Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        //pendingIntent.cancel();
        alarmManager.cancel(pendingIntent);
        vibrator.cancel();

        Intent intent = new Intent();
        intent.setAction("STOP_ME");
        intent.putExtra("STOP_RINGTONE",7);
        sendBroadcast(intent);

    }

    TimePickerDialog.OnTimeSetListener mTimeListenerStart = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute)
        {
            startDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            startDateTime.set(Calendar.MINUTE, minute);
            if(startDateTime.getTimeInMillis() <= endDateTime.getTimeInMillis())
            {
                updateStartDateTime();
                eventTimeStartTv.setText(mSelectedStartTime);
            }
            else {
                if(flag){
                    updateStartDateTime();
                    eventTimeStartTv.setText(mSelectedStartTime);
                }
                else {
                    Toast.makeText(ScheduleEventActivity.this, "Wrong date-time selection", Toast.LENGTH_SHORT).show();
                }
            }

        }
    };

    DatePickerDialog.OnDateSetListener mDateListenerEnd = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
        {

            endDateTime.set(Calendar.YEAR, year);
            endDateTime.set(Calendar.MONTH, month);
            endDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            if(endDateTime.getTimeInMillis() >= startDateTime.getTimeInMillis()){
                updateEndDateTime();
                eventDateEndTv.setText(mSelectedEndDate);
            }
            else {
                Toast.makeText(ScheduleEventActivity.this, "Wrong date-time selection", Toast.LENGTH_SHORT).show();
            }

        }
    };

    TimePickerDialog.OnTimeSetListener mTimeListenerEnd = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            endDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            endDateTime.set(Calendar.MINUTE, minute);
            if(endDateTime.getTimeInMillis() >= startDateTime.getTimeInMillis()){
                updateEndDateTime();
                eventTimeEndTv.setText(mSelectedEndTime);
            }
            else {
                Toast.makeText(ScheduleEventActivity.this, "Wrong date-time selection", Toast.LENGTH_SHORT).show();
            }
            }
    };



    public void displayKeyboardDialogTitle(final String entryItem){
        AlertDialog.Builder builder = new AlertDialog.Builder(ScheduleEventActivity.this);
        builder.setTitle(entryItem);
        //final EditText userInput = new EditText(ScheduleEventActivity.this);
        builder.setView(eventTitleEt);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                eventTitle = eventTitleEt.getText().toString();
                Log.d("DESCRIPTION", eventTitle);

            }
        });
    }
    public void displayKeyboardDialogDescription(final String entryItem){
        AlertDialog.Builder builder = new AlertDialog.Builder(ScheduleEventActivity.this);
        builder.setTitle(entryItem);
        //final EditText userInput = new EditText(ScheduleEventActivity.this);
        builder.setView(eventDescriptionEt);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                eventDescription = eventDescriptionEt.getText().toString();
                Log.d("DESCRIPTION", eventDescription);

            }
        });
    }


    public class StopAlarmServiceReciever extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            int rqs = intent.getIntExtra("STOP_ALARM",0);
            if(rqs == 3)
            {
                myIntent = new Intent(ScheduleEventActivity.this, AlarmReceiver.class);


                PendingIntent pendingIntent = PendingIntent.getBroadcast(ScheduleEventActivity.this, 0,myIntent,PendingIntent.FLAG_UPDATE_CURRENT);
                Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
                //pendingIntent.cancel();
                alarmManager.cancel(pendingIntent);
                vibrator.cancel();
                unregisterReceiver(stopAlarmServiceReciever);
                //AlarmReceiver.mediaPlayer.stop();
//                AlarmReceiver.mediaPlayer.release();
              //  AlarmReceiver.mediaPlayer = null;
                //AudioManager audioManager = (AudioManager)getSystemService(ScheduleEventActivity.AUDIO_SERVICE);
                //audioManager.stopBluetoothSco();
                //audioManager.setMicrophoneMute(true);


            }
        }
    }




    private void updateStartDateTime()
    {
        mSelectedStartTime = DateUtils.formatDateTime(ScheduleEventActivity.this, startDateTime.getTimeInMillis(),DateUtils.FORMAT_SHOW_TIME);
        mSelectedStartDate = DateUtils.formatDateTime(ScheduleEventActivity.this, startDateTime.getTimeInMillis(),DateUtils.FORMAT_SHOW_DATE);
    }

    private void updateEndDateTime()
    {
        mSelectedEndTime = DateUtils.formatDateTime(ScheduleEventActivity.this, endDateTime.getTimeInMillis(),DateUtils.FORMAT_SHOW_TIME);
        mSelectedEndDate = DateUtils.formatDateTime(ScheduleEventActivity.this, endDateTime.getTimeInMillis(),DateUtils.FORMAT_SHOW_DATE);
    }

    class AddEventTask extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... voids) {

            if(!isCancelled()){

                try{
                    ToDoEntry savedEvent = new ToDoEntry(eventTitle, eventLocation, eventDescription, startDateTime, endDateTime );
                    database.createItem(savedEvent);
                }
                catch (Exception e){

                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(getApplicationContext(), "Successfully added to database!", Toast.LENGTH_SHORT).show();
            addEventTask = null;
        }
    }

    class DeleteEventTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            if(!isCancelled())
            {
                database.deleteItem(eventToDisplay);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(getApplicationContext(), "Item successfully deleted!", Toast.LENGTH_SHORT).show();
            deleteEventTask = null;
           // finish();
        }
    }

    public class EventOptionsActivity extends Activity
    {
        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Log.d("EventOptionsActivity", "EVENTO");
            setContentView(R.layout.activity_event_reminder_options);
            //getIntent();


        }
    }


























}
