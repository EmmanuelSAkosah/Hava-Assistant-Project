package edu.dartmouth.cs.havvapa;

import android.app.Activity;
import android.content.res.Configuration;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;
import edu.dartmouth.cs.havvapa.pageManager.ActionTabsViewPagerAdapter;



public class MainActivity extends AppCompatActivity
{
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ArrayList<Fragment> fragments;
    private ActionTabsViewPagerAdapter myViewPageAdapter;
    public static final int[] tabIcons = {R.mipmap.baseline_calendar_view_day_black_48, R.mipmap.baseline_chrome_reader_mode_black_48};

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("MainActivity");  // Set the title. I don't know how it can give null pointer exception.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Define SlidingTabLayout (shown at top)
        // and ViewPager (shown at bottom) in the layout.
        // Get their instances.
        tabLayout = (TabLayout) findViewById(R.id.tab);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        // create a fragment list in order.
        fragments = new ArrayList<Fragment>();
        fragments.add(new ToDoFragment());
        fragments.add(new WhatsNewFragment());



        // use FragmentPagerAdapter to bind the TabLayout (tabs with different titles)
        // and ViewPager (different pages of fragment) together.
        myViewPageAdapter =new ActionTabsViewPagerAdapter(getSupportFragmentManager(),
                fragments);
        // add the PagerAdapter to the viewPager
        viewPager.setAdapter(myViewPageAdapter);

        // The one-stop shop for setting up this TabLayout with a ViewPager.
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                if(tab.getPosition() == 1)
                {
                    startActivity(new Intent(MainActivity.this, NewsActivity.class));
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menuitem_settings:
                startActivity(new Intent(MainActivity.this, AlarmManagmentActivity.class));
                return true;

            case R.id.menuitem_editProfile:
                startActivity(new Intent(MainActivity.this, SignUpActivity.class));
                return true;

            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }
}
