package edu.dartmouth.cs.havvapa;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

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

    private String mSelectedTime;
    private String mSelectedDate;
    private String mSelectedStartDate;
    private String mSelectedEndDate;
    private String mSelectedStartTime;
    private String mSelectedEndTime;

    private ToDoItemsSource database;
    private ToDoEntry eventToDisplay;
    private boolean modifyEvent;

    boolean flag = true;


    private AddEventTask addEventTask = null;
    private DeleteEventTask deleteEventTask = null;


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
                }
                else {
                    addEventTask = new AddEventTask();
                    addEventTask.execute();

                    Intent scheduledEventIntent = new Intent(ScheduleEventActivity.this, MainActivity.class);
                    startActivity(scheduledEventIntent);
                }


                return true;

            case R.id.delete_event_btn:

                deleteEventTask = new DeleteEventTask();
                deleteEventTask.execute();
                Intent deleteEventIntent = new Intent(ScheduleEventActivity.this, MainActivity.class);
                startActivity(deleteEventIntent);


            case R.id.menuitem_settings:
                startActivity(new Intent(ScheduleEventActivity.this, SignUpActivity.class));
                return true;

            case R.id.menuitem_editProfile:
                startActivity(new Intent(ScheduleEventActivity.this, SignUpActivity.class));
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
        outState.putBoolean("BOOLEAN_FLAG", false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_schedule_event);
        setContentView(R.layout.activity_schedule_event_vol2);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Schedule your event");

        database = new ToDoItemsSource(this);

        eventTitleEt = findViewById(R.id.event_title);
        eventLocationEt = findViewById(R.id.event_location);
        eventDescriptionEt = findViewById(R.id.event_description);
        eventDateStartTv = findViewById(R.id.event_start_date);
        eventDateEndTv = findViewById(R.id.event_end_date);
        eventTimeStartTv = findViewById(R.id.event_start_time);
        eventTimeEndTv = findViewById(R.id.event_end_time);

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
            flag = savedInstanceState.getBoolean("BOOLEAN_FLAG");

            eventTimeStartTv.setText(mSelectedStartTime);
            eventTimeEndTv.setText(mSelectedEndTime);
            eventDateStartTv.setText(mSelectedStartDate);
            eventDateEndTv.setText(mSelectedEndDate);
        }

        if(flag)
        {
            try
            {
                long selectedEventId = getIntent().getExtras().getLong("ENTRY_ROW_ID");
                eventToDisplay = database.fetchEntryByIndex(selectedEventId);
                startDateTime = eventToDisplay.getStartDateTime();
                endDateTime = eventToDisplay.getEndDateTime();
                updateStartDateTime();
                updateEndDateTime();

                eventTimeStartTv.setText(mSelectedStartTime);
                eventTimeEndTv.setText(mSelectedEndTime);
                eventDateStartTv.setText(mSelectedStartDate);
                eventDateEndTv.setText(mSelectedEndDate);

                eventTitle = eventToDisplay.getEventTitle();
                eventDescription = eventToDisplay.getEventDescription();
                Log.d("TITLE1", eventTitle);
                Log.d("DESCRIPTION1", eventDescription);

                eventTitleEt.setText(eventTitle);
                eventDescriptionEt.setText(eventDescription);

                modifyEvent = true;


            }
            catch (Exception e)
            {
                modifyEvent = false;
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

    }

    DatePickerDialog.OnDateSetListener mDateListenerStart = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

            startDateTime.set(Calendar.YEAR, year);
            startDateTime.set(Calendar.MONTH, month);
            startDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateStartDateTime();

            eventDateStartTv.setText(mSelectedStartDate);


        }
    };

    TimePickerDialog.OnTimeSetListener mTimeListenerStart = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            startDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            startDateTime.set(Calendar.MINUTE, minute);
            updateStartDateTime();

            eventTimeStartTv.setText(mSelectedStartTime);
        }
    };

    DatePickerDialog.OnDateSetListener mDateListenerEnd = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

            endDateTime.set(Calendar.YEAR, year);
            endDateTime.set(Calendar.MONTH, month);
            endDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateEndDateTime();

            eventDateEndTv.setText(mSelectedEndDate);
        }
    };

    TimePickerDialog.OnTimeSetListener mTimeListenerEnd = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            endDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            endDateTime.set(Calendar.MINUTE, minute);
            updateEndDateTime();

            eventTimeEndTv.setText(mSelectedEndTime);


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


























}
