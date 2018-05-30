package edu.dartmouth.cs.havvapa.models;

public class GreetingsNewsItem {
    private String mTitle;
    private String mSource;


    public GreetingsNewsItem(String title, String source) {
        mTitle = title;
        mSource = source;
    }
    public String getTitle() {
        return mTitle;
    }
    public String getSource() {
        return mSource;
    }
}
