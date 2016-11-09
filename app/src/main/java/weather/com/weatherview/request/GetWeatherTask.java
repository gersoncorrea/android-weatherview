package weather.com.weatherview.request;

import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import weather.com.weatherview.R;
import weather.com.weatherview.adapter.WeatherAdapter;
import weather.com.weatherview.model.Weather;

/**
 * Makes Rest web service call
 * Created by gerson on 10/6/16.
 */

public class GetWeatherTask extends AsyncTask<URL, Void, JSONObject> {
    CoordinatorLayout coordinatorLayout;
    WeatherAdapter weatherAdapter;
    List<Weather> weatherList = new ArrayList<>();
    ListView weatherListView;

    /**
     * Constructor of class
     * @param coordinatorLayout
     * @param weatherAdapter
     * @param weatherList
     * @param weatherListView
     */
    public GetWeatherTask(CoordinatorLayout coordinatorLayout, WeatherAdapter weatherAdapter, List<Weather> weatherList, ListView weatherListView) {
        this.coordinatorLayout = coordinatorLayout;
        this.weatherAdapter = weatherAdapter;
        this.weatherList = weatherList;
        this.weatherListView = weatherListView;
    }


    @Override
    protected JSONObject doInBackground(URL... params) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) params[0].openConnection();
            int response = connection.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                StringBuilder builder = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()))
                ) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                } catch (IOException e) {
                    Snackbar
                            .make(coordinatorLayout, R.string.read_error, Snackbar.LENGTH_LONG)
                            .show();
                    e.printStackTrace();
                }
                return new JSONObject(builder.toString());
            } else {
                Snackbar
                        .make(coordinatorLayout, R.string.read_error, Snackbar.LENGTH_LONG)
                        .show();
            }

        } catch (Exception e) {
            Snackbar
                    .make(coordinatorLayout, R.string.connect_error, Snackbar.LENGTH_LONG)
                    .show();
            e.printStackTrace();
        } finally {
            // close connection
            connection.disconnect();
        }
        return null;
    }

    /**
     * Process response and update ListView
     *
     * @param weather
     */
    @Override
    protected void onPostExecute(JSONObject weather) {
        convertJSONtoArrayList(weather);
        weatherAdapter.notifyDataSetChanged();
        weatherListView.smoothScrollToPosition(0);
    }

    /**
     * @param forecast
     */
    private void convertJSONtoArrayList(JSONObject forecast) {
        weatherList.clear(); //clear old data
        try {
            JSONArray list = forecast.getJSONArray("list");

            for (int i = 0; i < list.length(); ++i) {
                JSONObject day = list.getJSONObject(i);
                JSONObject temperatures = day.getJSONObject("temp");
                JSONObject weather = day
                        .getJSONArray("weather")
                        .getJSONObject(0);

                /**
                 * Add weather object
                 */
                weatherList.add(new Weather(
                        day.getLong("dt"),
                        temperatures.getDouble("min"),
                        temperatures.getDouble("max"),
                        day.getDouble("humidity"),
                        weather.getString("description"),
                        weather.getString("icon")
                ));
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
