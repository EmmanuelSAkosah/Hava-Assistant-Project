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
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;

import android.support.v4.content.Loader;

import android.support.v7.app.AlertDialog;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;


import android.widget.LinearLayout;
import android.widget.ListView;

import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
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


import java.util.ArrayList;
import java.util.Calendar;

import edu.dartmouth.cs.havvapa.APIs.NewsHelper;
import edu.dartmouth.cs.havvapa.AlarmHelpers.AlarmManagmentActivity;
import edu.dartmouth.cs.havvapa.adapters.GreetingsEventsAdapter;
import edu.dartmouth.cs.havvapa.adapters.GreetingsHeadlinesAdapter;
import edu.dartmouth.cs.havvapa.database_elements.ToDoItemsSource;
import edu.dartmouth.cs.havvapa.database_elements.UpcomingToDoEntryListLoader;
import edu.dartmouth.cs.havvapa.models.GreetingsToDoEntry;
import edu.dartmouth.cs.havvapa.models.NewsItem;
import edu.dartmouth.cs.havvapa.APIs.SpeechToTextHelper;
import edu.dartmouth.cs.havvapa.models.ToDoEntry;

import org.json.JSONObject;

import java.io.InputStream;


import edu.dartmouth.cs.havvapa.APIs.TextToSpeechHelper;
import edu.dartmouth.cs.havvapa.APIs.WeatherHelper;
import edu.dartmouth.cs.havvapa.models.Weather;
import edu.dartmouth.cs.havvapa.utils.Preferences;


import static edu.dartmouth.cs.havvapa.APIs.WeatherHelper.API_KEY;

public class GreetingsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<ToDoEntry>>
{
    private static final String TAG = "GreetingsActivity";


    private boolean permissionToRecordAccepted = false;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static final int RECORD_REQUEST_CODE = 101;
    private FloatingActionButton recordBtn;
    private TextView inputMessage;
    private static Preferences pref;

    private ToDoItemsSource datasource;
    private ArrayList<ToDoEntry> allEntries;
    private GreetingsEventsAdapter greetingsEventsAdapter;
    private GreetingsHeadlinesAdapter greetingsHeadlinesAdapter;
    ArrayList<GreetingsToDoEntry> updatedGreetingsEntries = new ArrayList<>();
    private static  final int ALL_ITEMS_LOADER_ID = 2;
    private Calendar currGreetingsEventCal;
    private ListView mListView;
    private ListView mHeadlinesListView;
    private TextView greetings_tv;
    private TextView weather_tv;
    private static ArrayList<NewsItem> newsList;
    private LinearLayout toDoItemsAndTitleView;
    private LinearLayout newsItemsAndTitleView;


    boolean flag;

    private RecognizeCallback mS2TCallback;
    private SpeechToTextHelper speechToTextHelper;
    private static TextToSpeechHelper textToSpeechHelper;
    private NewsHelper newsHelper;

    private FusedLocationProviderClient mFusedLocationClient;
    private static Weather weather;



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
          /*  case R.id.menuitem_singOut:
               pref.setUserLoggedIn(false);
               resetUserOptions();
                return true; */
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("curr_cal_key", currGreetingsEventCal);
        outState.putBoolean("flag_boolean", true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = new Preferences(getApplicationContext());
        //first time opening app, direct to log in
        boolean VisitedLogInStatus = pref.hasVisitedLogIn();
        if(!VisitedLogInStatus){
            startActivity(new Intent(GreetingsActivity.this, SignUpActivity.class));
        }
        setContentView(R.layout.activity_greetings);
        getSupportActionBar().setTitle("Havva PA");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(savedInstanceState != null){
            currGreetingsEventCal = (Calendar)savedInstanceState.getSerializable("curr_cal_key");
            flag = savedInstanceState.getBoolean("flag_boolean");
        }
        else {
            currGreetingsEventCal = Calendar.getInstance();
            flag =true;
        }

        //get API helpers
        speechToTextHelper = new SpeechToTextHelper();
        textToSpeechHelper = new TextToSpeechHelper();
        newsHelper = new NewsHelper();

        recordBtn = findViewById(R.id.record_btn_greetings);
        inputMessage = findViewById(R.id.transcribed_text_tv);
        toDoItemsAndTitleView = findViewById(R.id.to_do_title_and_items_box);
        newsItemsAndTitleView = findViewById(R.id.whats_new_title_and_items_box);

        mListView = findViewById(R.id.to_do_items_listView);
        newsList = new ArrayList<>();
        datasource = new ToDoItemsSource(getApplicationContext());

        //set username
        greetings_tv = findViewById(R.id.tv_greetings);
        addressUser();

        weather_tv = findViewById(R.id.weather_tv);
        greetings_tv = findViewById(R.id.tv_greetings);
        greetings_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(GreetingsActivity.this, SignUpActivity.class));
                return;
            }
        });

        recordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    speechToTextHelper.recordMessage(mS2TCallback, GreetingsActivity.this);
            }
        });

        toDoItemsAndTitleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(GreetingsActivity.this, ToDoActivity.class);
               startActivity(intent);
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(GreetingsActivity.this, ToDoActivity.class);
                startActivity(intent);
            }
        });

        newsItemsAndTitleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GreetingsActivity.this, NewsActivity.class));
            }
        });

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
                    speechToTextHelper.executeCommand(text,GreetingsActivity.this); // act on transcribed text
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
       mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
       mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            sendGETForWeather(location);
                        }
                    }


                });

        sendGET(newsHelper.getNewsByCategory(0));// load general news headlines
    }

    //Weather
    public void sendGETForWeather(Location location) {

        String url = WeatherHelper.BASE_URL+"lat="+location.getLatitude()+
                "&lon="+location.getLongitude()+API_KEY;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url,
                null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
               weather = WeatherHelper.parseWeatherResponse(response);
               weather_tv.setText(""+weather.getTemperature()+"°");
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

        RequestQueue queue = Volley.newRequestQueue(GreetingsActivity.this);
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


    @Override
    protected void onResume() {
        super.onResume();
        flag = true;
        getSupportLoaderManager().initLoader(ALL_ITEMS_LOADER_ID, null, this).forceLoad();
    }

    private String updateDateDisplay(Calendar dateTime)
    {
        String mSelectedDate = DateUtils.formatDateTime(GreetingsActivity.this, dateTime.getTimeInMillis(),DateUtils.FORMAT_SHOW_DATE);
        return mSelectedDate;
    }
    private String updateTimeDisplay(Calendar dateTime)
    {
        String mSelectedTime = DateUtils.formatDateTime(GreetingsActivity.this, dateTime.getTimeInMillis(),DateUtils.FORMAT_SHOW_TIME);
        return mSelectedTime;
    }


    @NonNull
    @Override
    public Loader<ArrayList<ToDoEntry>> onCreateLoader(int id, @Nullable Bundle args) {

        switch (id){
            case ALL_ITEMS_LOADER_ID:
                return new UpcomingToDoEntryListLoader(GreetingsActivity.this);
        }

        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<ToDoEntry>> loader, ArrayList<ToDoEntry> entities) {
        if(loader.getId() == ALL_ITEMS_LOADER_ID)
        {

            greetingsEventsAdapter = new GreetingsEventsAdapter(GreetingsActivity.this, updatedGreetingsEntries);
            mListView.setAdapter(greetingsEventsAdapter);
            mHeadlinesListView = findViewById(R.id.greetings_headlines_listView);
            mHeadlinesListView.setAdapter(greetingsHeadlinesAdapter);
            mHeadlinesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    startActivity(new Intent(GreetingsActivity.this, NewsActivity.class));
                }
            });

            if(entities!=null&&entities.size() > 0)
            {
                allEntries = entities;
                ArrayList<GreetingsToDoEntry> greetingsToDoEntries = new ArrayList<>();
                for(ToDoEntry toDoEntry : entities)
                {
                    GreetingsToDoEntry currGreetingsEvent;
                    String startTime = updateTimeDisplay(toDoEntry.getStartDateTime());
                    String endTime = updateTimeDisplay(toDoEntry.getEndDateTime());
                    String eventStartDate = updateDateDisplay(toDoEntry.getStartDateTime());
                    int currEventStartDate = toDoEntry.getStartDateTime().get(Calendar.DAY_OF_MONTH);

                    String eventTitle = toDoEntry.getEventTitle();
                    String eventLocation = toDoEntry.getEventLocation();

                    if(flag)
                    {
                        currGreetingsEvent = new GreetingsToDoEntry(eventStartDate,eventTitle + "  -  " + eventLocation, startTime + "  -  " + endTime);
                        currGreetingsEventCal.setTimeInMillis(toDoEntry.getStartDateTime().getTimeInMillis());
                    }
                    else {

                        if(currEventStartDate == currGreetingsEventCal.get(Calendar.DAY_OF_MONTH)){
                            currGreetingsEvent = new GreetingsToDoEntry("",eventTitle + "  -  " + eventLocation, startTime + "  -  " + endTime);

                        }
                        else {
                            currGreetingsEvent = new GreetingsToDoEntry(eventStartDate,eventTitle + "  -  " + eventLocation, startTime + "  -  " + endTime);

                        }

                    }
                    flag = false;

                    currGreetingsEventCal.setTimeInMillis(toDoEntry.getStartDateTime().getTimeInMillis());
                    greetingsToDoEntries.add(currGreetingsEvent);



                }
                updatedGreetingsEntries = greetingsToDoEntries;
                greetingsEventsAdapter.setGreetingsEntriesList(updatedGreetingsEntries);
                greetingsEventsAdapter.notifyDataSetChanged();


                //updatedToDoItemEnries = toDoEntriesPerScheduledEvent;
                // mToDoListAdapter.setCalendarItems(updatedToDoItemEnries);
                // mToDoListAdapter.notifyDataSetChanged();
            }
            else {
                greetingsEventsAdapter.clear();
                greetingsEventsAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<ToDoEntry>> loader) {
        if(loader.getId()==ALL_ITEMS_LOADER_ID){
            greetingsEventsAdapter.clear();
            greetingsEventsAdapter.notifyDataSetChanged();
        }
    }

    public void resetUserOptions(){
        //user signed out, clear his name and alarm settings

    }

    public void addressUser(){
        if(!pref.isHavvaMute()){
            if(!pref.getUsername().isEmpty()) { //write user's name
            greetings_tv.setText("Hi, " + pref.getUsername());
             }
        //say hi to user
            textToSpeechHelper.readAloud(greetings_tv.getText().toString());
        }

    }

    public static void tellWeather(){
        if(!pref.isHavvaMute()) {
            if (!weather.getDecscription().isEmpty())
                textToSpeechHelper.readAloud(" weather forecast for today is" + weather.getDecscription());
            if (!Integer.toString(weather.getTemperature()).isEmpty())
                textToSpeechHelper.readAloud("It is " + weather.getTemperature() + "degrees Fahrenheits");
        }
    }

    public static void readNewsAloud(){
        for (NewsItem news : newsList){
            textToSpeechHelper.readAloud("Next up,");
            textToSpeechHelper.readAloud(news.getTitle());

        }
    }

    public void sendGET(String url) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                newsList = newsHelper.parseResponse(response);
                greetingsHeadlinesAdapter = new GreetingsHeadlinesAdapter(getApplicationContext(),R.id.news_item_greetings, newsList);
                if( mHeadlinesListView == null){
                    mHeadlinesListView = findViewById(R.id.greetings_headlines_listView);
                }
                mHeadlinesListView.setAdapter(greetingsHeadlinesAdapter);

            }
        },new Response.ErrorListener() {
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

   /* public void refreshView(){
        mNewsAdapter = new NewsListAdapter(this,R.layout.news_item,newsList);
        listView.setAdapter(mNewsAdapter);
    }

    public void setUpView()    {
        //mNewsAdapter = new NewsListAdapter(this,R.layout.news_item,newsList);
        listView = findViewById(R.id.news_list_NDA);

    }*/
}

