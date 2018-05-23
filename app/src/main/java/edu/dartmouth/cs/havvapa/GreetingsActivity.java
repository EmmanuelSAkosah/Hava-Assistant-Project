package edu.dartmouth.cs.havvapa;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ibm.watson.developer_cloud.android.library.audio.MicrophoneInputStream;
import com.ibm.watson.developer_cloud.android.library.audio.utils.ContentType;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.websocket.BaseRecognizeCallback;
import com.ibm.watson.developer_cloud.speech_to_text.v1.websocket.RecognizeCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import edu.dartmouth.cs.havvapa.models.ExampleNewsResponse;
import edu.dartmouth.cs.havvapa.models.NewsItem;

import static android.content.ContentValues.TAG;
import static java.security.AccessController.getContext;


public class GreetingsActivity extends AppCompatActivity
{
    private static final String TAG = "GreetingsActivity";
    private boolean permissionToRecordAccepted = false;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static final int RECORD_REQUEST_CODE = 101;
    private boolean listening = false;
    private Button recordBtn;
    private TextView inputMessage;
    private SpeechToText S2T_service;
    private MicrophoneInputStream capture;
    private RecognizeCallback mCallback;
    public static ArrayList<NewsItem> newsList;

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menuitem_settings:
                startActivity(new Intent(GreetingsActivity.this, SignUpActivity.class));
                return true;

            case R.id.menuitem_editProfile:
                startActivity(new Intent(GreetingsActivity.this, SignUpActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_greetings);

        recordBtn = findViewById(R.id.record_btn_greetings);
        inputMessage = findViewById(R.id.transcribed_text_tv);
        newsList = new ArrayList<>();

        TextView greetings_tv = findViewById(R.id.tv_greetings);
        greetings_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
                return;
            }
        });

        recordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                recordMessage();
            }
        });

        S2T_service = new SpeechToText("5d82fc7f-953a-401c-9d32-0634c24d8b24",
                "XnBJiBWcMkC5");


        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission to record denied");
            makeRequest();
        }



        mCallback = new BaseRecognizeCallback() {
            @Override
            public void onTranscription(SpeechResults speechResults) {
                System.out.println(speechResults);
                if(speechResults.getResults() != null && !speechResults.getResults().isEmpty()) {
                    String text = speechResults.getResults().get(0).getAlternatives().get(0).getTranscript();
                    showMicText(text);
                    executeCommand(text); // act on transcribed text
                }
            }

            //@Override
            public void onTranscriptionComplete(){
                //done transcribing, stop all results processing
                // Toast.makeText(getApplicationContext(), "Watson Audio Process Complete", Toast.LENGTH_LONG).show();

            }

            @Override public void onError(Exception e) {
                showError(e);
                enableMicButton();
            }



            @Override
            public void onDisconnected() {
                //   Toast.makeText(, "Watson Service Disconnected", Toast.LENGTH_LONG).show();
                enableMicButton();
            }
        };

        //News API GET request

    }

// Speech-to-Text Record Audio permission
        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                               @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
            case RECORD_REQUEST_CODE: {

                if (grantResults.length == 0
                        || grantResults[0] !=
                        PackageManager.PERMISSION_GRANTED) {

                    Log.i(TAG, "Permission has been denied by user");
                } else {
                    Log.i(TAG, "Permission has been granted by user");
                }
                return;
            }
        }
        if (!permissionToRecordAccepted ) finish();

    }

    protected void makeRequest() {
    ActivityCompat.requestPermissions(this,
            new String[]{Manifest.permission.RECORD_AUDIO},
            RECORD_REQUEST_CODE);
    }

    //Private Methods - Speech to Text
    private RecognizeOptions getRecognizeOptions() {
        return new RecognizeOptions.Builder()
                .contentType(ContentType.OPUS.toString())
                .model("en-US_NarrowbandModel")
                .interimResults(true)
                .inactivityTimeout(2000)
                .build();
    }


    private void showMicText(final String text) {
        runOnUiThread(new Runnable() {
            @Override public void run() {
                inputMessage.setText(text);
            }
        });
    }

    private void enableMicButton() {
        runOnUiThread(new Runnable() {
            @Override public void run() {
                recordBtn.setEnabled(true);
            }
        });
    }

    private void showError(final Exception e) {
        runOnUiThread(new Runnable() {
            @Override public void run() {
                Toast.makeText(GreetingsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                e.printStackTrace();
            }
        });
    }

    private void recordMessage() {
        //mic.setEnabled(false);

        if(listening != true) {
            capture = new MicrophoneInputStream(true);
            new Thread(new Runnable() {
                @Override public void run() {
                    try {
                        S2T_service.recognizeUsingWebSocket(capture, getRecognizeOptions(), mCallback);
                    } catch (Exception e) {
                        showError(e);
                    }
                }
            }).start();
            listening = true;
            Toast.makeText(GreetingsActivity.this,"Listening....Click to Stop", Toast.LENGTH_LONG).show();

        } else {
            try {
                capture.close();
                listening = false;
                Toast.makeText(GreetingsActivity.this,"Stopped Listening....Click to Start", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void executeCommand(String transcribed_text){
       String speech = transcribed_text.toLowerCase();
        //sign in
        if (speech.contains("sign")){
            startActivity(new Intent(getApplicationContext(), SignUpActivity.class));

        }else if(speech.contains("schedule")) {
            startActivity(new Intent(getApplicationContext(), ScheduleEventActivity.class));

        }

    }



        private static final String GET_URL =
       "https://newsapi.org/v2/top-headlines?country=us&pageSize=5&apiKey=752c89d9fdb143298b57034a95939344";
    // "http://localhost:9090/SpringMVCExample"; 752c89d9fdb143298b57034a95939344


}
