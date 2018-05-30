package edu.dartmouth.cs.havvapa.AlarmHelpers;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import edu.dartmouth.cs.havvapa.user_records.KeepUserRecords;
import edu.dartmouth.cs.havvapa.R;
import edu.dartmouth.cs.havvapa.database_elements.EventReminderItemsSource;
import edu.dartmouth.cs.havvapa.models.EventReminderItem;
import edu.dartmouth.cs.havvapa.utils.Preferences;

public class AlarmManagmentActivity extends AppCompatActivity
{
    private SwitchCompat reminderSoundOptionSwitch;
    private SwitchCompat reminderVibrationSwitch;
    private SwitchCompat mutePreferenceSwitch;
    private TextView reminderSnoozeTimeIndicatorTv;
    private TextView reminderSnoozeTimeTv;
    //private TextView alarmCloseTv;
    private RelativeLayout reminderSnoozeOptionsBox;
    private RadioButton reminderSnoozeOptionRb;
    private KeepUserRecords userRecords;

    private EventReminderItemsSource datasource;
    private EventReminderItem reminderItem;

    private String selectedSnoozeOptionText;
    private long selectedSnoozeOptionLong;
    private boolean vibrationOn;
    private boolean soundOn;
    private Preferences pref;

    private boolean itemExists;
    private boolean flag=true;

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_alarm_managment_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.save_recorded_reminder_settings:
                if(!itemExists)
                {

                    reminderItem = new EventReminderItem(selectedSnoozeOptionLong,vibrationOn,soundOn);
                    datasource.createItem(reminderItem);
                    Toast.makeText(this, "created your reminder Settings", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(this, "modified your reminder Settings", Toast.LENGTH_SHORT).show();
                }
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

        outState.putString("snooze_option_text", selectedSnoozeOptionText);
        outState.putLong("snooze_option_long", selectedSnoozeOptionLong);
        outState.putBoolean("boolean_vibration", vibrationOn);
        outState.putBoolean("boolean_sound_on", soundOn);
        outState.putBoolean("boolean_item_exists", itemExists);
        outState.putBoolean("boolean_flag", false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_managment);

        getSupportActionBar().setTitle("Reminder Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        pref = new Preferences(getApplicationContext());


        reminderSoundOptionSwitch = findViewById(R.id.reminder_sound_option);
        reminderVibrationSwitch = findViewById(R.id.reminder_vibration_option);
        reminderSnoozeTimeIndicatorTv = findViewById(R.id.reminder_snooze_time_indicator);
        reminderSnoozeTimeTv = findViewById(R.id.reminder_snooze_time);
        mutePreferenceSwitch = findViewById(R.id.mute_preference_switch);
        reminderSnoozeOptionsBox = findViewById(R.id.reminder_snooze_options_box);

        datasource = new EventReminderItemsSource(this);
        //reminderItem = new EventReminderItem();
        userRecords = new KeepUserRecords(getApplicationContext());
        if(savedInstanceState!=null){
            selectedSnoozeOptionText = savedInstanceState.getString("snooze_option_text");
            selectedSnoozeOptionLong = savedInstanceState.getLong("snooze_option_long");
            vibrationOn = savedInstanceState.getBoolean("boolean_vibration");
            soundOn = savedInstanceState.getBoolean("boolean_sound_on");
            itemExists = savedInstanceState.getBoolean("boolean_item_exists");
            flag = savedInstanceState.getBoolean("boolean_flag");

            reminderSnoozeTimeTv.setText(selectedSnoozeOptionText);

        }

        try
        {

            reminderItem = datasource.getReminderItem();
            itemExists = true;
        }

        catch (Exception e){
            reminderItem = new EventReminderItem();
            itemExists = false;
        }

        if(flag)
        {
            if(itemExists)
            {
                soundOn = reminderItem.getSoundPref();
                vibrationOn = reminderItem.getVibrationPref();
                selectedSnoozeOptionLong = reminderItem.getSnoozePref();
                int alreadyRecordedSnooze = (int)(reminderItem.getSnoozePref())/(1000 * 60);
                if(alreadyRecordedSnooze == 60){
                    selectedSnoozeOptionText = String.valueOf(1) + " hour";
                }
                else selectedSnoozeOptionText = String.valueOf(alreadyRecordedSnooze) + " minutes";

                reminderSoundOptionSwitch.setChecked(soundOn);
                reminderVibrationSwitch.setChecked(vibrationOn);
                reminderSnoozeTimeTv.setText(selectedSnoozeOptionText);


            }
            else {
                soundOn = false;
                vibrationOn = false;
                selectedSnoozeOptionLong = 0;

                reminderSoundOptionSwitch.setChecked(soundOn);
                reminderVibrationSwitch.setChecked(vibrationOn);
                reminderSnoozeTimeTv.setText("No snooze!");
            }

        }

        reminderVibrationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(isChecked)
                {
                    userRecords.setVibrationPref(true);
                    vibrationOn = true;
                    Log.d("Is Vib?", String.valueOf(isChecked));

                    if(itemExists)
                    {
                        datasource.updateItemVibrationPref(reminderItem.getEventReminderId(), true);
                    }
                    reminderItem.setVibrationPref(true);
                }
                else {
                    userRecords.setVibrationPref(false);
                    vibrationOn = false;
                    Log.d("Is Vib?", String.valueOf(isChecked));

                    if(itemExists){
                        datasource.updateItemVibrationPref(reminderItem.getEventReminderId(), false);
                    }
                    reminderItem.setVibrationPref(false);
                }
            }
        });

        reminderSoundOptionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    userRecords.setSoundPref(true);
                    soundOn = true;
                    Log.d("Is Sound?", String.valueOf(isChecked));

                    if(itemExists){
                        datasource.updateItemSoundPref(reminderItem.getEventReminderId(), true);
                    }
                    reminderItem.setSoundPref(true);
                }
                else {
                    userRecords.setSoundPref(false);
                    soundOn = false;
                    if(itemExists){
                        datasource.updateItemSoundPref(reminderItem.getEventReminderId(), false);
                    }

                    reminderItem.setSoundPref(false);
                }
            }
        });

        reminderSnoozeOptionsBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                //RadioGroup rg = findViewById(R.id.event_reminder_options_radio_group);
                final AlertDialog.Builder builder = new AlertDialog.Builder(AlarmManagmentActivity.this);

                LayoutInflater inflater = getLayoutInflater();
                //RadioGroup rgf = new RadioGroup(ScheduleEventActivity.this);
                //rgf.addView(inflater.inflate(R.layout.activity_event_reminder_options, (ViewGroup)findViewById(R.id.event_reminder_options_radio_group)));
                final View view = (View)inflater.inflate(R.layout.event_reminders_snooze_options,null);
                //RadioGroup grp = (RadioGroup)view.findViewById(R.id.event_reminder_options_radio_group) ;
                final RadioGroup snoozeOptionsRg = view.findViewById(R.id.event_reminder_snooze_options_radio_group);
                snoozeOptionsRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId)
                    {
                        Log.d("ITEM_CHECKED_radio_SNOOZE", String.valueOf(checkedId));
                        reminderSnoozeOptionRb =(RadioButton)view.findViewById(checkedId);


                    }
                });

                builder.setView(view);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Log.d("ITEM_CHECKED_builder_snooze", String.valueOf(which));
                        Log.d("IN SNOOZE", "SNOOZE");
                        int checkedId = snoozeOptionsRg.getCheckedRadioButtonId();

                        if(checkedId!=-1)
                        {

                            switch (reminderSnoozeOptionRb.getId())
                            {
                                case R.id.event_reminder_snooze_options_none:
                                    if(reminderSnoozeOptionRb.isChecked())
                                    {
                                        selectedSnoozeOptionText = reminderSnoozeOptionRb.getText().toString();
                                        reminderSnoozeTimeTv.setText(selectedSnoozeOptionText);
                                        selectedSnoozeOptionLong = 0;
                                        if(itemExists){
                                            datasource.updateItemSnoozePref(reminderItem.getEventReminderId(),selectedSnoozeOptionLong);
                                        }
                                        reminderItem.setSnoozePref(selectedSnoozeOptionLong);

                                    }
                                    break;

                                case R.id.event_reminder_snooze_options_1minute_radio_btn:
                                    if(reminderSnoozeOptionRb.isChecked())
                                    {
                                        selectedSnoozeOptionText = reminderSnoozeOptionRb.getText().toString();
                                        reminderSnoozeTimeTv.setText(selectedSnoozeOptionText);
                                        selectedSnoozeOptionLong = 60 * 1000;
                                        if(itemExists){
                                            datasource.updateItemSnoozePref(reminderItem.getEventReminderId(),selectedSnoozeOptionLong);
                                        }
                                        reminderItem.setSnoozePref(selectedSnoozeOptionLong);

                                    }
                                    break;

                                case R.id.event_reminder_snooze_options_5minutes_radio_btn:
                                    if(reminderSnoozeOptionRb.isChecked())
                                    {
                                        selectedSnoozeOptionText = reminderSnoozeOptionRb.getText().toString();
                                        reminderSnoozeTimeTv.setText(selectedSnoozeOptionText);
                                        selectedSnoozeOptionLong = 5 * 60 * 1000;
                                        if(itemExists){
                                            datasource.updateItemSnoozePref(reminderItem.getEventReminderId(),selectedSnoozeOptionLong);
                                        }
                                        reminderItem.setSnoozePref(selectedSnoozeOptionLong);
                                    }
                                    break;

                                case R.id.event_reminder_snooze_options_10minutes_radio_btn:
                                    if(reminderSnoozeOptionRb.isChecked())
                                    {
                                        selectedSnoozeOptionText = reminderSnoozeOptionRb.getText().toString();
                                        reminderSnoozeTimeTv.setText(selectedSnoozeOptionText);
                                        selectedSnoozeOptionLong = 10 * 60 *1000;
                                        if(itemExists){
                                            datasource.updateItemSnoozePref(reminderItem.getEventReminderId(),selectedSnoozeOptionLong);
                                        }
                                        reminderItem.setSnoozePref(selectedSnoozeOptionLong);
                                    }
                                    break;
                                case R.id.event_reminder_snooze_options_20minutes_radio_btn:
                                    if(reminderSnoozeOptionRb.isChecked())
                                    {
                                        selectedSnoozeOptionText = reminderSnoozeOptionRb.getText().toString();
                                        reminderSnoozeTimeTv.setText(selectedSnoozeOptionText);
                                        selectedSnoozeOptionLong = 20 * 60 *1000;
                                        if(itemExists){
                                            datasource.updateItemSnoozePref(reminderItem.getEventReminderId(),selectedSnoozeOptionLong);
                                        }
                                        reminderItem.setSnoozePref(selectedSnoozeOptionLong);
                                    }
                                    break;
                                case R.id.event_reminder_snooze_options_30minutes_radio_btn:
                                    if(reminderSnoozeOptionRb.isChecked())
                                    {
                                        selectedSnoozeOptionText = reminderSnoozeOptionRb.getText().toString();
                                        reminderSnoozeTimeTv.setText(selectedSnoozeOptionText);
                                        selectedSnoozeOptionLong = 30 * 60 * 1000;
                                        if(itemExists){
                                            datasource.updateItemSnoozePref(reminderItem.getEventReminderId(),selectedSnoozeOptionLong);
                                        }
                                        reminderItem.setSnoozePref(selectedSnoozeOptionLong);

                                    }
                                    break;
                                case R.id.event_reminder_snooze_options_1hour_radio_btn:
                                    if(reminderSnoozeOptionRb.isChecked())
                                    {
                                        selectedSnoozeOptionText = reminderSnoozeOptionRb.getText().toString();
                                        reminderSnoozeTimeTv.setText(selectedSnoozeOptionText);
                                        selectedSnoozeOptionLong = 60 * 60 * 1000;
                                        if(itemExists){
                                            datasource.updateItemSnoozePref(reminderItem.getEventReminderId(),selectedSnoozeOptionLong);
                                        }
                                        reminderItem.setSnoozePref(selectedSnoozeOptionLong);

                                    }
                                    break;



                            }

                        }

                    }
                });

                builder.show();

            }

        });

        if(pref.isHavvaMute()){
            mutePreferenceSwitch.setChecked(true);
        }else{
            mutePreferenceSwitch.setChecked(false);
        }

        mutePreferenceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    pref.muteHavva(true);
                }else {
                    pref.muteHavva(false);
                }
            }
        });


    }
}
