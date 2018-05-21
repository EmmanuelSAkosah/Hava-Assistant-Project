package edu.dartmouth.cs.havvapa.models;

import java.util.Calendar;

public class ToDoEntry
{
    private long id;
    private Calendar dateTime;
    private String eventTitle;
    private String eventDescription;

    public ToDoEntry(){

    }

    public ToDoEntry(String eventTitle, String eventDescription, Calendar dateTime){
        this.eventTitle = eventTitle;
        this.eventDescription = eventDescription;
        this.dateTime = dateTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public Calendar getDateTime() {
        return dateTime;
    }

    public void setDateTime(Calendar dateTime) {
        this.dateTime = dateTime;
    }

    public ToDoItemForAdapter getToDoItemOfAdapter(){
        String eventTitle = this.getEventTitle();
        String eventDescription = this.getEventDescription();
        String dateTime = String.valueOf(this.getDateTime().getTimeInMillis());
        return new ToDoItemForAdapter(eventTitle, eventDescription, dateTime);
    }
}
