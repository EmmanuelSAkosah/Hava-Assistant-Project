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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import edu.dartmouth.cs.havvapa.database_elements.ToDoItemsSource;
import edu.dartmouth.cs.havvapa.models.ToDoEntry;

public class ScheduleEventActivity extends AppCompatActivity
{

    private String eventTitle;
    private String eventDescription;
    private Calendar dateTime;

    private  EditText eventTitleEt;
    private EditText eventDescriptionEt;
    private Button eventDateBtn;
    private Button eventTimeBtn;

    private String mSelectedTime;
    private String mSelectedDate;

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
                eventDescription = eventDescriptionEt.getText().toString();
                Log.d("TITLE", eventTitle);
                Log.d("DESCRIPTION", eventDescription);


                /*
                Intent scheduleIntent = new Intent(ScheduleEventActivity.this, MainActivity.class);
                scheduleIntent.putExtra("event_title",eventTitle);
                scheduleIntent.putExtra("event_description",eventDescription);
                scheduleIntent.putExtra("event_time",mSelectedTime);
                scheduleIntent.putExtra("event_date",mSelectedDate);
                startActivity(scheduleIntent);*/
                if(modifyEvent)
                {
                    database.modifyScheduledEvent(eventToDisplay.getId(), eventTitle, eventDescription, dateTime.getTimeInMillis());
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
        outState.putString("EVENT_DESCRIPTION", eventDescription);
        outState.putSerializable("CALENDAR_KEY", dateTime);
        outState.putString("DATE_KEY", mSelectedDate);
        outState.putString("TIME_KEY", mSelectedTime);
        outState.putBoolean("MODIFY_EVENT", modifyEvent);
        outState.putBoolean("BOOLEAN_FLAG", false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_event);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Schedule your event");

        database = new ToDoItemsSource(this);

        eventTitleEt = findViewById(R.id.to_do_item_title);
        eventDescriptionEt = findViewById(R.id.to_do_item_description);
        eventDateBtn = findViewById(R.id.to_do_item_date);
        eventTimeBtn = findViewById(R.id.to_do_item_time);

        if(savedInstanceState!=null){
            eventTitle = savedInstanceState.getString("EVENT_TITLE");
            eventDescription = savedInstanceState.getString("EVENT_DESCRIPTION");
            dateTime = (Calendar)savedInstanceState.getSerializable("CALENDAR_KEY");
            mSelectedDate = savedInstanceState.getString("DATE_KEY");
            mSelectedTime = savedInstanceState.getString("TIME_KEY");
            modifyEvent = savedInstanceState.getBoolean("MODIFY_EVENT");
            flag = savedInstanceState.getBoolean("BOOLEAN_FLAG");
        }

        if(flag)
        {
            try
            {
                long selectedEventId = getIntent().getExtras().getLong("ENTRY_ROW_ID");
                eventToDisplay = database.fetchEntryByIndex(selectedEventId);
                dateTime = eventToDisplay.getDateTime();
                updateDateAndTimeDisplay();

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
                dateTime = Calendar.getInstance();

            }
        }


        eventDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                new DatePickerDialog(ScheduleEventActivity.this, mDateListener, dateTime.get(Calendar.YEAR),
                        dateTime.get(Calendar.MONTH), dateTime.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        eventTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(ScheduleEventActivity.this, mTimeListener, dateTime.get(Calendar.HOUR_OF_DAY), dateTime.get(Calendar.MINUTE), true).show();

            }
        });
        /*
        eventTitleEt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //displayKeyboardDialogTitle("Event name");
                AlertDialog.Builder builder = new AlertDialog.Builder(ScheduleEventActivity.this);
                builder.setTitle("Event title");
                builder.setView(eventTitleEt);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        eventTitle = eventTitleEt.getText().toString();
                        Log.d("DESCRIPTION", eventTitle);
                    }
                });
                builder.create();
                builder.show();



            }
        });

        eventDescriptionEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // displayKeyboardDialogDescription("Event description");

                AlertDialog.Builder builder = new AlertDialog.Builder(ScheduleEventActivity.this);
                builder.setTitle("Event title");
                builder.setView(eventDescriptionEt);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        eventDescription = eventDescriptionEt.getText().toString();
                        Log.d("DESCRIPTION", eventDescription);
                    }
                });
                builder.create();
                builder.show();
            }
        });*/






    }

    DatePickerDialog.OnDateSetListener mDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

            dateTime.set(Calendar.YEAR, year);
            dateTime.set(Calendar.MONTH, month);
            dateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateAndTimeDisplay();


        }
    };

    TimePickerDialog.OnTimeSetListener mTimeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateTime.set(Calendar.MINUTE, minute);
            updateDateAndTimeDisplay();




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

    private void updateDateAndTimeDisplay()
    {
        mSelectedTime = DateUtils.formatDateTime(ScheduleEventActivity.this, dateTime.getTimeInMillis(),DateUtils.FORMAT_SHOW_TIME);
        mSelectedDate = DateUtils.formatDateTime(ScheduleEventActivity.this, dateTime.getTimeInMillis(),DateUtils.FORMAT_SHOW_DATE);
    }

    class AddEventTask extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... voids) {

            if(!isCancelled()){

                try{
                    ToDoEntry savedEvent = new ToDoEntry(eventTitle, eventDescription, dateTime);
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
