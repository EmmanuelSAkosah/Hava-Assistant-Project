package edu.dartmouth.cs.havvapa.database_elements;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

import edu.dartmouth.cs.havvapa.models.ToDoEntry;
import edu.dartmouth.cs.havvapa.models.ToDoItemForAdapter;

public class ToDoItemsSource
{
    private static final String TAG = "ManualActivity Source";

    private MySQLiteHelper dbHelper;
    private SQLiteDatabase database;

    private String[] allColumns = {MySQLiteHelper.ROW_ID,MySQLiteHelper.COLUMN_EVENT_TITLE, MySQLiteHelper.COLUMN_EVENT_DESCRIPTION, MySQLiteHelper.COLUMN_DATE_TIME};

    public ToDoItemsSource(Context context){
        dbHelper = new MySQLiteHelper(context);
    }

    public ToDoEntry createItem(ToDoEntry entry)
    {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(MySQLiteHelper.COLUMN_EVENT_TITLE, entry.getEventTitle());
        values.put(MySQLiteHelper.COLUMN_EVENT_DESCRIPTION, entry.getEventDescription());
        values.put(MySQLiteHelper.COLUMN_DATE_TIME, entry.getDateTime().getTimeInMillis());

        long insertId = database.insert(MySQLiteHelper.TABLE_ITEMS, null, values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_ITEMS, allColumns, MySQLiteHelper.ROW_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        entry.setId(cursor.getLong(0));

        cursor.close();
        database.close();
        dbHelper.close();
        return entry;
    }

    public void modifyScheduledEvent(long rowId, String modifiedTitle, String modifiedDescription, long modifiedDateTime)
    {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String strFilter = MySQLiteHelper.ROW_ID + " = " + rowId;
        ContentValues args = new ContentValues();

        args.put(MySQLiteHelper.COLUMN_EVENT_TITLE, modifiedTitle);
        args.put(MySQLiteHelper.COLUMN_EVENT_DESCRIPTION, modifiedDescription);
        args.put(MySQLiteHelper.COLUMN_DATE_TIME, modifiedDateTime);

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
        Calendar entryDateAndTime = Calendar.getInstance();
        entryDateAndTime.setTimeInMillis(cursor.getLong(3));

        entry.setId(cursor.getLong(0));
        entry.setEventTitle(cursor.getString(1));
        entry.setEventDescription(cursor.getString(2));
        entry.setDateTime(entryDateAndTime);

        return entry;
    }
}
