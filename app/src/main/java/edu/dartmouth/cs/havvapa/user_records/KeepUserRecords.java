package edu.dartmouth.cs.havvapa.user_records;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class KeepUserRecords {

    private SharedPreferences mUserRecordsPref;
    private SharedPreferences.Editor mUserRecordsEditor;

    public KeepUserRecords(Context context)
    {
        mUserRecordsPref = PreferenceManager.getDefaultSharedPreferences(context);
        mUserRecordsEditor = mUserRecordsPref.edit();
    }


    public void clearRecords(){
        mUserRecordsEditor.clear().commit();
    }

    public void setVibrationPref(boolean vibrationPref){
        mUserRecordsEditor.putBoolean("recorded_vibrationPref", vibrationPref).apply();
    }

    public boolean getVibrationPref(){
        return mUserRecordsPref.getBoolean("recorded_vibrationPref",true);
    }

    public void setSoundPref(boolean soundPref){
        mUserRecordsEditor.putBoolean("recorded_soundPref", soundPref).apply();
    }

    public boolean getSoundPref(){
        return mUserRecordsPref.getBoolean("recorded_soundPref", true);
    }

    public void setAppFirstTimeDownloaded(boolean firstTimeDownloaded){
        mUserRecordsEditor.putBoolean("first_time_downloaded", firstTimeDownloaded).apply();
    }

    public boolean isAppFirstTimeDownloaded(){
        return mUserRecordsPref.getBoolean("first_time_downloaded",true);
    }


    public void setUserEmail(String email){
        mUserRecordsEditor.putString("user_email", email).apply();
    }

    public String getUserEmail(){

        return mUserRecordsPref.getString("user_email","");
    }

}
