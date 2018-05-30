package edu.dartmouth.cs.havvapa.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.net.URL;
import java.util.ArrayList;

import edu.dartmouth.cs.havvapa.NewsActivity;
import edu.dartmouth.cs.havvapa.OnSwipeTouchListener;
import edu.dartmouth.cs.havvapa.R;
import edu.dartmouth.cs.havvapa.RecordedNewsActivity;
import edu.dartmouth.cs.havvapa.database_elements.NewsItemsSource;
import edu.dartmouth.cs.havvapa.models.NewsItem;
import edu.dartmouth.cs.havvapa.models.ToDoEntry;
import edu.dartmouth.cs.havvapa.utils.ImageManager;

public class NewsListAdapter extends ArrayAdapter {

    private ArrayList<NewsItem> mNewsList;
    private Context mContext;
    private NewsItemsSource datasource;

    View newsItemView;
    ImageLoader imageLoader = ImageManager.getInstance().getImageLoader();

    public NewsListAdapter(Context context,int layoutID, ArrayList<NewsItem> newsList){
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
    public View getView(int position, View convertView, ViewGroup parent){
        newsItemView = convertView;
        if (newsItemView == null)
            newsItemView = LayoutInflater.from(mContext).inflate(R.layout.news_item, parent, false);
        if (imageLoader == null)
            imageLoader = ImageManager.getInstance().getImageLoader();
        final NetworkImageView networkImageView = (NetworkImageView) newsItemView
                .findViewById(R.id.tv_news_item_image);
        final NewsItem curr = getItem(position);
        String title =  curr.getTitle();
        String source = curr.getSource();
        final String imageUrl = curr.getImageURL();

        TextView title_tv = newsItemView.findViewById(R.id.tv_news_item_title);
        TextView source_tv = newsItemView.findViewById(R.id.tv_news_item_source);

        networkImageView.setImageUrl(imageUrl, imageLoader);
        title_tv.setText(title);
        source_tv.setText(source);

        //final android.support.v7.widget.CardView networkImageView = newsItemView.findViewById(R.id.swipe_card);
        LinearLayout newsItemLinewrView = (LinearLayout)newsItemView.findViewById(R.id.history_layout);
        newsItemLinewrView.setOnTouchListener(new OnSwipeTouchListener(getContext())
        {
            //CardView newsItemView = mNewsAdapter.getSelectedView().findViewById(R.id.swipe_card);
            public void onSwipeTop() {
                //Toast.makeText(getContext(), "top", Toast.LENGTH_SHORT).show();



            }
            public void onSwipeRight()
            {
                //Toast.makeText(getContext(), "right", Toast.LENGTH_SHORT).show();
                //Toast.makeText(getContext(), "item swiped",Toast.LENGTH_SHORT).show();
                datasource = new NewsItemsSource(getContext());
                datasource.createItem(curr);
                Log.d("datasource_size", String.valueOf(datasource.getAllEntries().size()));
                Toast.makeText(getContext(), "Saved the article",Toast.LENGTH_SHORT).show();
                


            }
            public void onSwipeLeft() {
                //Toast.makeText(getContext(), "left", Toast.LENGTH_SHORT).show();
                datasource = new NewsItemsSource(getContext());
                try {
                    if(curr.getNewsItemId()!=0)
                    {
                        datasource.deleteItem(curr);
                        RecordedNewsActivity.refreshHistoryView();
                        Toast.makeText(getContext(), "Deleted the article",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else {
                        Log.d("cantDelete","item id: " + String.valueOf(curr.getNewsItemId()));
                    }
                }
                catch (Exception e){
                    Log.d("cantDelete", "database_error");
                }


            }
            public void onSwipeBottom() {
                //Toast.makeText(getContext(), "bottom", Toast.LENGTH_SHORT).show();

            }

        });

        return newsItemView;
    }

    public View getSelectedView(){
        return newsItemView;
    }

}
