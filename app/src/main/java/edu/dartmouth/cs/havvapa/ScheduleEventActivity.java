package edu.dartmouth.cs.havvapa;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
    private RadioButton eventReminder2RadBtn;

    private String mSelectedTime;
    private String mSelectedDate;
    private String mSelectedStartDate;
    private String mSelectedEndDate;
    private String mSelectedStartTime;
    private String mSelectedEndTime;
    private String mReminderOption1;
    private String mReminderOption2;
    private long eventReminder1Interval;
    private long eventReminder2Interval;
    private long snoozeTime;
    private SwitchCompat eventAllDaySwitch;
    private long selectedEventId;

    private ToDoItemsSource database;
    //private EventReminderItemsSource reminderItemsDatabase;
    //private EventReminderItem eventReminderItem;
    private ToDoEntry eventToDisplay;
    private boolean modifyEvent;
    private Intent myIntent;

    boolean flag = true;
    private boolean reminder1Created;


    private AddEventTask addEventTask = null;
    private DeleteEventTask deleteEventTask = null;

    public AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private int eventUniqueTimestamp;
    private int eventUniqueTimestamp2;



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
                    database.modifyScheduledEvent(selectedEventId, eventTitle,eventLocation, eventDescription, startDateTime.getTimeInMillis(), endDateTime.getTimeInMillis(), mReminderOption1);
                    Intent modifyEventIntent = new Intent(ScheduleEventActivity.this, GreetingsActivity.class);
                    startActivity(modifyEventIntent);
                    finish();

                }
                else {
                    addEventTask = new AddEventTask();
                    addEventTask.execute();

                    Intent scheduledEventIntent = new Intent(this, GreetingsActivity.class);
                    startActivity(scheduledEventIntent);
                    finish();
                }


                return true;

            case R.id.delete_event_btn:

                deleteEventTask = new DeleteEventTask();
                deleteEventTask.execute();
                Intent deleteEventIntent = new Intent(this, GreetingsActivity.class);
                startActivity(deleteEventIntent);
                finish();
                return true;


            case R.id.menuitem_settings:
                startActivity(new Intent(ScheduleEventActivity.this, AlarmManagmentActivity.class));
                finish();
                return true;

            case R.id.menuitem_editProfile:
                startActivity(new Intent(this, SignUpActivity.class));
                finish();
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
        outState.putString("REMINDER_2",mReminderOption2);
        outState.putLong("EVENT_REMINDER_1_INTERVAL", eventReminder1Interval);
        outState.putLong("EVENT_REMINDER_2_INTERVAL", eventReminder2Interval);
        outState.putBoolean("BOOLEAN_FLAG", false);
        outState.putBoolean("BOOLEAN_REMINDER_1", reminder1Created);
        outState.putLong("SNOOZE_TIME", snoozeTime);
        outState.putInt("UNIQUE_TIMESTAMP",eventUniqueTimestamp);
        outState.putInt("UNIQUE_TIMESTAMP_2", eventUniqueTimestamp2);
        outState.putLong("EVENT_DB_ID", selectedEventId);
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
        eventAllDaySwitch = findViewById(R.id.event_all_day);

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
            mReminderOption2 = savedInstanceState.getString("REMINDER_2");
            eventReminder1Interval = savedInstanceState.getLong("EVENT_REMINDER_1_INTERVAL");
            eventReminder2Interval = savedInstanceState.getLong("EVENT_REMINDER_2_INTERVAL");
            flag = savedInstanceState.getBoolean("BOOLEAN_FLAG");
            reminder1Created = savedInstanceState.getBoolean("BOOLEAN_REMINDER_1");
            //snoozeTime = savedInstanceState.getLong("SNOOZE_TIME");
            eventUniqueTimestamp = savedInstanceState.getInt("UNIQUE_TIMESTAMP");
            eventUniqueTimestamp2 = savedInstanceState.getInt("UNIQUE_TIMESTAMP_2");
            selectedEventId = savedInstanceState.getLong("EVENT_DB_ID");

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
                selectedEventId = getIntent().getExtras().getLong("ENTRY_ROW_ID");
                eventToDisplay = database.fetchEntryByIndex(selectedEventId);
                //eventReminderItem = reminderItemsDatabase.getReminderItem();
                //snoozeTime = eventReminderItem.getSnoozePref();
                startDateTime = eventToDisplay.getStartDateTime();
                endDateTime = eventToDisplay.getEndDateTime();
                eventUniqueTimestamp = eventToDisplay.getEventUniqueTimestamp();
                eventUniqueTimestamp2 = eventToDisplay.getEventUniqueTimestamp2();
                mReminderOption1 = eventToDisplay.getEventReminderOption();

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
                eventReminder1Tv.setText(mReminderOption1);

                if(!mReminderOption1.equals("Add a reminder") && !mReminderOption1.equals("No reminder")){
                    reminder1Created=true;
                    eventReminder2Tv.setAlpha(1);
                }
                else {
                    reminder1Created = false;
                }

                modifyEvent = true;


            }
            catch (Exception e)
            {
                modifyEvent = false;
                snoozeTime = 0;
                startDateTime = Calendar.getInstance();
                endDateTime = Calendar.getInstance();
                mReminderOption1 = "Add a reminder";
                mReminderOption2 = "Add a reminder";
                eventUniqueTimestamp = createUniqueTimestamp();
                eventUniqueTimestamp2= createUniqueTimestamp() + (int)(Math.random() * 1000);
                reminder1Created = false;

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


        eventAllDaySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked)
                {
                    Log.d("IsAllDayChecked", "Checked");
                    eventTimeStartTv.setVisibility(View.INVISIBLE);
                    eventTimeEndTv.setVisibility(View.INVISIBLE);
                    eventTimeStartTv.setOnClickListener(null);
                    eventTimeEndTv.setOnClickListener(null);
                }

                else
                    {
                        Log.d("IsAllDayChecked", "Not Checked");
                        eventTimeStartTv.setVisibility(View.VISIBLE);
                        eventTimeEndTv.setVisibility(View.VISIBLE);
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

                    }
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
                                        eventReminder1Interval =0;
                                        Log.d("WHICH REMINDER OPTION?", String.valueOf(eventReminder1Interval));
                                    }
                                    break;

                                case R.id.event_reminder_1_minute_before_radio:
                                    if(eventReminder1RadBtn.isChecked())
                                    {
                                        mReminderOption1 = eventReminder1RadBtn.getText().toString();
                                        eventReminder1Tv.setText(mReminderOption1);
                                        eventReminder1Interval = 60*1000;
                                        Log.d("WHICH REMINDER OPTION?", String.valueOf(eventReminder1Interval));
                                    }
                                    break;
                                case R.id.event_reminder_5_minutes_before_radio:
                                    if(eventReminder1RadBtn.isChecked())
                                    {
                                        mReminderOption1 = eventReminder1RadBtn.getText().toString();
                                        eventReminder1Tv.setText(mReminderOption1);
                                        eventReminder1Interval = 5 * 60 *1000;
                                        Log.d("WHICH REMINDER OPTION?", String.valueOf(eventReminder1Interval));
                                    }
                                    break;
                                case R.id.event_reminder_10_minutes_before_radio:
                                    if(eventReminder1RadBtn.isChecked())
                                    {
                                        mReminderOption1 = eventReminder1RadBtn.getText().toString();
                                        eventReminder1Tv.setText(mReminderOption1);
                                        eventReminder1Interval = 10*60*1000;
                                        Log.d("WHICH REMINDER OPTION?", String.valueOf(eventReminder1Interval));
                                    }
                                    break;
                                case R.id.event_reminder_20_minutes_before_radio:
                                    if(eventReminder1RadBtn.isChecked())
                                    {
                                        mReminderOption1 = eventReminder1RadBtn.getText().toString();
                                        eventReminder1Tv.setText(mReminderOption1);
                                        eventReminder1Interval = 20*60*1000;
                                        Log.d("WHICH REMINDER OPTION?", String.valueOf(eventReminder1Interval));

                                    }
                                    break;
                                case R.id.event_reminder_30_minutes_before_radio:
                                    if(eventReminder1RadBtn.isChecked())
                                    {
                                        mReminderOption1 = eventReminder1RadBtn.getText().toString();
                                        eventReminder1Tv.setText(mReminderOption1);
                                        eventReminder1Interval = 30*60*1000;
                                        Log.d("WHICH REMINDER OPTION?", String.valueOf(eventReminder1Interval));
                                    }
                                    break;
                                case R.id.event_reminder_45_minutes_before_radio:
                                    if(eventReminder1RadBtn.isChecked())
                                    {
                                        mReminderOption1 = eventReminder1RadBtn.getText().toString();
                                        eventReminder1Tv.setText(mReminderOption1);
                                        eventReminder1Interval = 45*60*1000;
                                        Log.d("WHICH REMINDER OPTION?", String.valueOf(eventReminder1Interval));
                                    }
                                    break;

                                case R.id.event_reminder_1_hour_before_radio:
                                    if(eventReminder1RadBtn.isChecked())
                                    {
                                        mReminderOption1 = eventReminder1RadBtn.getText().toString();
                                        eventReminder1Tv.setText(mReminderOption1);
                                        eventReminder1Interval = 60*60*1000;
                                        Log.d("WHICH REMINDER OPTION?", String.valueOf(eventReminder1Interval));
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
                                myIntent.putExtra("EVENT_TIMESTAMP", eventUniqueTimestamp);

                                pendingIntent = PendingIntent.getBroadcast(ScheduleEventActivity.this, eventUniqueTimestamp,myIntent,PendingIntent.FLAG_CANCEL_CURRENT);
                                Calendar alarmCalendar = Calendar.getInstance();
                                long alarmTimeInMillis = startDateTime.getTimeInMillis() - eventReminder1Interval;

                                Calendar currCal = Calendar.getInstance();
                                if((alarmTimeInMillis <= startDateTime.getTimeInMillis()) && (startDateTime.getTimeInMillis() >= currCal.getTimeInMillis())){

                                    alarmCalendar.setTimeInMillis(alarmTimeInMillis);
                                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmCalendar.getTimeInMillis(), pendingIntent);
                                    reminder1Created = true;
                                    eventReminder2Tv.setAlpha(1);
                                }
                            }
                        }
                        else {
                            mReminderOption1 = null;
                        }

                        }
                });

                builder.show();

                }
        });


        if(reminder1Created){
            eventReminder2Tv.setOnClickListener(new View.OnClickListener() {
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
                            eventReminder2RadBtn =(RadioButton)view.findViewById(checkedId);


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

                                switch (eventReminder2RadBtn.getId())
                                {
                                    case R.id.event_reminder_none_radio:
                                        if(eventReminder2RadBtn.isChecked())
                                        {
                                            mReminderOption2 = eventReminder2RadBtn.getText().toString();
                                            eventReminder2Tv.setText(mReminderOption2);
                                            if (alarmManager != null){
                                                alarmManager.cancel(pendingIntent);
                                            }

                                        }
                                        break;

                                    case R.id.event_reminder_start_radio:
                                        if(eventReminder2RadBtn.isChecked())
                                        {
                                            mReminderOption2 = eventReminder2RadBtn.getText().toString();
                                            eventReminder2Tv.setText(mReminderOption2);
                                            eventReminder2Interval =0;
                                            Log.d("WHICH REMINDER OPTION?", String.valueOf(eventReminder2Interval));
                                        }
                                        break;

                                    case R.id.event_reminder_1_minute_before_radio:
                                        if(eventReminder2RadBtn.isChecked())
                                        {
                                            mReminderOption2 = eventReminder2RadBtn.getText().toString();
                                            eventReminder2Tv.setText(mReminderOption2);
                                            eventReminder2Interval = 60*1000;
                                            Log.d("WHICH REMINDER OPTION?", String.valueOf(eventReminder2Interval));
                                        }
                                        break;
                                    case R.id.event_reminder_5_minutes_before_radio:
                                        if(eventReminder2RadBtn.isChecked())
                                        {
                                            mReminderOption2 = eventReminder2RadBtn.getText().toString();
                                            eventReminder2Tv.setText(mReminderOption2);
                                            eventReminder2Interval = 5 * 60 *1000;
                                            Log.d("WHICH REMINDER OPTION?", String.valueOf(eventReminder2Interval));
                                        }
                                        break;
                                    case R.id.event_reminder_10_minutes_before_radio:
                                        if(eventReminder2RadBtn.isChecked())
                                        {
                                            mReminderOption2 = eventReminder2RadBtn.getText().toString();
                                            eventReminder2Tv.setText(mReminderOption2);
                                            eventReminder2Interval = 10*60*1000;
                                            Log.d("WHICH REMINDER OPTION?", String.valueOf(eventReminder2Interval));
                                        }
                                        break;
                                    case R.id.event_reminder_20_minutes_before_radio:
                                        if(eventReminder2RadBtn.isChecked())
                                        {
                                            mReminderOption2 = eventReminder2RadBtn.getText().toString();
                                            eventReminder2Tv.setText(mReminderOption2);
                                            eventReminder2Interval = 20*60*1000;
                                            Log.d("WHICH REMINDER OPTION?", String.valueOf(eventReminder2Interval));

                                        }
                                        break;
                                    case R.id.event_reminder_30_minutes_before_radio:
                                        if(eventReminder2RadBtn.isChecked())
                                        {
                                            mReminderOption2 = eventReminder2RadBtn.getText().toString();
                                            eventReminder2Tv.setText(mReminderOption2);
                                            eventReminder2Interval = 30*60*1000;
                                            Log.d("WHICH REMINDER OPTION?", String.valueOf(eventReminder2Interval));
                                        }
                                        break;
                                    case R.id.event_reminder_45_minutes_before_radio:
                                        if(eventReminder2RadBtn.isChecked())
                                        {
                                            mReminderOption2= eventReminder2RadBtn.getText().toString();
                                            eventReminder2Tv.setText(mReminderOption2);
                                            eventReminder2Interval = 45*60*1000;
                                            Log.d("WHICH REMINDER OPTION?", String.valueOf(eventReminder2Interval));
                                        }
                                        break;

                                    case R.id.event_reminder_1_hour_before_radio:
                                        if(eventReminder2RadBtn.isChecked())
                                        {
                                            mReminderOption2 = eventReminder2RadBtn.getText().toString();
                                            eventReminder2Tv.setText(mReminderOption2);
                                            eventReminder2Interval = 60*60*1000;
                                            Log.d("WHICH REMINDER OPTION?", String.valueOf(eventReminder2Interval));
                                        }
                                        break;

                                    case R.id.custom_reminder_radio:
                                        if(eventReminder2RadBtn.isChecked())
                                        {
                                            goToCustomOptionView2();
                                        }
                                        break;


                                }
                                if(eventReminder2RadBtn.getId() != R.id.event_reminder_none_radio && eventReminder2RadBtn.getId() != R.id.custom_reminder_radio){

                                    alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
                                    myIntent = new Intent(ScheduleEventActivity.this, AlarmReceiver.class);
                                    //myIntent.putExtra(AlarmReceiver.NOTIFICATION_ID,1);
                                    //myIntent.putExtra(AlarmReceiver.NOTIFICATION, createAlarmNotification());
                                    myIntent.putExtra("EVENT_TIMESTAMP", eventUniqueTimestamp2);

                                    pendingIntent = PendingIntent.getBroadcast(ScheduleEventActivity.this, eventUniqueTimestamp2,myIntent,PendingIntent.FLAG_CANCEL_CURRENT);
                                    Calendar alarmCalendar = Calendar.getInstance();
                                    long alarmTimeInMillis = startDateTime.getTimeInMillis() - eventReminder2Interval;

                                    Calendar currCal = Calendar.getInstance();
                                    if((alarmTimeInMillis <= startDateTime.getTimeInMillis()) && (startDateTime.getTimeInMillis() >= currCal.getTimeInMillis())){

                                        alarmCalendar.setTimeInMillis(alarmTimeInMillis);
                                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmCalendar.getTimeInMillis(), pendingIntent);
                                    }
                                }
                            }
                            else {
                                mReminderOption2 = null;
                            }

                        }
                    });

                    builder.show();

                }
            });

        }



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
                            eventReminder1Interval = customReminderInterval*24*60*60*1000;
                            mReminderOption1 = String.valueOf(customReminderInterval) + " days before";
                            eventReminder1Tv.setText(mReminderOption1);

                        case R.id.event_reminder_custom_hours_radio_btn:
                            String customReminderOption2 = customOptionEt.getText().toString();
                            int customReminderInterval2 = Integer.parseInt(customReminderOption2);
                            eventReminder1Interval = customReminderInterval2*60*60*1000;
                            mReminderOption1 = String.valueOf(customReminderInterval2) + " hours before";
                            eventReminder1Tv.setText(mReminderOption1);

                        case R.id.event_reminder_custom_minutes_radio_btn:
                            String customReminderOption3 = customOptionEt.getText().toString();
                            int customReminderInterval3 = Integer.parseInt(customReminderOption3);
                            eventReminder1Interval = customReminderInterval3*60*1000;
                            mReminderOption1 = String.valueOf(customReminderInterval3) + " minutes before";
                            eventReminder1Tv.setText(mReminderOption1);
                    }





                    alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
                    myIntent = new Intent(ScheduleEventActivity.this, AlarmReceiver.class);
                    myIntent.putExtra("EVENT_TIMESTAMP", eventUniqueTimestamp);

                    pendingIntent = PendingIntent.getBroadcast(ScheduleEventActivity.this, eventUniqueTimestamp,myIntent,PendingIntent.FLAG_CANCEL_CURRENT);
                    Calendar alarmCalendar = Calendar.getInstance();
                    long alarmTimeInMillis = startDateTime.getTimeInMillis() - eventReminder1Interval;

                    Calendar currCal = Calendar.getInstance();
                    if((alarmTimeInMillis <= startDateTime.getTimeInMillis()) && (startDateTime.getTimeInMillis() >= currCal.getTimeInMillis())){

                        alarmCalendar.setTimeInMillis(alarmTimeInMillis);
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmCalendar.getTimeInMillis(), pendingIntent);
                    }
                }

            }
        });

        customOptionBuilder.show();

    }

    public void goToCustomOptionView2()
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
                            eventReminder2Interval = customReminderInterval*24*60*60*1000;
                            mReminderOption2 = String.valueOf(customReminderInterval) + " days before";
                            eventReminder2Tv.setText(mReminderOption2);

                        case R.id.event_reminder_custom_hours_radio_btn:
                            String customReminderOption2 = customOptionEt.getText().toString();
                            int customReminderInterval2 = Integer.parseInt(customReminderOption2);
                            eventReminder2Interval = customReminderInterval2*60*60*1000;
                            mReminderOption2 = String.valueOf(customReminderInterval2) + " hours before";
                            eventReminder2Tv.setText(mReminderOption2);

                        case R.id.event_reminder_custom_minutes_radio_btn:
                            String customReminderOption3 = customOptionEt.getText().toString();
                            int customReminderInterval3 = Integer.parseInt(customReminderOption3);
                            eventReminder2Interval = customReminderInterval3*60*1000;
                            mReminderOption2 = String.valueOf(customReminderInterval3) + " minutes before";
                            eventReminder2Tv.setText(mReminderOption2);
                    }





                    alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
                    myIntent = new Intent(ScheduleEventActivity.this, AlarmReceiver.class);
                    myIntent.putExtra("EVENT_TIMESTAMP", eventUniqueTimestamp2);

                    pendingIntent = PendingIntent.getBroadcast(ScheduleEventActivity.this, eventUniqueTimestamp2,myIntent,PendingIntent.FLAG_CANCEL_CURRENT);
                    Calendar alarmCalendar = Calendar.getInstance();
                    long alarmTimeInMillis = startDateTime.getTimeInMillis() - eventReminder2Interval;

                    Calendar currCal = Calendar.getInstance();
                    if((alarmTimeInMillis <= startDateTime.getTimeInMillis()) && (startDateTime.getTimeInMillis() >= currCal.getTimeInMillis())){

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


    public int createUniqueTimestamp(){
        Date now = new Date();
        int id = Integer.parseInt(new SimpleDateFormat("ddHHmmss",  Locale.US).format(now));
        Log.d("UniqueID  ", String.valueOf(id));
        return id;
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
                    ToDoEntry savedEvent = new ToDoEntry(eventTitle, eventLocation, eventDescription, startDateTime, endDateTime,mReminderOption1, eventUniqueTimestamp, eventUniqueTimestamp2);
                    database.createItem(savedEvent);
                    Toast.makeText(ScheduleEventActivity.this, "Successfully added to database!", Toast.LENGTH_SHORT).show();
                }
                catch (Exception e){

                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            addEventTask = null;
        }
    }

    class DeleteEventTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            if(!isCancelled())
            {
                try{
                    database.deleteItem(eventToDisplay);
                    Toast.makeText(ScheduleEventActivity.this, "Item successfully deleted!", Toast.LENGTH_SHORT).show();
                }
                catch (Exception e){

                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            deleteEventTask = null;
           // finish();
        }
    }



}
