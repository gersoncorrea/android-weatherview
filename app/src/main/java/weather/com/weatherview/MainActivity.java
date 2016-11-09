package weather.com.weatherview;

import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import weather.com.weatherview.adapter.WeatherAdapter;
import weather.com.weatherview.model.Weather;

public class MainActivity extends AppCompatActivity {
    private List<Weather> weatherList = new ArrayList<>();
    CoordinatorLayout coordinatorLayout;
    private WeatherAdapter weatherAdapter;
    private ListView weatherListView;

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();

    }

    private void initialize(){
        weatherListView = (ListView)findViewById(R.id.weatherListView);
        weatherAdapter = new WeatherAdapter(this,weatherList);
        weatherListView.setAdapter(weatherAdapter);

        FloatingActionButton fab =
                (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText locationEditText = (EditText)findViewById(R.id.locationEditText);
                URL url = createURL(locationEditText.getText().toString());

                if(url != null){
                    dismissKeyboard(locationEditText);
                    coordinatorLayout = (CoordinatorLayout)findViewById(R.id.activity_main);

                    weather.com.weatherview.request.GetWeatherTask getWeatherTask =
                            new weather.com.weatherview.request.GetWeatherTask(coordinatorLayout,weatherAdapter,weatherList,weatherListView);
//                    GetWeatherTask getWeatherTask =
//                            new GetWeatherTask();
                            getWeatherTask.execute(url);
                } else{
                    Snackbar
                            .make(findViewById(R.id.activity_main),R.string.invalid_url,Snackbar.LENGTH_LONG)
                            .show();
                }
            }
        });
    }

    /**
     * Dismiss keyboard when user touches FAB
     * @param view
     */
    private void dismissKeyboard(View view){
        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }


    /**
     *  Create url using city
     * @param city
     * @return
     */
    private URL createURL(String city){
        String apiKey = getString(R.string.api_key);
        String baseUrl = getString(R.string.web_service_url);
        try{
            String urlString = baseUrl + URLEncoder.encode(city,"UTF-8")
                    + "&units=imperial&cnt=16&APPID=" + apiKey;
            return new URL(urlString);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private class GetWeatherTask extends AsyncTask<URL,Void,JSONObject>{

        @Override
        protected JSONObject doInBackground(URL... params) {
            HttpURLConnection connection = null;
            try{
                connection = (HttpURLConnection)params[0].openConnection();
                int response = connection.getResponseCode();
                if(response == HttpURLConnection.HTTP_OK){
                    StringBuilder builder = new StringBuilder();
                    try(BufferedReader reader
                                = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                        String line;
                        while((line = reader.readLine()) != null){
                            builder.append(line);
                        }
                    } catch (IOException e){
                        Snackbar
                                .make(findViewById(R.id.conditionImageView), R.string.read_error,Snackbar.LENGTH_LONG)
                                .show();
                        e.printStackTrace();
                    }
                    return new JSONObject(builder.toString());
                } else {
                    Snackbar
                            .make(findViewById(R.id.activity_main),R.string.read_error,Snackbar.LENGTH_LONG)
                            .show();
                }

            } catch (Exception e){
                Snackbar
                        .make(findViewById(R.id.activity_main),R.string.connect_error,Snackbar.LENGTH_LONG)
                        .show();
            } finally {
                connection.disconnect();
            }
            return null;
        }

        /**
         * Process response and update ListView
         * @param weather
         */
        @Override
        protected void onPostExecute(JSONObject weather) {
            convertJSONtoArrayList(weather);
            weatherAdapter.notifyDataSetChanged();
            weatherListView.smoothScrollToPosition(0);
        }

        /**
         *
         * @param forecast
         */
        private void convertJSONtoArrayList(JSONObject forecast){
            weatherList.clear(); //clear old data
            try {
                JSONArray list = forecast.getJSONArray("list");

                for (int i = 0; i < list.length(); ++i){
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
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }
}
