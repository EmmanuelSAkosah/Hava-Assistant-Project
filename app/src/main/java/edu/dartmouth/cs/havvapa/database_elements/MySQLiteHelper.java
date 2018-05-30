package edu.dartmouth.cs.havvapa.database_elements;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteHelper extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "manuel_items.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_EVENT = "items";
    public static final String ROW_ID = "_id";
    public static final String COLUMN_TITLE = "event_title";
    public static final String COLUMN_LOCATION = "event_location";
    public static final String COLUMN_EVENT_DESCRIPTION = "event_description";
    public static final String COLUMN_START_DATE_TIME = "start_date_time";
    public static final String COLUMN_END_DATE_TIME = "end_date_time";
    //public static final String COLUMN_R = "event_reminder";
    public static final String COLUMN_UNIQUE_TIMESTAMP = "item_id";
    public static final String COLUMN_UNIQUE_TIMESTAMPP = "ite_id";


    private static final String DATABASE_TODO_EVENT = "CREATE TABLE IF NOT EXISTS " +
            TABLE_EVENT + "(" + ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_TITLE + " TEXT NOT NULL, " +
            COLUMN_LOCATION + " TEXT NOT NULL, " +
            COLUMN_EVENT_DESCRIPTION + " TEXT NOT NULL, " +
            COLUMN_START_DATE_TIME + " DATETIME NOT NULL, " +
            COLUMN_END_DATE_TIME + " DATETIME NOT NULL, " +
            COLUMN_UNIQUE_TIMESTAMP + " INTEGER NOT NULL, " +
            COLUMN_UNIQUE_TIMESTAMPP + " INTEGER NOT NULL);";


    MySQLiteHelper(Context context){

        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_TODO_EVENT);



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(TABLE_EVENT);
        onCreate(db);
    }
}
