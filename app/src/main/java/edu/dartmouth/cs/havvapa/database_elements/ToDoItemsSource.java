package edu.dartmouth.cs.havvapa.database_elements;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

import edu.dartmouth.cs.havvapa.models.ToDoEntry;

public class ToDoItemsSource
{
    private static final String TAG = "ManualActivity Source";

    private MySQLiteHelper dbHelper;
    private SQLiteDatabase database;
    private Calendar todaysCalendar = Calendar.getInstance();

    private String[] allColumns = {MySQLiteHelper.ROW_ID,MySQLiteHelper.COLUMN_EVENT_TITLE, MySQLiteHelper.COLUMN_EVENT_LOCATION, MySQLiteHelper.COLUMN_EVENT_DESCRIPTION, MySQLiteHelper.COLUMN_START_DATE_TIME
    , MySQLiteHelper.COLUMN_END_DATE_TIME, MySQLiteHelper.COLUMN_EVENT_DURATION};

    public ToDoItemsSource(Context context){
        dbHelper = new MySQLiteHelper(context);
    }

    public ToDoEntry createItem(ToDoEntry entry)
    {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(MySQLiteHelper.COLUMN_EVENT_TITLE, entry.getEventTitle());
        values.put(MySQLiteHelper.COLUMN_EVENT_LOCATION, entry.getEventLocation());
        values.put(MySQLiteHelper.COLUMN_EVENT_DESCRIPTION, entry.getEventDescription());
        values.put(MySQLiteHelper.COLUMN_START_DATE_TIME, entry.getStartDateTime().getTimeInMillis());
        values.put(MySQLiteHelper.COLUMN_END_DATE_TIME, entry.getEndDateTime().getTimeInMillis());
        values.put(MySQLiteHelper.COLUMN_EVENT_DURATION, entry.getEventDuration());

        long insertId = database.insert(MySQLiteHelper.TABLE_ITEMS, null, values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_ITEMS, allColumns, MySQLiteHelper.ROW_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        entry.setId(cursor.getLong(0));

        cursor.close();
        database.close();
        dbHelper.close();
        return entry;
    }

    public void modifyScheduledEvent(long rowId, String modifiedTitle,String modifiedLocation, String modifiedDescription, long modifiedStartDateTime, long modifiedEndDateTime, String modifiedDuration)
    {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String strFilter = MySQLiteHelper.ROW_ID + " = " + rowId;
        ContentValues args = new ContentValues();

        args.put(MySQLiteHelper.COLUMN_EVENT_TITLE, modifiedTitle);
        args.put(MySQLiteHelper.COLUMN_EVENT_LOCATION, modifiedLocation);
        args.put(MySQLiteHelper.COLUMN_EVENT_DESCRIPTION, modifiedDescription);
        args.put(MySQLiteHelper.COLUMN_START_DATE_TIME, modifiedStartDateTime);
        args.put(MySQLiteHelper.COLUMN_END_DATE_TIME, modifiedEndDateTime);
        args.put(MySQLiteHelper.COLUMN_EVENT_DURATION, modifiedDuration);

        database.update(MySQLiteHelper.TABLE_ITEMS, args, strFilter, null);

        database.close();
        dbHelper.close();
    }

    public void deleteItem(ToDoEntry entry)
    {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Log.d(TAG, "entry with " + entry.getId() + " deleted!");
        long entryId = entry.getId();
        database.delete(MySQLiteHelper.TABLE_ITEMS, MySQLiteHelper.ROW_ID + " = " + entryId, null );

        database.close();
        dbHelper.close();
    }

    public void deleteAllItems(){
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Log.d(TAG, "all items are deleted!");

        try {
            database.delete(MySQLiteHelper.TABLE_ITEMS, null, null);
        }
        catch (Exception e){

            Log.d("deleteAllItems()", "DATABASE_EMPTY");
        }

        database.close();
        dbHelper.close();
    }

    public ArrayList<ToDoEntry> getAllEntries()
    {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        ArrayList<ToDoEntry> allEntries = new ArrayList<>();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_ITEMS, allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            ToDoEntry item = cursorToEntry(cursor);
            allEntries.add(item);
            cursor.moveToNext();
        }

        cursor.close();
        database.close();
        dbHelper.close();
        return allEntries;
    }

    public ArrayList<ToDoEntry> getAllUpcomingEntries()
    {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        ArrayList<ToDoEntry> allUpcomingEntries = new ArrayList<>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_ITEMS, allColumns, null, null, null, null, MySQLiteHelper.COLUMN_START_DATE_TIME + " ASC ");

        cursor.moveToFirst();

        while (!cursor.isAfterLast())
        {
            ToDoEntry item = cursorToEntry(cursor);
            Calendar cal = item.getStartDateTime();


            if(cal.getTimeInMillis() >= todaysCalendar.getTimeInMillis() && (cal.get(Calendar.YEAR) == todaysCalendar.get(Calendar.YEAR)))
            {
                allUpcomingEntries.add(item);
            }

            cursor.moveToNext();
        }

        cursor.close();
        database.close();
        dbHelper.close();

        if (allUpcomingEntries.size() <= 5){
            return allUpcomingEntries;
        }
        else {
            ArrayList<ToDoEntry> fetchedUpcomingEvents = new ArrayList<>();
            for(int i = 0; i < 5; i++){
                fetchedUpcomingEvents.add(allUpcomingEntries.get(i));
            }
            return fetchedUpcomingEvents;
        }
    }

    public ToDoEntry fetchEntryByIndex(long rowId)
    {
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_ITEMS, allColumns, MySQLiteHelper.ROW_ID + " = " + rowId, null, null, null, null);
        cursor.moveToFirst();

        ToDoEntry entryAtRowId = cursorToEntry(cursor);
        cursor.close();
        database.close();
        dbHelper.close();
        return entryAtRowId;

    }

    private ToDoEntry cursorToEntry(Cursor cursor){

        ToDoEntry entry = new ToDoEntry();
        Calendar entryStartDateAndTime = Calendar.getInstance();
        Calendar entryEndDateAndTime = Calendar.getInstance();
        entryStartDateAndTime.setTimeInMillis(cursor.getLong(4));
        entryEndDateAndTime.setTimeInMillis(cursor.getLong(5));

        entry.setId(cursor.getLong(0));
        entry.setEventTitle(cursor.getString(1));
        entry.setEventLocation(cursor.getString(2));
        entry.setEventDescription(cursor.getString(3));
        entry.setStartDateTime(entryStartDateAndTime);
        entry.setEndDateTime(entryEndDateAndTime);
        entry.setEventDuration(cursor.getString(6));

        return entry;
    }
}
