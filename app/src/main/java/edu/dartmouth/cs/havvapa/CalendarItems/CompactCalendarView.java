package edu.dartmouth.cs.havvapa.CalendarItems;

import android.view.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.widget.OverScroller;



import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class CompactCalendarView extends View {

    public static final int FILL_LARGE_INDICATOR = 1;
    public static final int NO_FILL_LARGE_INDICATOR = 2;
    public static final int SMALL_INDICATOR = 3;

    private final HandleAnimation handleAnimation;
    private ControlCalendar controlCalendar;
    private GestureDetectorCompat gestureDetector;
    private boolean horizontalScrollEnabled = true;

    public interface CompactCalendarViewListener
    {
         void onDayClick(Date dateClicked);
         void onMonthScroll(Date firstDayOfNewMonth);
    }

    private final GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            controlCalendar.onSingleTapUp(e);
            invalidate();
            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if(horizontalScrollEnabled) {
                if (Math.abs(distanceX) > 0) {
                    getParent().requestDisallowInterceptTouchEvent(true);

                    controlCalendar.onScroll(e1, e2, distanceX, distanceY);
                    invalidate();
                    return true;
                }
            }

            return false;
        }
    };


    public CompactCalendarView(Context context) {
        this(context, null);
    }

    public CompactCalendarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CompactCalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        controlCalendar = new ControlCalendar(new Paint(), new OverScroller(getContext()),
                new Rect(), attrs, getContext(),  Color.argb(255, 233, 84, 81),
                Color.argb(255, 64, 64, 64), Color.argb(255, 219, 219, 219), VelocityTracker.obtain(),
                Color.argb(255, 100, 68, 65), new EventsContainer(Calendar.getInstance()),
                Locale.getDefault(), TimeZone.getDefault());
        gestureDetector = new GestureDetectorCompat(getContext(), gestureListener);
        handleAnimation = new HandleAnimation(controlCalendar, this);
    }

    public void setFirstDayOfWeek(int dayOfWeek){
        controlCalendar.setFirstDayOfWeek(dayOfWeek);
        invalidate();
    }



    public void setListener(CompactCalendarViewListener listener){
        controlCalendar.setListener(listener);
    }




    public void addEvents(List<Event> events){
        controlCalendar.addEvents(events);
        invalidate();
    }

    public void setUseThreeLetterAbbreviation(boolean useThreeLetterAbbreviation){
        controlCalendar.setUseWeekDayAbbreviation(useThreeLetterAbbreviation);
        invalidate();
    }


    public List<Event> getEvents(Date date){
        return controlCalendar.getCalendarEventsFor(date.getTime());
    }


    public void removeAllEvents() {
        controlCalendar.removeAllEvents();
        invalidate();
    }

    @Override
    protected void onMeasure(int parentWidth, int parentHeight) {
        super.onMeasure(parentWidth, parentHeight);
        int width = MeasureSpec.getSize(parentWidth);
        int height = MeasureSpec.getSize(parentHeight);
        if(width > 0 && height > 0) {
            controlCalendar.onMeasure(width, height, getPaddingRight(), getPaddingLeft());
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        controlCalendar.onDraw(canvas);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if(controlCalendar.computeScroll()){
            invalidate();
        }
    }


    public boolean onTouchEvent(MotionEvent event) {
        if (horizontalScrollEnabled) {
            controlCalendar.onTouch(event);
            invalidate();
        }

        if((event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP) && horizontalScrollEnabled) {
            getParent().requestDisallowInterceptTouchEvent(false);
        }

        return gestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean canScrollHorizontally(int direction) {
        if (this.getVisibility() == View.GONE) {
            return false;
        }
        return this.horizontalScrollEnabled;
    }

}
