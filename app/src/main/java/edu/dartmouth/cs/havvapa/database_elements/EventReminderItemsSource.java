package edu.dartmouth.cs.havvapa.database_elements;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import edu.dartmouth.cs.havvapa.models.EventReminderItem;
import edu.dartmouth.cs.havvapa.models.ToDoEntry;

public class EventReminderItemsSource
{
    private static final String TAF = "EventReminderItems source";

    private MySQLiteHelperReminder dbHelper;
    private SQLiteDatabase database;

    private String[] allColumns = {MySQLiteHelper.ROW_ID,MySQLiteHelperReminder.COLUMN_SNOOZE_PREFERENCE, MySQLiteHelperReminder.COLUMN_VIBRATION_PREFERENCE
            , MySQLiteHelperReminder.COLUMN_SOUND_PREFERENCE};


    public EventReminderItemsSource(Context context){
        dbHelper = new MySQLiteHelperReminder(context);
    }

    public EventReminderItem createItem(EventReminderItem item){

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        int flagVibrationPref;
        if(item.getVibrationPref()){
            flagVibrationPref = 1;
        }
        else flagVibrationPref = 0;

        int flagSoundPref;
        if(item.getSoundPref()){
            flagSoundPref = 1;
        }
        else flagSoundPref = 0;

        values.put(MySQLiteHelperReminder.COLUMN_SNOOZE_PREFERENCE, item.getSnoozePref());
        values.put(MySQLiteHelperReminder.COLUMN_VIBRATION_PREFERENCE, flagVibrationPref);
        values.put(MySQLiteHelperReminder.COLUMN_SOUND_PREFERENCE, flagSoundPref);

        long insertId = database.insert(MySQLiteHelperReminder.TABLE_ITEMS, null, values);
        Cursor cursor = database.query(MySQLiteHelperReminder.TABLE_ITEMS, allColumns, MySQLiteHelperReminder.ROW_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        item.setEventReminderId(cursor.getLong(0));

        cursor.close();
        database.close();
        dbHelper.close();
        return item;

    }

    public void updateItemVibrationPref(long rowId, boolean vibrationPref)
    {
        int vibrationPrefInt;
        if(vibrationPref) vibrationPrefInt=1;
        else vibrationPrefInt = 0;

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String strFilter = MySQLiteHelperReminder.ROW_ID + " = " + rowId;
        ContentValues args = new ContentValues();
        args.put(MySQLiteHelperReminder.COLUMN_VIBRATION_PREFERENCE, vibrationPrefInt);
        database.update(MySQLiteHelperReminder.TABLE_ITEMS, args, strFilter,null);

        database.close();
        dbHelper.close();
    }

    public void updateItemSoundPref(long rowId, boolean soundPref)
    {
        int soundPrefInt;
        if(soundPref) soundPrefInt=1;
        else soundPrefInt = 0;

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String strFilter = MySQLiteHelperReminder.ROW_ID + " = " + rowId;
        ContentValues args = new ContentValues();
        args.put(MySQLiteHelperReminder.COLUMN_SOUND_PREFERENCE, soundPrefInt);
        database.update(MySQLiteHelperReminder.TABLE_ITEMS, args, strFilter,null);

        database.close();
        dbHelper.close();
    }

    public void updateItemSnoozePref(long rowId, long snoozePref){

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String strFilter = MySQLiteHelperReminder.ROW_ID + " = " + rowId;
        ContentValues args = new ContentValues();
        args.put(MySQLiteHelperReminder.COLUMN_SNOOZE_PREFERENCE, snoozePref);
        database.update(MySQLiteHelperReminder.TABLE_ITEMS,args,strFilter,null);
    }

    private EventReminderItem cursorToItem(Cursor cursor) {

        EventReminderItem item = new EventReminderItem();

        item.setEventReminderId(cursor.getLong(0));
        item.setSnoozePref(cursor.getLong(1));
        item.setVibrationPref(cursor.getInt(2) == 1);
        item.setSoundPref(cursor.getInt(3) == 1);

        return item;
    }

    private EventReminderItem cursorToEntry(Cursor cursor){

        EventReminderItem item = new EventReminderItem();
        item.setEventReminderId(cursor.getLong(0));
        item.setSnoozePref(cursor.getLong(1));
        item.setVibrationPref(cursor.getInt(2) == 1);
        item.setSoundPref(cursor.getInt(3) == 1);

        return item;
    }

    public EventReminderItem getReminderItem()
    {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        EventReminderItem eventReminderItem;

        Cursor cursor = database.query(MySQLiteHelperReminder.TABLE_ITEMS, allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        eventReminderItem = cursorToEntry(cursor);

        cursor.close();
        database.close();
        dbHelper.close();
        return eventReminderItem;

    }



}
