package edu.dartmouth.cs.havvapa.CalendarItems;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class EventsContainer {

    private Calendar eventsCalendar;
    private Map<String, List<Events>> monthAndYearEventsMap = new HashMap<>();

    public EventsContainer(Calendar eventsCalendar) {
        this.eventsCalendar = eventsCalendar;
    }

    public void addEvent(Event event) {
        eventsCalendar.setTimeInMillis(event.getTimeInMillis());
        String key = getKeyForCalendarEvent(eventsCalendar);
        List<Events> eventsForMonth = monthAndYearEventsMap.get(key);
        if (eventsForMonth == null) {
            eventsForMonth = new ArrayList<>();
        }
        Events eventsForTargetDay = getEventDayEvent(event.getTimeInMillis());
        if (eventsForTargetDay == null) {
            List<Event> events = new ArrayList<>();
            events.add(event);
            eventsForMonth.add(new Events(event.getTimeInMillis(), events));
        } else {
            eventsForTargetDay.getEvents().add(event);
        }
        monthAndYearEventsMap.put(key, eventsForMonth);
    }


    public void addEvents(List<Event> events)
    {
        int count = events.size();
        for (int i = 0; i < count; i++) {
            addEvent(events.get(i));
        }
    }

    public void removeAllEvents() {
        monthAndYearEventsMap.clear();
    }



    public List<Events> getEventsForMonthAndYear(int month, int year){
        return monthAndYearEventsMap.get(year + "_" + month);
    }

    public List<Event> getEventsFor(long epochMillis) {
        Events events = getEventDayEvent(epochMillis);
        if (events == null) {
            return new ArrayList<>();
        } else {
            return events.getEvents();
        }
    }


    private Events getEventDayEvent(long eventTimeInMillis){
        eventsCalendar.setTimeInMillis(eventTimeInMillis);
        int dayInMonth = eventsCalendar.get(Calendar.DAY_OF_MONTH);
        String keyForCalendarEvent = getKeyForCalendarEvent(eventsCalendar);
        List<Events> eventsForMonthsAndYear = monthAndYearEventsMap.get(keyForCalendarEvent);
        if (eventsForMonthsAndYear != null) {
            for (Events events : eventsForMonthsAndYear) {
                eventsCalendar.setTimeInMillis(events.getTimeInMillis());
                int dayInMonthFromCache = eventsCalendar.get(Calendar.DAY_OF_MONTH);
                if (dayInMonthFromCache == dayInMonth) {
                    return events;
                }
            }
        }
        return null;
    }

    private String getKeyForCalendarEvent(Calendar cal) {
        return cal.get(Calendar.YEAR) + "_" + cal.get(Calendar.MONTH);
    }


}
