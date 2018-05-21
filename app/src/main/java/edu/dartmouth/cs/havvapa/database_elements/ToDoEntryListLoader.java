package edu.dartmouth.cs.havvapa.database_elements;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import org.json.JSONException;

import java.util.ArrayList;

import edu.dartmouth.cs.havvapa.models.ToDoEntry;

public class ToDoEntryListLoader extends AsyncTaskLoader<ArrayList<ToDoEntry>> {

    private ToDoItemsSource datasource;

    public ToDoEntryListLoader(@NonNull Context context)
    {
        super(context);
        this.datasource = new ToDoItemsSource(context);
    }

    @Nullable
    @Override
    public ArrayList<ToDoEntry> loadInBackground()
    {
        try{
            return datasource.getAllEntries();
        }
        catch (Exception e){

            return null;
        }
    }

}
