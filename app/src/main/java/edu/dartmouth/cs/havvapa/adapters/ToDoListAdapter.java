package edu.dartmouth.cs.havvapa.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import edu.dartmouth.cs.havvapa.R;
import edu.dartmouth.cs.havvapa.models.ToDoItem;

public class ToDoListAdapter extends BaseAdapter {

    private ArrayList<ToDoItem> calendarItems;
    private Context mContext;

    public ToDoListAdapter(Context context, ArrayList<ToDoItem> calendarItems){
        this.mContext = context;
        this.calendarItems = calendarItems;
    }

    public ToDoListAdapter(Context context) {
        this.mContext = context;
    }

    public ArrayList<ToDoItem> getCalendarItems() {
        return calendarItems;
    }

    public void setCalendarItems(ArrayList<ToDoItem> calendarItems) {
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
    public ToDoItem getItem(int position) {
        return calendarItems.get(position);
    }

    public void clear() {
        this.getCalendarItems().clear();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View toDoItemView = convertView;
        if (toDoItemView == null)
            toDoItemView = LayoutInflater.from(mContext).inflate(R.layout.to_do_item, parent, false);

        ToDoItem curr = getItem(position);
        String toDoItemTitle = curr.getToDoItemTitle();
        String toDoItemDescription = curr.getToDoItemDescription();
        String toDoItemTime = curr.getToDoItemTime();

        TextView toDoItemTitleTv = toDoItemView.findViewById(R.id.tv_todo_item_title);
        TextView toDoItemDescriptionTv = toDoItemView.findViewById(R.id.tv_todo_item_description);
        TextView toDoItemTimeTv = toDoItemView.findViewById(R.id.tv_todo_item_time);

        toDoItemTitleTv.setText(toDoItemTitle);
        toDoItemDescriptionTv.setText(toDoItemDescription);
        toDoItemTimeTv.setText(toDoItemTime);

        return toDoItemView;

    }

}
