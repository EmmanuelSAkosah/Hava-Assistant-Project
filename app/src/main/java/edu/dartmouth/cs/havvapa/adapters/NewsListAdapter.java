package edu.dartmouth.cs.havvapa.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.net.URL;
import java.util.ArrayList;

import edu.dartmouth.cs.havvapa.R;
import edu.dartmouth.cs.havvapa.models.NewsItem;
import edu.dartmouth.cs.havvapa.models.ToDoItem;
import edu.dartmouth.cs.havvapa.utils.ImageManager;

public class NewsListAdapter extends ArrayAdapter {

    private ArrayList<NewsItem> mNewsList;
    private Context mContext;
  //  ImageView imageView;
    //View newsItemView;
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


    @Override
    public NewsItem getItem(int position) {
        return mNewsList.get(position);
    }

    public void clear() {
        mNewsList.clear();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
       View newsItemView = convertView;
        if (newsItemView == null)
            newsItemView = LayoutInflater.from(mContext).inflate(R.layout.news_item, parent, false);
        if (imageLoader == null)
            imageLoader = ImageManager.getInstance().getImageLoader();
        NetworkImageView newsThumbnail = (NetworkImageView) newsItemView
                .findViewById(R.id.tv_news_item_image);
        NewsItem curr = getItem(position);
        String title =  curr.getTitle();
        String source = curr.getSource();
        final String imageUrl = curr.getImageURL();

        TextView title_tv = newsItemView.findViewById(R.id.tv_news_item_title);
        TextView source_tv = newsItemView.findViewById(R.id.tv_news_item_source);

        newsThumbnail.setImageUrl(imageUrl, imageLoader);
        title_tv.setText(title);
        source_tv.setText(source);


       /* new Thread(new Runnable() {
            @Override public void run() {
                try {
                    URL url = new URL(imageUrl);
                    Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    displayImage(bitmap,newsItemView);
                }catch (Exception e){
                    e.printStackTrace();
                   //showImageUnavailableError();
                }
            }
        }).start(); */

        return newsItemView;
    }

   /* public void displayImage(final Bitmap bitmap, final View newsItemView){
        ((Activity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (bitmap == null) {
                    showImageUnavailableError();
                }else {
                    imageView = newsItemView.findViewById(R.id.tv_news_item_image);
                    imageView.setImageBitmap(bitmap);
                }
            }
        });
    }

    public void showImageUnavailableError(){
        ((Activity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imageView.setContentDescription("Image not available");
            }
        });
    } */
}
