package edu.dartmouth.cs.havvapa.pageManager;

import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import java.util.ArrayList;

public class ActionTabsViewPagerAdapter extends FragmentPagerAdapter {
    private static final String TAG = ActionTabsViewPagerAdapter.class.getSimpleName();;
    private ArrayList<Fragment> fragments;

    public static final int ToDo = 0;
    public static final int WHATS_NEW = 1;

    public static final String UI_TAB_TODO = "To Do";
    public static final String UI_TAB_NEWS = "What's New";



    public ActionTabsViewPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments){
        super(fm);
        this.fragments = fragments;
    }

    // Return the Fragment associated with a specified position.
    public Fragment getItem(int pos){
        Log.d(TAG, "getItem " + "position" + pos);
        return fragments.get(pos);
    }

    // Return the number of views available
    public int getCount(){
        Log.d(TAG, "getCount " + "size " + fragments.size());
        return fragments.size();
    }

    // This method may be called by the ViewPager to obtain a title string
    // to describe the specified page
    public CharSequence getPageTitle(int position) {
        Log.d(TAG, "getPageTitle " + "position " + position);
        switch (position)
        {
            case ToDo:
                return UI_TAB_TODO;
            case WHATS_NEW:
                return UI_TAB_NEWS;

            default:
                break;
        }
        return null;
    }
}



