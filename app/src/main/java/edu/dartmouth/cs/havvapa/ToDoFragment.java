package edu.dartmouth.cs.havvapa;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import edu.dartmouth.cs.havvapa.models.ToDoItem;


public class ToDoFragment extends Fragment
{
    private ToDoListAdapter mToDoListAdapter;
    private ArrayList<ToDoItem> mToDoList;
    private ToDoItem item;


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
        setRetainInstance(true);
        mToDoList = new ArrayList<>();





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
        try {
            Intent intent = getActivity().getIntent();
            String title = intent.getExtras().getString("event_title");
            String description = intent.getExtras().getString("event_description");
            String date = intent.getExtras().getString("event_date");
            String time = intent.getExtras().getString("event_time");
            item = new ToDoItem(title,description,date + " " + time);
            mToDoList.add(item);
        }
        catch (Exception e){

        }

        ListView mListView = view.findViewById(R.id.calendarListView);
        mToDoListAdapter = new ToDoListAdapter(getActivity(), mToDoList);
        //mToDoListAdapter.setCalendarItems(mToDoList);
        mListView.setAdapter(mToDoListAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(mToDoListAdapter!=null && mToDoListAdapter.getCount()!=0)
                {

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
}
