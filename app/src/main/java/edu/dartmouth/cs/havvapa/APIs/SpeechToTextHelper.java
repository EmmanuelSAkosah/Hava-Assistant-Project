package edu.dartmouth.cs.havvapa.APIs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ibm.watson.developer_cloud.android.library.audio.MicrophoneInputStream;
import com.ibm.watson.developer_cloud.android.library.audio.utils.ContentType;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.websocket.RecognizeCallback;

import edu.dartmouth.cs.havvapa.GreetingsActivity;
import edu.dartmouth.cs.havvapa.NewsActivity;
import edu.dartmouth.cs.havvapa.ScheduleEventActivity;
import edu.dartmouth.cs.havvapa.SignUpActivity;


public class SpeechToTextHelper {

    private SpeechToText mService;
    private MicrophoneInputStream capture;
    private boolean listening = false;
    TextToSpeechHelper textToSpeechHelper = new TextToSpeechHelper();



    public SpeechToTextHelper(){
        mService = new SpeechToText("5d82fc7f-953a-401c-9d32-0634c24d8b24",
                "XnBJiBWcMkC5");
    }

    public SpeechToText getService(){
        return mService;
    };

    public void recordMessage(final RecognizeCallback callback,final Context context) {

    if(listening != true) {
        capture = new MicrophoneInputStream(true);
        new Thread(new Runnable() {
            @Override public void run() {
                try {
                    mService.recognizeUsingWebSocket(capture, getRecognizeOptions(), callback);
                } catch (Exception e) {
                    showError(e, context);
                }
            }
        }).start();
        listening = true;
        Toast.makeText(context,"Listening....Click to Stop", Toast.LENGTH_LONG).show();

    } else {
        try {
            capture.close();
            listening = false;
            Toast.makeText(context,"Stopped Listening...Click to Start", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

    //Private Methods - Speech to Text
    public RecognizeOptions getRecognizeOptions() {
        return new RecognizeOptions.Builder()
                .contentType(ContentType.OPUS.toString())
                .model("en-US_NarrowbandModel")
                .interimResults(true)
                .inactivityTimeout(2000)
                .build();
    }


    public void showError(final Exception e, final Context context) {
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override public void run() {
                Toast.makeText(context, e.getMessage(),
                        Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
    }

    public void showMicText(final String text, Context context, final TextView textView) {
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override public void run() {
                textView.setText(text);
            }
        });
    }

    public void enableMicButton(final Button recordBtn,Context context) {
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override public void run() {
                recordBtn.setEnabled(true);
            }
        });
    }

    public void enableMicButton(final FloatingActionButton recordBtn, Context context) {
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override public void run() {
                recordBtn.setEnabled(true);
            }
        });
    }

    public void executeCommand(String transcribed_text, Context context){
        String speech = transcribed_text.toLowerCase();
        //sign in
        if (speech.contains("sign") || speech.contains("log in") || (speech.contains("register"))){
            context.startActivity(new Intent(context, SignUpActivity.class));

            //news activity
        }else if(speech.contains("news") || speech.contains("headlines") || speech.contains("happening") ) {
            context.startActivity(new Intent(context, NewsActivity.class));

            //open schedule activity
        }else if(speech.contains("schedule") || speech.contains("event") || speech.contains("to do")
                || speech.contains("reminder") || speech.contains("plan")) { //open schedule activity
            context.startActivity(new Intent(context, ScheduleEventActivity.class));
        }        //tell weather, readheadlines, say my name
        else if (speech.contains("tell") || speech.contains("say") || speech.contains("read")){

            if ((speech.contains("weather") || speech.contains("temperature")) || speech.contains("conditions")){
                GreetingsActivity.tellWeather();
            }

            if(speech.contains("news")){
                GreetingsActivity.readNewsAloud();
            }

        }
        else if (speech.contains("tell me about") || speech.contains("who") || speech.contains("explain") || speech.contains("describe")){
            context.startActivity(new Intent(context, NewsActivity.class));
        }

        else if (speech.contains("socks") || speech.contains("arsenal") || speech.contains("socks")){
           textToSpeechHelper.readAloud("Arsenal Sucks. Big time!");
        }

        }

}

