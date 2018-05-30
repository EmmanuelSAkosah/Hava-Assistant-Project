package edu.dartmouth.cs.havvapa.APIs;

import android.content.Context;
import android.os.AsyncTask;

import com.ibm.watson.developer_cloud.android.library.audio.StreamPlayer;
import com.ibm.watson.developer_cloud.text_to_speech.v1.TextToSpeech;
import com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice;

import org.w3c.dom.Text;

import edu.dartmouth.cs.havvapa.GreetingsActivity;
import edu.dartmouth.cs.havvapa.utils.Preferences;

public class TextToSpeechHelper {
    StreamPlayer streamPlayer;


    private TextToSpeech initTextToSpeechService() {
        TextToSpeech service = new TextToSpeech();
        String username = "ff4968b9-83d1-4503-8774-ca8613213cff";
        String password = "mPvTlusJCxf0";
        service.setUsernameAndPassword(username, password);
        return service;
    }

    private class WatsonTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... textToSpeak) {

            TextToSpeech textToSpeech = initTextToSpeechService();
            streamPlayer = new StreamPlayer();
            streamPlayer.playStream(textToSpeech.synthesize(textToSpeak[0],Voice.EN_LISA).execute());

            return "text to speech done";
        }

        @Override
        protected void onPostExecute(String result) {
        }

    }

    public void readAloud(String text){
       WatsonTask task = new WatsonTask();
       task.execute(text);

    }

}