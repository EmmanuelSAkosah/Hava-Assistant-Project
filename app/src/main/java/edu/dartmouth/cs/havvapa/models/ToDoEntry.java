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
    private String eventDuration;


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
        this.setEventDuration(this.calculateEventDuration(startDateTime, endDateTime));

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

    public String getEventDuration()
    {
        return eventDuration;

    }

    public String calculateEventDuration(Calendar dateTimeStart, Calendar dateTimeEnd)
    {

        String eventDuration = null;
        long secondDifference = ((dateTimeEnd.getTimeInMillis() - dateTimeStart.getTimeInMillis())/1000);
        int hourDifference = (int)(secondDifference/3600);
        int remainderSeconds = (int)(secondDifference % 3600);
        int remainderMinutes = (remainderSeconds/60);
        if(hourDifference < 24)
        {
            if(remainderMinutes<60){
                eventDuration = (String.valueOf(hourDifference) + " hours - " + String.valueOf(remainderMinutes) + " minutes");
            }

        }
        else if(hourDifference == 24){
            if(remainderMinutes<60){
                eventDuration = ("1 day - " + String.valueOf(remainderMinutes) + " minutes");
            }

        }
        else {

            int day = (hourDifference / 24);
            int remainderHours = (hourDifference%24);
            eventDuration = (String.valueOf(day) + " day - " + String.valueOf(remainderHours) + " hours - " + String.valueOf(remainderMinutes) + " minutes");
        }



        return eventDuration;
    }

    public void setEventDuration(String eventDuration) {
        this.eventDuration = eventDuration;
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
