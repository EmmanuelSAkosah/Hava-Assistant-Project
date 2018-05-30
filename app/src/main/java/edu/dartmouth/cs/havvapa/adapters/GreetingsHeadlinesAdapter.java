package edu.dartmouth.cs.havvapa.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import edu.dartmouth.cs.havvapa.R;
import edu.dartmouth.cs.havvapa.database_elements.NewsItemsSource;
import edu.dartmouth.cs.havvapa.models.NewsItem;
import edu.dartmouth.cs.havvapa.utils.ImageManager;

public class GreetingsHeadlinesAdapter extends ArrayAdapter {

    private ArrayList<NewsItem> mNewsList;
    private Context mContext;
    private NewsItemsSource datasource;

    View newsItemView;


    public GreetingsHeadlinesAdapter(Context context,int layoutID, ArrayList<NewsItem> newsList){
        super(context,layoutID);
        mNewsList = newsList;
        mContext = context;
    }

    public int getCount() {
        return mNewsList.size();
    }

    @Override
    public boolean isEmpty() {
        return mNewsList.size() == 0;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public ArrayList<NewsItem> getmNewsList() {
        return mNewsList;
    }

    public void setmNewsList(ArrayList<NewsItem> mNewsList) {
        this.mNewsList = mNewsList;
    }

    @Override
    public NewsItem getItem(int position) {
        return mNewsList.get(position);
    }

    public void clear() {
        mNewsList.clear();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        newsItemView = convertView;
        if (newsItemView == null)
            newsItemView = LayoutInflater.from(mContext).inflate(R.layout.news_item_greetings, parent, false);

        final NewsItem curr = getItem(position);
        String title = curr.getTitle();
        String source = curr.getSource();

        TextView title_tv = newsItemView.findViewById(R.id.tv_news_item_title);
        TextView source_tv = newsItemView.findViewById(R.id.tv_news_item_source);
        title_tv.setText(title);
        source_tv.setText(source);
        return newsItemView;

    }



}
