package edu.dartmouth.cs.havvapa.database_elements;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteHelperNews extends SQLiteOpenHelper
{

    private static final String DATABASE_NAME = "savedNews.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_ITEMS = "items";
    public static final String ROW_ID = "_id";
    public static final String COLUMN_NEWS_TITLE = "news_title";
    public static final String COLUMN_NEWS_URL = "news_url";
    public static final String COLUMN_NEWS_SOURCE = "news_source";
    public static final String COLUMN_NEWS_IMAGE_URL = "news_image_url";



    private static final String DATABASE_TODO_ITEMS = "CREATE TABLE IF NOT EXISTS " +
            TABLE_ITEMS + "(" + ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_NEWS_TITLE + " TEXT NOT NULL, " +
            COLUMN_NEWS_URL + " TEXT NOT NULL, " +
            COLUMN_NEWS_SOURCE + " TEXT NOT NULL, " +
            COLUMN_NEWS_IMAGE_URL + " TEXT NOT NULL);";


    MySQLiteHelperNews(Context context){

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
