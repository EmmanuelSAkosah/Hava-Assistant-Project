package edu.dartmouth.cs.havvapa.models;

public class ToDoItemForAdapter {

    String toDoItemTitle;
    String toDoItemDescription;
    String toDoItemTime;

    public ToDoItemForAdapter(String toDoItemTitle, String toDoItemDescription, String toDoItemTime){
        this.toDoItemTitle = toDoItemTitle;
        this.toDoItemDescription = toDoItemDescription;
        this.toDoItemTime = toDoItemTime;
    }

    public String getToDoItemTitle() {
        return toDoItemTitle;
    }

    public void setToDoItemTitle(String toDoItemTitle) {
        this.toDoItemTitle = toDoItemTitle;
    }

    public String getToDoItemDescription() {
        return toDoItemDescription;
    }

    public void setToDoItemDescription(String toDoItemDescription) {
        this.toDoItemDescription = toDoItemDescription;
    }

    public String getToDoItemTime() {
        return toDoItemTime;
    }

    public void setToDoItemTime(String toDoItemTime) {
        this.toDoItemTime = toDoItemTime;
    }
}
