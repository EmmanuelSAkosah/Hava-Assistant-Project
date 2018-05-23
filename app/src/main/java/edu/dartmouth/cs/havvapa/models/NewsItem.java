package edu.dartmouth.cs.havvapa.models;

public class NewsItem {

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getURL() {
        return mURL;
    }

    public void setURL(String mURL) {
        this.mURL = mURL;
    }

    public String getSource() {
        return mSource;
    }

    public void setSource(String mSource) {
        this.mSource = mSource;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    private String mTitle;
    private String mURL;
    private String mSource;
    private String imageURL = "";


    public NewsItem(String title, String url) {
        mTitle = title;
        mURL = url;
    }
}
