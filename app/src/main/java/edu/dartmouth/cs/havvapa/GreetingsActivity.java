package edu.dartmouth.cs.havvapa;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.ibm.watson.developer_cloud.android.library.audio.StreamPlayer;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.websocket.BaseRecognizeCallback;
import com.ibm.watson.developer_cloud.speech_to_text.v1.websocket.RecognizeCallback;

import org.json.JSONObject;

import java.io.InputStream;


import edu.dartmouth.cs.havvapa.APIs.TextToSpeechHelper;
import edu.dartmouth.cs.havvapa.APIs.WeatherHelper;
import edu.dartmouth.cs.havvapa.APIs.SpeechToTextHelper;
import edu.dartmouth.cs.havvapa.models.Weather;


import static edu.dartmouth.cs.havvapa.APIs.WeatherHelper.API_KEY;


public class GreetingsActivity extends AppCompatActivity
{
    private static final String TAG = "GreetingsActivity";
    private boolean permissionToRecordAccepted = false;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static final int RECORD_REQUEST_CODE = 101;
    private Button recordBtn;
    private TextView inputMessage;
    private SpeechToText S2T_service;
    StreamPlayer streamPlayer;
    private Button listenBtn;
    //public Weather weather;

    private RecognizeCallback mS2TCallback;
    private SpeechToTextHelper speechToTextHelper;
    private TextToSpeechHelper textToSpeechHelper;
    private TextView textView;
    private TextView greetings_tv;
    private FusedLocationProviderClient mFusedLocationClient;



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
        listenBtn = findViewById(R.id.listen_btn_greetings);
        inputMessage = findViewById(R.id.transcribed_text_tv);

        textView = (TextView) findViewById(R.id.textView);
        greetings_tv = findViewById(R.id.tv_greetings);
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
                speechToTextHelper.recordMessage(mS2TCallback,GreetingsActivity.this);
            }
        });
        listenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textToSpeechHelper.readAloud(greetings_tv.getText().toString());
            }
        });

        //get API helpers
        speechToTextHelper = new SpeechToTextHelper();
        S2T_service = speechToTextHelper.getService();
        textToSpeechHelper = new TextToSpeechHelper();

        //permission to record audio
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission to record denied");
            makeRequest();
        }

        //SpeechToText callback
        mS2TCallback = new BaseRecognizeCallback() {
            @Override
            public void onTranscription(SpeechResults speechResults) {
                if(speechResults.getResults() != null && !speechResults.getResults().isEmpty()) {
                    String text = speechResults.getResults().get(0).getAlternatives().get(0).getTranscript();
                    speechToTextHelper.showMicText(text,GreetingsActivity.this,inputMessage);
                    speechToTextHelper.executeCommand(text,getApplicationContext()); // act on transcribed text
                }
            }

            //@Override
            public void onTranscriptionComplete(){

            }

            @Override public void onError(Exception e) {
                speechToTextHelper.showError(e,GreetingsActivity.this);
                speechToTextHelper.enableMicButton(recordBtn,GreetingsActivity.this);
            }

            @Override
            public void onDisconnected() {
                 Toast.makeText(GreetingsActivity.this, "Watson Service Disconnected", Toast.LENGTH_LONG).show();
                speechToTextHelper.enableMicButton(recordBtn,GreetingsActivity.this);
            }
        };

        //get weather
      /*  mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            sendGETForWeather(location);
                        }
                    }


                }); */

    }

    //Weather
    public void sendGETForWeather(Location location) {
        //api.openweathermap.org/data/2.5/weather?lat=35&lon=139
        String url = WeatherHelper.BASE_URL+"lat="+location.getLatitude()+
                "&lon="+location.getLongitude()+API_KEY;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url,
                null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
               Weather weather = WeatherHelper.parseWeatherResponse(response);
               displayWeatherIcon(weather.getIcon());
            }
        },new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error.getMessage() != null) {
                    Log.d(TAG, "error is: " + error.getMessage());
                }
            }
        });

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(jsonObjectRequest);
    }

    public void displayWeatherIcon(String IconId){
        String IMG_URL = "http://openweathermap.org/img/w/"+IconId+".png";
        new downloadWeatherIconTask((ImageView) findViewById(R.id.weatherImage_img_greetings))
                .execute(IMG_URL);
    }

    private class downloadWeatherIconTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public downloadWeatherIconTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("This permission is important for the app.")
                            .setTitle("Important permission required");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                            }

                        }
                    });
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
            new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.ACCESS_COARSE_LOCATION},
            RECORD_REQUEST_CODE);
    }




}

