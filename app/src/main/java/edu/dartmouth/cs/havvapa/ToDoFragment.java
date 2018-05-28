package edu.dartmouth.cs.havvapa;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toolbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
    private android.support.v7.widget.Toolbar toolBarForCurrMonth;
    private CompactCalendarView compactCalendarView;
    private Date lastDateClicked;

    private static  final int ALL_ITEMS_LOADER_ID = 1;


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.menuitem_schedule_event_btn:
                startActivity(new Intent(getActivity(), ScheduleEventActivity.class));
                //Intent intent = new Intent(getActivity(), ScheduleEventActivity.EventOptionsActivity.class);
                //startActivity(intent);
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
        //updatedToDoItemEnries = new ArrayList<>();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_to_do, container, false);
        compactCalendarView = (CompactCalendarView) view.findViewById(R.id.compactcalendar_view);
        compactCalendarView.setUseThreeLetterAbbreviation(false);
        compactCalendarView.setFirstDayOfWeek(Calendar.MONDAY);

        toolBarForCurrMonth = view.findViewById(R.id.tool_bar_for_month_display);

        /*
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH,cal.get(Calendar.DAY_OF_MONTH) + 1);

        List<Event> events = Arrays.asList(
                new Event(Color.argb(255, 169, 68, 65), cal.getTimeInMillis(), "Event at " + new Date(Calendar.getInstance().getTimeInMillis())),
                new Event(Color.argb(255, 100, 68, 65), cal.getTimeInMillis(), "Event 2 at " + new Date(Calendar.getInstance().getTimeInMillis())));

        compactCalendarView.addEvents(events);
        Log.d("TRY", String.valueOf(compactCalendarView.getEvents(cal.getTimeInMillis()).size()));
        for(Event event : compactCalendarView.getEvents(cal.getTimeInMillis())){
            Log.d("DATE", updateDateDisplay2(event.getTimeInMillis()));
        }*/
        compactCalendarView.invalidate();


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ListView mListView = view.findViewById(R.id.calendarListView);
        mToDoListAdapter = new ToDoListAdapter(getContext(),updatedToDoItemEnries);
        mListView.setAdapter(mToDoListAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(mToDoListAdapter!=null )
                {
                    long selectedEntryId = allEntries.get(position).getId();
                    Log.d("ID_?", String.valueOf(selectedEntryId));
                    Intent displayIntent = new Intent(getActivity(), ScheduleEventActivity.class);
                    displayIntent.putExtra("ENTRY_ROW_ID", selectedEntryId);
                    startActivity(displayIntent);

                    //Intent intent = new Intent(getActivity(), ScheduleEventActivity.EventOptionsActivity.class);
                    //startActivity(intent);

                }
            }
        });
        toolBarForCurrMonth = view.findViewById(R.id.tool_bar_for_month_display);
        toolBarForCurrMonth.setTitle(updateDateDisplay(Calendar.getInstance()) + "  -  " + String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));

        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked)
            {

                List<Event> toDoEvents = compactCalendarView.getEvents(dateClicked);
                Calendar currCal = Calendar.getInstance();
                currCal.setTime(dateClicked);
                toolBarForCurrMonth.setTitle(updateDateDisplay(currCal) + "  -  " + currCal.get(Calendar.YEAR));

                allEntries = new ArrayList<>();
                updatedToDoItemEnries = new ArrayList<>();
                if(toDoEvents!=null && mToDoListAdapter!=null )
                {
                    mToDoListAdapter.clear();

                    Log.d("SIZE//", String.valueOf(toDoEvents.size()));

                    for(Event event : toDoEvents)
                    {
                        ToDoEntry toDoEntryOfEvent = event.getEntry();
                        Log.d("NAME//", toDoEntryOfEvent.getEventTitle());
                        allEntries.add(toDoEntryOfEvent);
                        Calendar startCal = toDoEntryOfEvent.getStartDateTime();
                        Calendar endCal = toDoEntryOfEvent.getEndDateTime();

                        ToDoItemForAdapter toDoEntryOfScheduledEvent = toDoEntryOfEvent.getToDoItemOfAdapter();
                        toDoEntryOfScheduledEvent.setToDoItemStartTime(updateTimeDisplay(startCal));
                        toDoEntryOfScheduledEvent.setToDoItemEndTime(updateTimeDisplay(endCal));
                        updatedToDoItemEnries.add(toDoEntryOfScheduledEvent);

                    }
                    mToDoListAdapter.setCalendarItems(updatedToDoItemEnries);
                    mToDoListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth)
            {
                Calendar currCal = Calendar.getInstance();
                currCal.setTime(firstDayOfNewMonth);
                toolBarForCurrMonth.setTitle(updateDateDisplay(currCal) + "  -  " + currCal.get(Calendar.YEAR));
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

    private String updateDateDisplay2(long dateTimeInMillis)
    {
        String mSelectedDate = DateUtils.formatDateTime(getActivity(), dateTimeInMillis,DateUtils.FORMAT_SHOW_DATE);
        return mSelectedDate;
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
            if(entities.size()>0)
            {
                try {
                    compactCalendarView.removeAllEvents();
                }
                catch (Exception e){

                }
                //allEntries = entities;
                List<Event> events = new ArrayList<>();
                ArrayList<ToDoItemForAdapter> toDoEntriesPerScheduledEvent = new ArrayList<>();
                for(ToDoEntry toDoEntry : entities)
                {
                    Calendar cal = toDoEntry.getStartDateTime();
                    events.add(new Event(Color.argb(255, 169, 68, 65), cal.getTimeInMillis(), toDoEntry));


                    /*
                    ToDoItemForAdapter toDoEntryOfScheduledEvent = toDoEntry.getToDoItemOfAdapter();
                    toDoEntryOfScheduledEvent.setToDoItemTime(updateDateDisplay(cal) + " " + updateTimeDisplay(cal));
                    toDoEntriesPerScheduledEvent.add(toDoEntryOfScheduledEvent);*/

                }
                compactCalendarView.addEvents(events);
                compactCalendarView.invalidate();

                //updatedToDoItemEnries = toDoEntriesPerScheduledEvent;
               // mToDoListAdapter.setCalendarItems(updatedToDoItemEnries);
               // mToDoListAdapter.notifyDataSetChanged();
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
