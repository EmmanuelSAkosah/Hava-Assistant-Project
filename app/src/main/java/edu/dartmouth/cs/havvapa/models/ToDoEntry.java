package edu.dartmouth.cs.havvapa.models;

import java.util.Calendar;

public class ToDoEntry
{
    private long id;
    private Calendar startDateTime;
    private Calendar endDateTime;
    private String eventTitle;
    private  String eventLocation;
    private String eventDescription;
    private int eventUniqueTimestamp;
    private int eventUniqueTimestamp2;
    //private String eventReminderOption;


    public ToDoEntry(){

    }

    public ToDoEntry(String eventTitle, String eventDescription, Calendar startDateTime){
        this.eventTitle = eventTitle;
        this.eventDescription = eventDescription;
        this.startDateTime = startDateTime;
    }

    public ToDoEntry(String eventTitle, String eventLocation, String eventDescription, Calendar startDateTime, Calendar endDateTime)
    {
        this.eventTitle = eventTitle;
        this.eventLocation = eventLocation;
        this.eventDescription = eventDescription;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;


    }

    public ToDoEntry(String eventTitle, String eventLocation, String eventDescription, Calendar startDateTime, Calendar endDateTime,int eventUniqueTimestamp, int eventUniqueTimestamp2)
    {
        this.eventTitle = eventTitle;
        this.eventLocation = eventLocation;
        this.eventDescription = eventDescription;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        //this.eventReminderOption = eventReminderOption;
        this.eventUniqueTimestamp = eventUniqueTimestamp;
        this.eventUniqueTimestamp2 = eventUniqueTimestamp2;


    }



    public int getEventUniqueTimestamp() {
        return eventUniqueTimestamp;
    }

    public void setEventUniqueTimestamp(int eventUniqueTimestamp) {
        this.eventUniqueTimestamp = eventUniqueTimestamp;
    }

    public int getEventUniqueTimestamp2() {
        return eventUniqueTimestamp2;
    }

    public void setEventUniqueTimestamp2(int eventUniqueTimestamp2) {
        this.eventUniqueTimestamp2 = eventUniqueTimestamp2;
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

    public Calendar getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(Calendar endDateTime) {
        this.endDateTime = endDateTime;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }



    public Calendar getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(Calendar startDateTime) {
        this.startDateTime = startDateTime;
    }

    public ToDoItemForAdapter getToDoItemOfAdapter(){
        String eventTitle = this.getEventTitle();
        String eventLocation = this.getEventLocation();
        String eventDescription = this.getEventDescription();
        String startDateTime = String.valueOf(this.getStartDateTime().getTimeInMillis());
        String endDateTime = String.valueOf(this.getEndDateTime().getTimeInMillis());
        return new ToDoItemForAdapter(eventTitle + "           " + eventLocation, eventDescription, startDateTime, endDateTime);
    }
}
