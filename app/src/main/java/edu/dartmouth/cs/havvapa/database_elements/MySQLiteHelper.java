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
    public static final String COLUMN_EVENT_TITLE = "event_title";
    public static final String COLUMN_EVENT_DESCRIPTION = "event_description";
    public static final String COLUMN_DATE_TIME = "date_time";


    private static final String DATABASE_TODO_ITEMS = "CREATE TABLE IF NOT EXISTS " +
            TABLE_ITEMS + "(" + ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_EVENT_TITLE + " TEXT NOT NULL, " +
            COLUMN_EVENT_DESCRIPTION + " TEXT NOT NULL, " +
            COLUMN_DATE_TIME + " DATETIME NOT NULL);";


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
