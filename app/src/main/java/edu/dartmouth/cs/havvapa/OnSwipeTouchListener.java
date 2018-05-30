package edu.dartmouth.cs.havvapa;

import android.content.Context;
import android.graphics.Rect;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;

public class OnSwipeTouchListener implements OnTouchListener {

    private final GestureDetector gestureDetector;
    public static int motionX;
    public static int motionY;
    public static enum Action{
        Left,
        Right,
        Top,
        Bottom,
        None
    }
    private Action mSwipeDetected = Action.None;

    public OnSwipeTouchListener (Context ctx)
    {
        gestureDetector = new GestureDetector(ctx, new GestureListener());
    }




    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);

    }

    private final class GestureListener extends SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        //private Action mSwipeDetected = Action.None;

        public boolean swipeDetected(){
            return mSwipeDetected != Action.None;
        }
        public Action getAction(){
            return mSwipeDetected;
        }


        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                motionX =(int) e2.getRawX();
                motionY = (int) e2.getRawY();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight();
                        } else {
                            onSwipeLeft();
                        }
                        result = true;
                    }
                }
                else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        onSwipeBottom();
                    } else {
                        onSwipeTop();
                    }
                    result = true;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }
    }

    public void onSwipeRight() {
         mSwipeDetected = Action.Right;
    }

    public void onSwipeLeft() {
        mSwipeDetected = Action.Left;
    }

    public void onSwipeTop() {
        mSwipeDetected = Action.Top;
    }

    public void onSwipeBottom() {
        mSwipeDetected = Action.Bottom;
    }

    public static int getMotionX() {
        return motionX;
    }

    public static void setMotionX(int motionx) {
        motionX = motionx;
    }

    public static int getMotionY() {
        return motionY;
    }

    public static void setMotionY(int motiony) {
        motionY = motiony;
    }
}
