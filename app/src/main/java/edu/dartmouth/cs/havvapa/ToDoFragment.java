package edu.dartmouth.cs.havvapa;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;

import edu.dartmouth.cs.havvapa.adapters.ToDoListAdapter;
import edu.dartmouth.cs.havvapa.database_elements.ToDoEntryListLoader;
import edu.dartmouth.cs.havvapa.database_elements.ToDoItemsSource;
import edu.dartmouth.cs.havvapa.models.ToDoEntry;
import edu.dartmouth.cs.havvapa.models.ToDoItemForAdapter;


public class ToDoFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<ToDoEntry>>
{
    private ToDoItemsSource datasource;
    private ToDoListAdapter mToDoListAdapter;
    private ArrayList<ToDoEntry> allEntries;
    ArrayList<ToDoItemForAdapter> updatedToDoItemEnries = new ArrayList<>();
    private ToDoItemForAdapter item;

    private static  final int ALL_ITEMS_LOADER_ID = 1;


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.menuitem_schedule_event_btn:
                startActivity(new Intent(getActivity(), ScheduleEventActivity.class));
                return true;

            case R.id.menuitem_settings:
                startActivity(new Intent(getActivity(), SignUpActivity.class));
                return true;

            case R.id.menuitem_editProfile:
                startActivity(new Intent(getActivity(), SignUpActivity.class));
                return true;

            default:
               // return super.onOptionsItemSelected(item);
                return true;

        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.to_do_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        datasource = new ToDoItemsSource(getContext());



        setRetainInstance(true);
        updatedToDoItemEnries = new ArrayList<>();





    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_to_do, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*
        try {
            Intent intent = getActivity().getIntent();
            String title = intent.getExtras().getString("event_title");
            String description = intent.getExtras().getString("event_description");
            String date = intent.getExtras().getString("event_date");
            String time = intent.getExtras().getString("event_time");
            item = new ToDoItemForAdapter(title,description,date + " " + time);
            updatedToDoItemEnries.add(item);
        }
        catch (Exception e){

        }

        ListView mListView = view.findViewById(R.id.calendarListView);
        mToDoListAdapter = new ToDoListAdapter(getActivity(), updatedToDoItemEnries);
        //mToDoListAdapter.setCalendarItems(updatedToDoItemEnries);
        mListView.setAdapter(mToDoListAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(mToDoListAdapter!=null && mToDoListAdapter.getCount()!=0)
                {

                }
            }
        });*/

        ListView mListView = view.findViewById(R.id.calendarListView);
        mToDoListAdapter = new ToDoListAdapter(getContext(), updatedToDoItemEnries);
        mListView.setAdapter(mToDoListAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(mToDoListAdapter!=null && mToDoListAdapter.getCount()!=0)
                {
                    long selectedEntryId = allEntries.get(position).getId();
                    Intent displayIntent = new Intent(getActivity(), ScheduleEventActivity.class);
                    displayIntent.putExtra("ENTRY_ROW_ID", selectedEntryId);
                    startActivity(displayIntent);

                }
            }
        });
    }

    private String updateDateDisplay(Calendar dateTime)
    {
        String mSelectedDate = DateUtils.formatDateTime(getActivity(), dateTime.getTimeInMillis(),DateUtils.FORMAT_SHOW_DATE);
        return mSelectedDate;
    }
    private String updateTimeDisplay(Calendar dateTime)
    {
        String mSelectedTime = DateUtils.formatDateTime(getActivity(), dateTime.getTimeInMillis(),DateUtils.FORMAT_SHOW_TIME);
        return mSelectedTime;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getSupportLoaderManager().initLoader(ALL_ITEMS_LOADER_ID, null, this).forceLoad();
    }

    @NonNull
    @Override
    public Loader<ArrayList<ToDoEntry>> onCreateLoader(int id, @Nullable Bundle args) {

        switch (id){
            case ALL_ITEMS_LOADER_ID:
                return new ToDoEntryListLoader(getActivity());
        }

        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<ToDoEntry>> loader, ArrayList<ToDoEntry> entities) {
        if(loader.getId() == ALL_ITEMS_LOADER_ID)
        {
            if(entities.size()>0){
                allEntries = entities;
                ArrayList<ToDoItemForAdapter> toDoEntriesPerScheduledEvent = new ArrayList<>();
                for(ToDoEntry toDoEntry : entities)
                {
                    Calendar cal = toDoEntry.getDateTime();
                    ToDoItemForAdapter toDoEntryOfScheduledEvent = toDoEntry.getToDoItemOfAdapter();
                    toDoEntryOfScheduledEvent.setToDoItemTime(updateDateDisplay(cal) + " " + updateTimeDisplay(cal));
                    toDoEntriesPerScheduledEvent.add(toDoEntryOfScheduledEvent);

                }

                updatedToDoItemEnries = toDoEntriesPerScheduledEvent;
                mToDoListAdapter.setCalendarItems(updatedToDoItemEnries);
                mToDoListAdapter.notifyDataSetChanged();
            }
            else {
                mToDoListAdapter.clear();
                mToDoListAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<ToDoEntry>> loader) {
        if(loader.getId()==ALL_ITEMS_LOADER_ID){
            mToDoListAdapter.clear();
            mToDoListAdapter.notifyDataSetChanged();
        }
    }
}
