package edu.dartmouth.cs.havvapa.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;

import edu.dartmouth.cs.havvapa.R;
import edu.dartmouth.cs.havvapa.models.ToDoItemForAdapter;

public class ToDoListAdapter extends BaseAdapter {

    private ArrayList<ToDoItemForAdapter> calendarItems;
    private Context mContext;

    public ToDoListAdapter(Context context, ArrayList<ToDoItemForAdapter> calendarItems){
        this.mContext = context;
        this.calendarItems = calendarItems;
    }

    public ToDoListAdapter(Context context) {
        this.mContext = context;
    }

    public ArrayList<ToDoItemForAdapter> getCalendarItems() {
        return calendarItems;
    }

    public void setCalendarItems(ArrayList<ToDoItemForAdapter> calendarItems) {
        this.calendarItems = calendarItems;
    }

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    public int getCount() {
        return calendarItems.size();
    }

    @Override
    public boolean isEmpty() {
        return calendarItems.size() == 0;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public ToDoItemForAdapter getItem(int position) {
        return calendarItems.get(position);
    }

    public void clear() {
        this.getCalendarItems().clear();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View toDoItemView = convertView;
        if (toDoItemView == null)
            toDoItemView = LayoutInflater.from(mContext).inflate(R.layout.to_do_item_frame_layout, parent, false).findViewById(R.id.event_entry);


        //FrameLayout toDoItemView = (FrameLayout)toDoItemFrameView.findViewById(R.id.event_items);
        ToDoItemForAdapter curr = getItem(position);
        String toDoItemTitleAndLocation = curr.getToDoItemTitleAndLocation();
        String toDoItemDescription = curr.getToDoItemDescription();
        String toDoItemStartTime = curr.getToDoItemStartTime();
        String toDoItemEndTime = curr.getToDoItemEndTime();

        TextView toDoItemTitleAndLocationTv = toDoItemView.findViewById(R.id.to_do_event_title_location);
        TextView toDoItemDescriptionTv = toDoItemView.findViewById(R.id.to_do_event_description);
        TextView toDoItemStartTimeTv = toDoItemView.findViewById(R.id.to_do_event_start_time);
        TextView toDoItemEndTimeTv = toDoItemView.findViewById(R.id.to_do_event_end_time);

        toDoItemTitleAndLocationTv.setText(toDoItemTitleAndLocation);
        toDoItemDescriptionTv.setText(toDoItemDescription);
        toDoItemStartTimeTv.setText(toDoItemStartTime);
        toDoItemEndTimeTv.setText(toDoItemEndTime);

        return toDoItemView;

    }

}
