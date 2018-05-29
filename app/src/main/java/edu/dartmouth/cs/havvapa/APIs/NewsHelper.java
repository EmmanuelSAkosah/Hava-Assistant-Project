package edu.dartmouth.cs.havvapa.APIs;

import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import edu.dartmouth.cs.havvapa.models.NewsItem;
import edu.dartmouth.cs.havvapa.utils.Constants;

public class NewsHelper {
    public NewsHelper(){

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

    public String getNewsOnConcept(String concept){
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_YEAR, -21);
        Long last3Weeks = cal.getTimeInMillis();

        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        df.setTimeZone(tz);
        String dateISO = df.format(last3Weeks);

        return Constants.baseNewsURL+
                "everything?pageSize=15&" +
                "q="+concept+"&from="+dateISO+Constants.newsAPIKey;
    }

    public ArrayList<NewsItem> parseResponse(JSONObject response){
        ArrayList<NewsItem> newsList = new ArrayList<>();
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
        return newsList;
    }
}
