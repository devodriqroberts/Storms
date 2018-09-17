package com.devodriqroberts.storms.Views;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.devodriqroberts.storms.Models.Current;
import com.devodriqroberts.storms.Models.Forecast;
import com.devodriqroberts.storms.Models.Hourly;
import com.devodriqroberts.storms.R;
import com.devodriqroberts.storms.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {


    private static final String KEY = "441ab0b3be13f34c82e80dc9fe018506";
    private static final String TAG = MainActivity.class.getSimpleName();
    private Forecast forecast;
    private ImageView iconImageView;
    private final double latitude = 42.3601;
    private final double longitude = -71.0589;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getForcast(latitude, longitude);
    }

    private void getForcast(double latitude, double longitude) {
        final ActivityMainBinding binding = DataBindingUtil.setContentView(MainActivity.this, R.layout.activity_main);

        // Create link for Dark Sky message
        TextView darkSky = findViewById(R.id.darkSkyAttribution);
        darkSky.setMovementMethod(LinkMovementMethod.getInstance());

        iconImageView = findViewById(R.id.iconImageView);


        String forcastUrl = String.format(getString(R.string.darksky_url), KEY, latitude, longitude);

        if (isNetworkAvailable()) {

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(forcastUrl)
                    .build();

            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    try {
                        String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {

                            forecast = parseForecastData(jsonData);

                            Current current = forecast.getCurrent();

                            final Current displayWeather = new Current(
                                    current.getLocationLabel(),
                                    current.getIcon(),
                                    current.getTime(),
                                    current.getTemperature(),
                                    current.getHumidity(),
                                    current.getPrecipProbability(),
                                    current.getSummary(),
                                    current.getTimeZone()
                            );

                            binding.setWeather(displayWeather);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Drawable drawable = getResources().getDrawable(displayWeather.getIconId());
                                    iconImageView.setImageDrawable(drawable);
                                }
                            });



                        } else {
                            alertUserAboutError();
                        }
                    } catch (IOException ioe) {
                        Log.e(TAG, "IO Exception caught: ", ioe);
                    } catch (JSONException jsone) {
                        Log.e(TAG, "JSON Exception caught: ", jsone);
                    }
                }
            });
        }
    }

    private Forecast parseForecastData(String jsonData) throws JSONException {



        Forecast forecast = new Forecast(getCurrentDetails(jsonData), getHourlyDetails(jsonData));



        return forecast;
    }

    private Hourly[] getHourlyDetails(String jsonData) throws JSONException {
        // Main data object
        JSONObject forecast = new JSONObject(jsonData);
        String forecastTimeZone = forecast.getString("timezone");

        // Hourly object within main data object
        JSONObject hourlyObject = forecast.getJSONObject("hourly");
        String hourlySummary = hourlyObject.getString("summary");
        String hourlyIcon = hourlyObject.getString("icon");

        // Hourly data objects within hourly data array
        JSONArray hourlyData = hourlyObject.getJSONArray("data");
        Hourly[] hourlyForcast = new Hourly[hourlyData.length()];

        for (int i = 0; i < hourlyData.length(); i++) {
            JSONObject jsonHour = hourlyData.getJSONObject(i);
            Hourly hourly = new Hourly(
                    jsonHour.getLong("time"),
                    jsonHour.getString("summary"),
                    jsonHour.getDouble("temperature"),
                    jsonHour.getString("icon")
            );

            hourlyForcast[i] = hourly;
        }

        return hourlyForcast;
    }

    private Current getCurrentDetails(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        JSONObject currently = forecast.getJSONObject("currently");

        String timezone = forecast.getString("timezone");

        String icon = currently.getString("icon");
        String summary = currently.getString("summary");
        double temperature = currently.getDouble("temperature");
        double humidity = currently.getDouble("humidity");
        double precipProbability = currently.getDouble("precipProbability");
        long time = currently.getLong("time");


        Current current = new Current("Alcatraz Island, CA", icon, time, temperature, humidity, precipProbability, summary, timezone);

        Log.d(TAG, current.getFormattedTime());

        return current;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert manager != null;
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;

        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        } else {
            Toast.makeText(this, R.string.network_unavailable_text, Toast.LENGTH_LONG).show();
        }

        return  isAvailable;
    }

    private void alertUserAboutError() {
    AlertDialogFragment dialog = new AlertDialogFragment();
    dialog.show(getFragmentManager(), "error_dialog");
    }

    public void refreshOnClick(View view) {
        Toast.makeText(this, "Refreshing Data", Toast.LENGTH_LONG).show();
        getForcast(latitude, longitude);
    }

}
