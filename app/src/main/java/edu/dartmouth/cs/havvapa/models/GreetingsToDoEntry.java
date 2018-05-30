package edu.dartmouth.cs.havvapa.models;

public class GreetingsToDoEntry
{
    String greetingsEventDate;
    String greetingsEventTitleAndLocation;
    String greetingsEventStartAndEndTime;

    public GreetingsToDoEntry(String greetingsEventDate, String greetingsEventTitleAndLocation, String greetingsEventStartAndEndTime){
        this.greetingsEventDate = greetingsEventDate;
        this.greetingsEventTitleAndLocation = greetingsEventTitleAndLocation;
        this.greetingsEventStartAndEndTime = greetingsEventStartAndEndTime;
    }

    public String getGreetingsEventDate() {
        return greetingsEventDate;
    }

    public void setGreetingsEventDate(String greetingsEventDate) {
        this.greetingsEventDate = greetingsEventDate;
    }

    public String getGreetingsEventTitleAndLocation() {
        return greetingsEventTitleAndLocation;
    }

    public void setGreetingsEventTitleAndLocation(String greetingsEventTitleAndLocation) {
        this.greetingsEventTitleAndLocation = greetingsEventTitleAndLocation;
    }

    public String getGreetingsEventStartAndEndTime() {
        return greetingsEventStartAndEndTime;
    }

    public void setGreetingsEventStartAndEndTime(String greetingsEventStartAndEndTime) {
        this.greetingsEventStartAndEndTime = greetingsEventStartAndEndTime;
    }
}
