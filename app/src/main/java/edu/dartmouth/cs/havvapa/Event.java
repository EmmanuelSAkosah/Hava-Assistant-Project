package edu.dartmouth.cs.havvapa;

import android.support.annotation.Nullable;

import edu.dartmouth.cs.havvapa.models.ToDoEntry;

public class Event {

    private int color;
    private long timeInMillis;
    private ToDoEntry entry;

    public Event(int color, long timeInMillis) {
        this.color = color;
        this.timeInMillis = timeInMillis;
    }

    public Event(int color, long timeInMillis, ToDoEntry entry) {
        this.color = color;
        this.timeInMillis = timeInMillis;
        this.entry = entry;
    }

    public int getColor() {
        return color;
    }

    public long getTimeInMillis() {
        return timeInMillis;
    }

    @Nullable
    public ToDoEntry getEntry() {
        return entry;
    }

    public void setEntry(ToDoEntry entry){
        this.entry = entry;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;

        if (color != event.color) return false;
        if (timeInMillis != event.timeInMillis) return false;
        if (entry != null ? !entry.equals(event.entry) : event.entry != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = color;
        result = 31 * result + (int) (timeInMillis ^ (timeInMillis >>> 32));
        result = 31 * result + (entry != null ? entry.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Event{" +
                "color=" + color +
                ", timeInMillis=" + timeInMillis +
                ", data=" + entry +
                '}';
    }
}
