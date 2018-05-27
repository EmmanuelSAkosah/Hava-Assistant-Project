package edu.dartmouth.cs.havvapa;

import android.Manifest;
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

import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.websocket.BaseRecognizeCallback;
import com.ibm.watson.developer_cloud.speech_to_text.v1.websocket.RecognizeCallback;

import java.util.ArrayList;

import edu.dartmouth.cs.havvapa.models.NewsItem;
import edu.dartmouth.cs.havvapa.APIs.SpeechToTextHelper;



public class GreetingsActivity extends AppCompatActivity
{
    private static final String TAG = "GreetingsActivity";
    private boolean permissionToRecordAccepted = false;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static final int RECORD_REQUEST_CODE = 101;
    private Button recordBtn;
    private TextView inputMessage;
    private SpeechToText S2T_service;
    //public Weather weather;

    private RecognizeCallback mS2TCallback;
    private SpeechToTextHelper speech2Text;
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
                startActivity(new Intent(GreetingsActivity.this, AlarmManagmentActivity.class));
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
                speech2Text.recordMessage(mS2TCallback,GreetingsActivity.this);
            }
        });

        //call custom speech to text service
        speech2Text = new SpeechToTextHelper();
        S2T_service = speech2Text.getService();

        //permission to record audio
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission to record denied");
            makeRequest();
        }



        mS2TCallback = new BaseRecognizeCallback() {
            @Override
            public void onTranscription(SpeechResults speechResults) {
                if(speechResults.getResults() != null && !speechResults.getResults().isEmpty()) {
                    String text = speechResults.getResults().get(0).getAlternatives().get(0).getTranscript();
                    speech2Text.showMicText(text,GreetingsActivity.this,inputMessage);
                    executeCommand(text); // act on transcribed text
                }
            }

            //@Override
            public void onTranscriptionComplete(){
                //done transcribing, stop all results processing
                // Toast.makeText(getApplicationContext(), "Watson Audio Process Complete", Toast.LENGTH_LONG).show();

            }

            @Override public void onError(Exception e) {
                speech2Text.showError(e,GreetingsActivity.this);
                speech2Text.enableMicButton(recordBtn,GreetingsActivity.this);
            }

            @Override
            public void onDisconnected() {
                 Toast.makeText(GreetingsActivity.this, "Watson Service Disconnected", Toast.LENGTH_LONG).show();
                speech2Text.enableMicButton(recordBtn,GreetingsActivity.this);
            }
        };

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


    private void executeCommand(String transcribed_text){
       String speech = transcribed_text.toLowerCase();
        //sign in
        if (speech.contains("sign")){
            startActivity(new Intent(getApplicationContext(), SignUpActivity.class));

        }else if(speech.contains("schedule")) {
            startActivity(new Intent(getApplicationContext(), ScheduleEventActivity.class));

        }

    }
}
