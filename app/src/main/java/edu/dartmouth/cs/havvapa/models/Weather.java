package edu.dartmouth.cs.havvapa.models;

public class Weather {
    public int getTemperature() {
        return mTemperature;
    }

    public void setTemperature(int mTemperature) {
        this.mTemperature = mTemperature;
    }

    public String getDecscription() {
        return mDescription;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public String getCondition() {
        return mCondition;
    }

    public void setCondition(String mCondition) {
        this.mCondition = mCondition;
    }


    public String getIcon() {
        return mIcon;
    }

    public void setIcon(String mIcon) {
        this.mIcon = mIcon;
    }
    public int getClouds() {
        return mClouds;
    }

    public void setClouds(int mClouds) {
        this.mClouds = mClouds;
    }

    private int mTemperature;
    private String mDescription;
    private String mCondition;
    private String mIcon;
    private int mClouds;


}
