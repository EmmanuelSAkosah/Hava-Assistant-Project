package edu.dartmouth.cs.havvapa.APIs;

import android.location.Location;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.dartmouth.cs.havvapa.models.Weather;

public class WeatherHelper {

    public static String BASE_URL = "http://api.openweathermap.org/data/2.5/weather?";
    private String TAG = "WeatherHelper";
    public static String API_KEY ="&APPID=db2e34c6ec913c5702ec1eb59a3ed321";

    public WeatherHelper(){

    }

    public static Weather parseWeatherResponse(JSONObject weatherAPIResponse){

        Weather weather = new Weather();
        // We get weather info (This is an array)
        try {
            JSONArray jArr = weatherAPIResponse.getJSONArray("weather");

            // We use only the first value
            JSONObject JSONWeather = jArr.getJSONObject(0);
            //weather.setCondition(JSONWeather.getString("main"));
            weather.setDescription(JSONWeather.getString("description"));
            weather.setIcon(JSONWeather.getString("icon"));

            JSONObject mainObj = weatherAPIResponse.getJSONObject("main");
            //weather.currentCondition.setHumidity(getInt("humidity", mainObj));
            //weather.currentCondition.setPressure(getInt("pressure", mainObj));
            //weather.temperature.setMaxTemp(getFloat("temp_max", mainObj));
            //weather.temperature.setMinTemp(getFloat("temp_min", mainObj));
            weather.setTemperature(mainObj.getInt("temp"));

            // Wind
           // JSONObject wObj = getObject("wind", jObj);
            //weather.wind.setSpeed(getFloat("speed", wObj));
            //weather.wind.setDeg(getFloat("deg", wObj));

            // Clouds
           // weather.setClouds(weatherAPIResponse.getJSONObject("clouds").getInt("all"));


            return weather;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


}
