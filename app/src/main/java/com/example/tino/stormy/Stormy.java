package com.example.tino.stormy;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.net.ConnectivityManagerCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Stormy extends AppCompatActivity {
    ////////////////////////////////////////////////////////////////
    // TKO OVO CITA USPJESNO JE PULLAO                          ////
    ////////////////////////////////////////////////////////////////

    public static final String TAG = Stormy.class.getSimpleName();

    private CurrentWeather mCurrentWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stormy);

        String apiKey = "61bcf33cb24e1247b2e60b9a6f16109b";
        double latitude = 37.8267;
        double longitude = -122.4233;
        String forecastUrl = "https://api.darksky.net/forecast/" + apiKey +
                "/" + latitude + "," + longitude;

        if (isNetworkAvailable()) {

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(forecastUrl)
                    .build();
            okhttp3.Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {

                }

                @Override
                public void onResponse(okhttp3.Call call, Response response) throws IOException {
                    try {
                        String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            mCurrentWeather = getCurrentDetails(jsonData);
                        } else {
                            alertUserAboutError();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Exception caught;", e);
                    }
                    catch (JSONException e) {
                        Log.e(TAG, "Exception caught;", e);
                    }
                }
            });
        } else {
            Toast.makeText(this, R.string.network_unvailable_message, Toast.LENGTH_LONG).show();
        }

        Log.d(TAG, "Main UI code is running");

    }

    private CurrentWeather getCurrentDetails(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        Log.i(TAG, "From JSON" + timezone);

        JSONObject currently = forecast.getJSONObject("currently");

        CurrentWeather currentWeather = new CurrentWeather();
        currentWeather.setHumidity(currently.getDouble("humidity"));
        currentWeather.setTime(currently.getLong("time"));
        currentWeather.setIcon(currently.getString("icon"));
        currentWeather.setPrecipChance(currently.getDouble("precipProbability"));
        currentWeather.setSummary(currently.getString("summary"));
        currentWeather.setTemp(currently.getDouble("temperature"));
        currentWeather.setTimeZone(timezone);

        Log.d(TAG, currentWeather.getFormattedTime());

        return currentWeather;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()){
            isAvailable = true;
        }
        return isAvailable;
    }

    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getSupportFragmentManager(), "error_dialog");

    }
}
