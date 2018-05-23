package edu.dartmouth.cs.havvapa;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.util.Log;
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
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import edu.dartmouth.cs.havvapa.adapters.NewsListAdapter;
import edu.dartmouth.cs.havvapa.models.NewsItem;
import edu.dartmouth.cs.havvapa.utils.Constants;

public class NewsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    final String TAG = "NewsActivity";
    private ArrayList<NewsItem> newsList;
    private NewsListAdapter mNewsAdapter;
    private ListView listView;
    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        //set up app bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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

         drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        newsList = new ArrayList<>();

    }
    @Override
    public void onStart(){
        super.onStart();
        setUpView();
        sendGET(getNewsByCategory(0));// load general news headlines
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
        switch (item.getItemId()) {
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
          // sendGET(getNewsByCategory(id));
//TODO cut down code
        if (id == R.id.nav_general) {
            sendGET(getNewsByCategory(0));

        } else if (id == R.id.nav_business) {
            sendGET(getNewsByCategory(1));

        } else if (id == R.id.nav_entertainment) {
            sendGET(getNewsByCategory(2));

        } else if (id == R.id.nav_health) {
            sendGET(getNewsByCategory(3));

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public String getNewsByCategory(int categoryID){
        String category;
        switch (categoryID){
            case 0: category = "general"; break;
            case 1: category = "business";break;
            case 2: category = "entertainment";break;
            case 3: category = "health"; break;
            case 4: category = "science"; break;
            case 5: category = "sports"; break;
            case 6: category = "technology"; break;
            default: category = "general";
        }

        return Constants.baseNewsURL+
                "top-headlines?country=us&pageSize=8&" +
                "category="+category+Constants.newsAPIKey;
    }

    public void parseResponse(JSONObject response){

        try{
            JSONArray articles = response.getJSONArray("articles");
            for(int i = 0; i < articles.length(); i++){
                JSONObject article = articles.getJSONObject(i);
                String title = article.getString("title");
                String url = article.getString("url");
                String source = (article.getJSONObject("source")).getString("name");
                String imageURL = article.getString("urlToImage");

                NewsItem news = new NewsItem(title,url);
                news.setSource(source);
                news.setImageURL(imageURL);
                newsList.add(news);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendGET(String url) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
               // Log.d(TAG, "Response is"+ response);
                parseResponse(response);
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
       mNewsAdapter.notifyDataSetChanged();
    }

    public void setUpView(){
        mNewsAdapter = new NewsListAdapter(this,R.layout.news_item,newsList);
        listView = (ListView)findViewById(R.id.news_list_NDA);

        listView.setAdapter(mNewsAdapter);
        listView.setOnItemClickListener(mListener);
    }


    AdapterView.OnItemClickListener mListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view,
                                int position, long id) {
            openWebPage(newsList.get(position).getSource());
        }
    };

    public void openWebPage(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No application can handle this request." +
                    " Please install a web browser or check your URL.",  Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
