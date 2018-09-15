package com.devodriqroberts.storms.Views;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.devodriqroberts.storms.R;
import com.devodriqroberts.storms.Models.CurrentWeather;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {


    private static final String KEY = "441ab0b3be13f34c82e80dc9fe018506";
    private static final String TAG = MainActivity.class.getSimpleName();
    private CurrentWeather currentWeather;
    private String forcastUrl;
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        latitude = 42.3601;
        longitude = -71.0589;

        forcastUrl = String.format(getString(R.string.darksky_url), KEY, latitude, longitude);

        if (isNetworkAvailable()) {

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(forcastUrl)
                    .build();

            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    try {
                        String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {

                            currentWeather = getCurrentDetails(jsonData);
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

    private CurrentWeather getCurrentDetails(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        JSONObject currently = forecast.getJSONObject("currently");

        String timezone = forecast.getString("timezone");

        String icon = currently.getString("icon");
        String summary = currently.getString("summary");
        double temperature = currently.getDouble("temperature");
        double humidity = currently.getDouble("humidity");
        double precipProbability = currently.getDouble("precipProbability");
        long time = currently.getLong("time");



        currentWeather = new CurrentWeather("Alcatraz Island, CA", icon, time, temperature, humidity, precipProbability, summary, timezone);

        Log.d(TAG, currentWeather.getFormattedTime());

        return currentWeather;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
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

}
