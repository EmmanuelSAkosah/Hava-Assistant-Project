package edu.dartmouth.cs.havvapa;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import edu.dartmouth.cs.havvapa.APIs.NewsHelper;
import edu.dartmouth.cs.havvapa.adapters.NewsListAdapter;
import edu.dartmouth.cs.havvapa.models.ExampleNewsResponse;
import edu.dartmouth.cs.havvapa.models.NewsItem;
import edu.dartmouth.cs.havvapa.utils.Constants;
//import edu.dartmouth.cs.havvapa.SimpleGestureFilter.SimpleGestureListener;
import edu.dartmouth.cs.havvapa.OnSwipeTouchListener;

public class NewsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    final String TAG = "NewsActivity";
    private final String SAVED_NEWS = "SAVED NEWS";
    public static ArrayList<NewsItem> newsList;
    public  NewsListAdapter mNewsAdapter;
    private ListView listView;
    private DrawerLayout drawer;
    private NewsHelper newsHelper;
    private JSONObject mResponse;
    private OnSwipeTouchListener onSwipeTouchListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        newsHelper = new NewsHelper();
        newsList = new ArrayList<>();


        if (savedInstanceState != null) {
            try {
                mResponse = new JSONObject(savedInstanceState.getString(SAVED_NEWS,""));
                newsList = newsHelper.parseResponse(mResponse);
            } catch (Exception e){
                e.printStackTrace();
            }

        }

        //set up app bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);

        //Set as refresh button
      /*  FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }); */

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setUpView();
        sendGET(newsHelper.getNewsByCategory(0));// load general news headlines

    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SAVED_NEWS,mResponse.toString());
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.news, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);
                return true;

            case R.id.saved_news:
                startActivity(new Intent(NewsActivity.this, RecordedNewsActivity.class));
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_general) {
            sendGET(newsHelper.getNewsByCategory(0));

        } else if (id == R.id.nav_business) {
            sendGET(newsHelper.getNewsByCategory(1));

        } else if (id == R.id.nav_entertainment) {
            sendGET(newsHelper.getNewsByCategory(2));

        } else if (id == R.id.nav_health) {
            sendGET(newsHelper.getNewsByCategory(3));

        } else if (id == R.id.nav_find_out) {
            openFindOutFragment();
        }
        drawer.closeDrawer(GravityCompat.START);
        refreshView();
        return true;
    }

    public void sendGET(String url) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mResponse = response;
                newsList = newsHelper.parseResponse(response);
                refreshView();
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error.getMessage() != null) {
                    Log.d(TAG, "error is: " + error.getMessage());
                }
            }
        });

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(jsonObjectRequest);
    }

    public void refreshView(){
        mNewsAdapter = new NewsListAdapter(this,R.layout.news_item,newsList);
        listView.setAdapter(mNewsAdapter);
       //mNewsAdapter.notifyDataSetChanged();
        //Log.d("ASIZE1", String.valueOf(mNewsAdapter.getAllNewsViews().size()));
        Log.d("ASIZE2", String.valueOf(newsList.size()));
        /*
        try{
            listView.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext())
            {
                CardView newsItemView = mNewsAdapter.getSelectedView().findViewById(R.id.swipe_card);
                public void onSwipeTop() {
                    Toast.makeText(getApplicationContext(), "top", Toast.LENGTH_SHORT).show();
                    Rect rect = new Rect();
                    //int childCount = listView.getChildCount();
                    //Log.d("Child number", String.valueOf(childCount));
                    int [] listViewCoords = new int[2];
                    newsItemView.getLocationOnScreen(listViewCoords);
                    int x = OnSwipeTouchListener.getMotionX() - listViewCoords[0];
                    int y = OnSwipeTouchListener.getMotionY() - listViewCoords[1];
                    //View child;
                    newsItemView.getHitRect(rect);
                    if(rect.contains(x,y))
                    {
                        Toast.makeText(getApplicationContext(), "item swiped",Toast.LENGTH_SHORT).show();
                    }


                }
                public void onSwipeRight() {
                    Toast.makeText(getApplicationContext(), "right", Toast.LENGTH_SHORT).show();
                    Rect rect = new Rect();
                    //int childCount = listView.getChildCount();
                    //Log.d("Child number", String.valueOf(childCount));
                    int [] listViewCoords = new int[2];
                    newsItemView.getLocationOnScreen(listViewCoords);
                    int x = OnSwipeTouchListener.getMotionX() - listViewCoords[0];
                    int y = OnSwipeTouchListener.getMotionY() - listViewCoords[1];
                    View child;
                    newsItemView.getHitRect(rect);
                    if(rect.contains(x,y))
                    {
                        Toast.makeText(getApplicationContext(), "item swiped",Toast.LENGTH_SHORT).show();
                    }



                }
                public void onSwipeLeft() {
                    Toast.makeText(getApplicationContext(), "left", Toast.LENGTH_SHORT).show();
                    Rect rect = new Rect();
                    //int childCount = listView.getChildCount();
                    // Log.d("Child number", String.valueOf(childCount));
                    int [] listViewCoords = new int[2];
                    newsItemView.getLocationOnScreen(listViewCoords);
                    int x = OnSwipeTouchListener.getMotionX() - listViewCoords[0];
                    int y = OnSwipeTouchListener.getMotionY() - listViewCoords[1];
                    View child;
                    newsItemView.getHitRect(rect);
                    if(rect.contains(x,y))
                    {
                        Toast.makeText(getApplicationContext(), "item swiped",Toast.LENGTH_SHORT).show();
                    }
                }
                public void onSwipeBottom() {
                    Toast.makeText(getApplicationContext(), "bottom", Toast.LENGTH_SHORT).show();
                    Rect rect = new Rect();
                    //int childCount = listView.getChildCount();
                    //Log.d("Child number", String.valueOf(childCount));
                    int [] listViewCoords = new int[2];
                    newsItemView.getLocationOnScreen(listViewCoords);
                    int x = OnSwipeTouchListener.getMotionX() - listViewCoords[0];
                    int y = OnSwipeTouchListener.getMotionY() - listViewCoords[1];
                    View child;
                    newsItemView.getHitRect(rect);
                    if(rect.contains(x,y))
                    {
                        Toast.makeText(getApplicationContext(), "item swiped",Toast.LENGTH_SHORT).show();
                    }
                }

            });

        }
        catch (Exception e){
            Log.d("HATA", "NULL");
        }*/






    }

    public void setUpView()
    {
        //mNewsAdapter = new NewsListAdapter(this,R.layout.news_item,newsList);
        listView = findViewById(R.id.news_list_NDA);
        listView.setOnItemClickListener(mListener);




    }


    AdapterView.OnItemClickListener mListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            openWebPage(newsList.get(position).getURL(),getApplicationContext());
            Toast.makeText(NewsActivity.this, "adapter", Toast.LENGTH_SHORT).show();

        }
    };


    public static void openWebPage(String url, Context context) {

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "No application can handle this request." +
                    " Please install a web browser or check your URL.",  Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

   public void openFindOutFragment(){
       FindOutFragment findOutFragment = new FindOutFragment();
       FragmentManager manager = getFragmentManager();
       FragmentTransaction transaction = manager.beginTransaction();
       transaction.add(R.id.drawer_layout,findOutFragment,FindOutFragment.TAG);
       transaction.addToBackStack(null);
       transaction.commit();
   }


}
