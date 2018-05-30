package edu.dartmouth.cs.havvapa.models;

public class EventReminderItem {

    private long snoozePref;
    private boolean vibrationPref;
    private boolean soundPref;
    private long eventReminderId;

    public EventReminderItem(){

    }

    public EventReminderItem(long snoozePref, boolean vibrationPref, boolean soundPref){

        this.snoozePref = snoozePref;
        this.vibrationPref = vibrationPref;
        this.soundPref = soundPref;
    }

    public long getSnoozePref() {
        return snoozePref;
    }

    public void setSnoozePref(long snoozePref) {
        this.snoozePref = snoozePref;
    }

    public boolean getVibrationPref() {
        return vibrationPref;
    }

    public void setVibrationPref(boolean vibrationPref) {
        this.vibrationPref = vibrationPref;
    }

    public boolean getSoundPref() {
        return soundPref;
    }

    public void setSoundPref(boolean soundPref) {
        this.soundPref = soundPref;
    }

    public long getEventReminderId() {
        return eventReminderId;
    }

    public void setEventReminderId(long eventReminderId) {
        this.eventReminderId = eventReminderId;
    }
}
