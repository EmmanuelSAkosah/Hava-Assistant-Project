package edu.dartmouth.cs.havvapa.pageManager;

import android.support.v4.view.ViewPager;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class DeactivateSwipeViewPager extends ViewPager {

    public DeactivateSwipeViewPager(Context c, AttributeSet aSet)
    {
        super(c, aSet);
    }

    // two methods to prevent swiping
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }


}
