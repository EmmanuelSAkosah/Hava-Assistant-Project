package edu.dartmouth.cs.havvapa.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preferences {

    private SharedPreferences mPreferences;

    public Preferences(Context context){
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean isUserLoggedIn() {
        return mPreferences.getBoolean("loggedIn",true);
    }

    public void setUserLoggedIn(boolean value) {
        mPreferences.edit().putBoolean("privacy",value).apply();
    }

    public boolean hasVisitedLogIn() {
        return mPreferences.getBoolean("visitedLogin",false);
    }

    public void setHasVisitedLogIn(boolean value) {
        mPreferences.edit().putBoolean("visitedLogin",value).apply();
    }

    public void setUsername(String name){
        mPreferences.edit().putString("username",name).apply();
    }

    public String getUsername(){
        return mPreferences.getString("username","");
    }

    public void muteHavva(boolean value){
        mPreferences.edit().putBoolean("mute",value).apply();
    }

    public boolean isHavvaMute(){
        return mPreferences.getBoolean("mute",false);
    }
}
