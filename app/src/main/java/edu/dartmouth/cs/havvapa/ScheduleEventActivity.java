package edu.dartmouth.cs.havvapa;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;

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

                Intent scheduleIntent = new Intent(ScheduleEventActivity.this, MainActivity.class);
                scheduleIntent.putExtra("event_title",eventTitle);
                scheduleIntent.putExtra("event_description",eventDescription);
                scheduleIntent.putExtra("event_time",mSelectedTime);
                scheduleIntent.putExtra("event_date",mSelectedDate);
                startActivity(scheduleIntent);

            case R.id.menuitem_settings:
                startActivity(new Intent(ScheduleEventActivity.this, SignUpActivity.class));

            case R.id.menuitem_editProfile:
                startActivity(new Intent(ScheduleEventActivity.this, SignUpActivity.class));

            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_event);

        eventTitleEt = findViewById(R.id.to_do_item_title);
        eventDescriptionEt = findViewById(R.id.to_do_item_description);
        eventDateBtn = findViewById(R.id.to_do_item_date);
        eventTimeBtn = findViewById(R.id.to_do_item_time);

        eventTitle = eventTitleEt.getText().toString();
        eventDescription = eventDescriptionEt.getText().toString();
        dateTime = Calendar.getInstance();

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

        eventTitleEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventTitle = eventTitleEt.getText().toString();
            }
        });

        eventDescriptionEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventDescription = eventDescriptionEt.getText().toString();
            }
        });



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

    private void updateDateAndTimeDisplay()
    {
        mSelectedTime = DateUtils.formatDateTime(ScheduleEventActivity.this, dateTime.getTimeInMillis(),DateUtils.FORMAT_SHOW_TIME);
        mSelectedDate = DateUtils.formatDateTime(ScheduleEventActivity.this, dateTime.getTimeInMillis(),DateUtils.FORMAT_SHOW_DATE);
    }
}
