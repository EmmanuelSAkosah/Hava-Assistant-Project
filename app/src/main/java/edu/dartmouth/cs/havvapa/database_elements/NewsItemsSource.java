package edu.dartmouth.cs.havvapa.database_elements;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import edu.dartmouth.cs.havvapa.models.EventReminderItem;
import edu.dartmouth.cs.havvapa.models.NewsItem;
import edu.dartmouth.cs.havvapa.models.ToDoEntry;

public class NewsItemsSource {

    private static final String TAG = "NewsItems source";

    private MySQLiteHelperNews dbHelper;
    private SQLiteDatabase database;

    private String[] allColumns = {MySQLiteHelperNews.ROW_ID,MySQLiteHelperNews.COLUMN_NEWS_TITLE, MySQLiteHelperNews.COLUMN_NEWS_URL, MySQLiteHelperNews.COLUMN_NEWS_SOURCE
            , MySQLiteHelperNews.COLUMN_NEWS_IMAGE_URL};


    public NewsItemsSource(Context context){
        dbHelper = new MySQLiteHelperNews(context);
    }

    public NewsItem createItem(NewsItem item)
    {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(MySQLiteHelperNews.COLUMN_NEWS_TITLE, item.getTitle());
        values.put(MySQLiteHelperNews.COLUMN_NEWS_URL,item.getURL() );
        values.put(MySQLiteHelperNews.COLUMN_NEWS_SOURCE, item.getSource());
        values.put(MySQLiteHelperNews.COLUMN_NEWS_IMAGE_URL,item.getImageURL() );



        long insertId = database.insert(MySQLiteHelperNews.TABLE_ITEMS, null, values);
        Cursor cursor = database.query(MySQLiteHelperNews.TABLE_ITEMS, allColumns, MySQLiteHelperNews.ROW_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        item.setNewsItemId(cursor.getLong(0));

        cursor.close();
        database.close();
        dbHelper.close();
        return item;
    }

    public void deleteItem(NewsItem item)
    {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        long entryId = item.getNewsItemId();
        database.delete(MySQLiteHelperNews.TABLE_ITEMS, MySQLiteHelperNews.ROW_ID + " = " + entryId, null );

        database.close();
        dbHelper.close();
    }

    private NewsItem cursorToEntry(Cursor cursor){

        NewsItem item = new NewsItem();

        item.setNewsItemId(cursor.getLong(0));
        item.setTitle(cursor.getString(1));
        item.setURL(cursor.getString(2));
        item.setSource(cursor.getString(3));
        item.setImageURL(cursor.getString(4));

        return item;
    }

    public NewsItem fetchEntryByIndex(long rowId)
    {
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        Cursor cursor = database.query(MySQLiteHelperNews.TABLE_ITEMS, allColumns, MySQLiteHelperNews.ROW_ID + " = " + rowId, null, null, null, null);
        cursor.moveToFirst();

        NewsItem newsAtRowId = cursorToEntry(cursor);
        cursor.close();
        database.close();
        dbHelper.close();
        return newsAtRowId;

    }

    public ArrayList<NewsItem> getAllEntries()
    {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        ArrayList<NewsItem> allEntries = new ArrayList<>();
        Cursor cursor = database.query(MySQLiteHelperNews.TABLE_ITEMS, allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            NewsItem item = cursorToEntry(cursor);
            allEntries.add(item);
            cursor.moveToNext();
        }

        cursor.close();
        database.close();
        dbHelper.close();
        return allEntries;
    }



























}
