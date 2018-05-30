package edu.dartmouth.cs.havvapa;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;

import edu.dartmouth.cs.havvapa.adapters.NewsListAdapter;
import edu.dartmouth.cs.havvapa.database_elements.NewsItemsSource;
import edu.dartmouth.cs.havvapa.models.NewsItem;

public class RecordedNewsActivity extends AppCompatActivity
{
    private final String SAVED_NEWS = "SAVED NEWS";
    public ArrayList<NewsItem> newsList;
    public static NewsListAdapter mNewsAdapter;
    private static ListView listView;
    private static NewsItemsSource datasource;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorded_news);
        datasource = new NewsItemsSource(getApplicationContext());

        listView = findViewById(R.id.recorded_news_listView);

        newsList = new ArrayList<>();

        try{
            newsList = datasource.getAllEntries();
            for(NewsItem item : newsList){
                Log.d("itemId", String.valueOf(item.getNewsItemId()) + "  " + String.valueOf(newsList.size()));
            }
            Toast.makeText(getApplicationContext(), String.valueOf(newsList.size()),Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(), "????",Toast.LENGTH_SHORT).show();

        }


        mNewsAdapter = new NewsListAdapter(this,R.layout.news_item,newsList);
        listView.setAdapter(mNewsAdapter);


    }

    public static void refreshHistoryView(){

        ArrayList<NewsItem> newsList = datasource.getAllEntries();
        if(newsList.size()>0){
            for(NewsItem item : newsList){
                Log.d("itemId", String.valueOf(item.getNewsItemId()) + "  " + String.valueOf(newsList.size()));
            }

            mNewsAdapter.setmNewsList(newsList);
            listView.setAdapter(mNewsAdapter);
        }
    }
}
