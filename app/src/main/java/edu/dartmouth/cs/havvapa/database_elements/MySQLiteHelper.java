package edu.dartmouth.cs.havvapa.database_elements;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteHelper extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "manuel_items.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_ITEMS = "items";
    public static final String ROW_ID = "_id";
    public static final String COLUMN_EVENT_TITLE = "eventTitle";
    public static final String COLUMN_EVENT_LOCATION = "eventLocation";
    public static final String COLUMN_EVENT_DESCRIPTION = "event_description";
    public static final String COLUMN_START_DATE_TIME = "startDateTime";
    public static final String COLUMN_END_DATE_TIME = "endDateTime";
    public static final String COLUMN_R = "opt";
    public static final String COLUMN_UNIQUE_TIMESTAMP = "itemId";
    public static final String COLUMN_UNIQUE_TIMESTAMPP = "iteId";


    private static final String DATABASE_TODO_ITEMS = "CREATE TABLE IF NOT EXISTS " +
            TABLE_ITEMS + "(" + ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_EVENT_TITLE + " TEXT NOT NULL, " +
            COLUMN_EVENT_LOCATION + " TEXT NOT NULL, " +
            COLUMN_EVENT_DESCRIPTION + " TEXT NOT NULL, " +
            COLUMN_START_DATE_TIME + " DATETIME NOT NULL, " +
            COLUMN_END_DATE_TIME + " DATETIME NOT NULL, " +
            COLUMN_R + " TEXT, " +
            COLUMN_UNIQUE_TIMESTAMP + " INTEGER NOT NULL, " +
            COLUMN_UNIQUE_TIMESTAMPP + " INTEGER NOT NULL);";


    MySQLiteHelper(Context context){

        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_TODO_ITEMS);



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(TABLE_ITEMS);
        onCreate(db);
    }
}
