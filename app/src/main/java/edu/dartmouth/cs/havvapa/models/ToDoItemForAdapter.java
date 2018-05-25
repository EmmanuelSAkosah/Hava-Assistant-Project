package edu.dartmouth.cs.havvapa.models;

public class ToDoItemForAdapter {

    String toDoItemTitleAndLocation;
    String toDoItemDescription;
    String toDoItemStartTime;
    String toDoItemEndTime;

    public ToDoItemForAdapter(String toDoItemTitleAndLocation, String toDoItemDescription, String toDoItemStartTime, String toDoItemEndTime){
        this.toDoItemTitleAndLocation = toDoItemTitleAndLocation;
        this.toDoItemDescription = toDoItemDescription;
        this.toDoItemStartTime = toDoItemStartTime;
        this.toDoItemEndTime = toDoItemEndTime;
    }

    public String getToDoItemTitleAndLocation() {
        return toDoItemTitleAndLocation;
    }

    public void setToDoItemTitleAndLocation(String toDoItemTitleAndLocation) {
        this.toDoItemTitleAndLocation = toDoItemTitleAndLocation;
    }

    public String getToDoItemDescription() {
        return toDoItemDescription;
    }

    public void setToDoItemDescription(String toDoItemDescription) {

        this.toDoItemDescription = toDoItemDescription;
    }

    public String getToDoItemStartTime() {
        return toDoItemStartTime;
    }

    public void setToDoItemStartTime(String toDoItemStartTime) {
        this.toDoItemStartTime = toDoItemStartTime;
    }

    public String getToDoItemEndTime() {
        return toDoItemEndTime;
    }

    public void setToDoItemEndTime(String toDoItemEndTime) {
        this.toDoItemEndTime = toDoItemEndTime;
    }
}
