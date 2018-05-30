package edu.dartmouth.cs.havvapa.database_elements;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteHelperReminder extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "event_reminders.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_ITEMS = "reminderItems";
    public static final String ROW_ID = "_id";
    public static final String COLUMN_SNOOZE_PREFERENCE = "snoozePreference";
    public static final String COLUMN_VIBRATION_PREFERENCE = "vibrationPreference";
    public static final String COLUMN_SOUND_PREFERENCE = "soundPreference";

    private static final String DATABASE_TODO_ITEMS = "CREATE TABLE IF NOT EXISTS " +
            TABLE_ITEMS + "(" + ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_SNOOZE_PREFERENCE + " LONG NOT NULL, " +
            COLUMN_VIBRATION_PREFERENCE + " INTEGER DEFAULT 0, " +
            COLUMN_SOUND_PREFERENCE + " INTEGER DEFAULT 0);";


    MySQLiteHelperReminder(Context context){

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
