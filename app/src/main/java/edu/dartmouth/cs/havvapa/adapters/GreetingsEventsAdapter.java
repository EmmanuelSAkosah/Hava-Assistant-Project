package edu.dartmouth.cs.havvapa.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import edu.dartmouth.cs.havvapa.R;
import edu.dartmouth.cs.havvapa.models.GreetingsToDoEntry;
import edu.dartmouth.cs.havvapa.models.ToDoItemForAdapter;

public class GreetingsEventsAdapter extends BaseAdapter {

    private ArrayList<GreetingsToDoEntry> greetingsEntriesList;
    private Context mContext;

    public GreetingsEventsAdapter(Context context, ArrayList<GreetingsToDoEntry> greetingsEntriesList){
        this.mContext = context;
        this.greetingsEntriesList = greetingsEntriesList;
    }

    public GreetingsEventsAdapter(Context context) {
        this.mContext = context;
    }

    public ArrayList<GreetingsToDoEntry> getGreetingsEntriesList() {
        return greetingsEntriesList;
    }

    public void setGreetingsEntriesList(ArrayList<GreetingsToDoEntry> greetingsEntriesList) {
        this.greetingsEntriesList = greetingsEntriesList;
    }

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    public int getCount() {
        return greetingsEntriesList.size();
    }

    @Override
    public boolean isEmpty() {
        return greetingsEntriesList.size() == 0;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public GreetingsToDoEntry getItem(int position) {
        return greetingsEntriesList.get(position);
    }

    public void clear() {
        this.getGreetingsEntriesList().clear();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View greetingsEventEntryView = convertView;
        if (greetingsEventEntryView == null)
            greetingsEventEntryView = LayoutInflater.from(mContext).inflate(R.layout.activity_greetings_to_do_entry, parent, false).findViewById(R.id.greetings_event_entry);


        //FrameLayout greetingsEventEntryView = (FrameLayout)toDoItemFrameView.findViewById(R.id.event_items);
        GreetingsToDoEntry curr = getItem(position);
        String greetingsEventDate = curr.getGreetingsEventDate();
        String greetingsEventTitleAndLocation = curr.getGreetingsEventTitleAndLocation();
        String greetingsEventStartAndEndTime = curr.getGreetingsEventStartAndEndTime();

        TextView greetingsEventDateTv = greetingsEventEntryView.findViewById(R.id.greetings_event_date_tv);
        TextView greetingsEventTitleAndLocTv = greetingsEventEntryView.findViewById(R.id.greetings_event_title_and_location_tv);
        TextView greetingsEventStartAndEndTimeTv = greetingsEventEntryView.findViewById(R.id.greetings_event_start_end_time);

        if(greetingsEventDate.equals("")){
            greetingsEventDateTv.setTextSize(2);
            greetingsEventDateTv.setText(greetingsEventDate);
        }

        else {
            greetingsEventDateTv.setTextSize(15);
            greetingsEventDateTv.setText(greetingsEventDate);
        }
        greetingsEventTitleAndLocTv.setText(greetingsEventTitleAndLocation);
        greetingsEventStartAndEndTimeTv.setText(greetingsEventStartAndEndTime);

        return greetingsEventEntryView;

    }

}

