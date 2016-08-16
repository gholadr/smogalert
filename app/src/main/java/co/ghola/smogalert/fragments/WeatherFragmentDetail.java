package co.ghola.smogalert.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.survivingwithandroid.weather.lib.WeatherClient;
import com.survivingwithandroid.weather.lib.WeatherConfig;
import com.survivingwithandroid.weather.lib.client.okhttp.WeatherDefaultClient;
import com.survivingwithandroid.weather.lib.exception.WeatherLibException;
import com.survivingwithandroid.weather.lib.exception.WeatherProviderInstantiationException;
import com.survivingwithandroid.weather.lib.model.CurrentWeather;
import com.survivingwithandroid.weather.lib.model.Weather;
import com.survivingwithandroid.weather.lib.provider.openweathermap.OpenweathermapProviderType;
import com.survivingwithandroid.weather.lib.request.WeatherRequest;

import java.util.HashMap;
import java.util.Map;

import co.ghola.smogalert.IconMapper.WeatherUtil;
import co.ghola.smogalert.R;

/**
 * Created by alecksjohansson on 8/1/16.
 */
public class WeatherFragmentDetail extends Fragment {
    // Store instance variables
    private String title;
    private int page;
    WeatherClient weatherClient;
    String cityId= "1851632";
    WeatherRequest req = new WeatherRequest(cityId);
    private TextView cityText;
    private TextView hum;
    private TextView windSpeed;
    private TextView tempMin;
    private TextView tempMax;
    private TextView sunRise;
    private TextView sunset;

    // newInstance constructor for creating fragment with arguments
    public static WeatherFragmentDetail newInstance(int page, String title) {
        WeatherFragmentDetail mWeatherFragmentDetail = new WeatherFragmentDetail();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        mWeatherFragmentDetail.setArguments(args);
        return mWeatherFragmentDetail;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");

    }
//    private Boolean isNetworkAvailable() {
//        ConnectivityManager connectivityManager
//                = (ConnectivityManager) getActivity().getSystemService(getContext().CONNECTIVITY_SERVICE);
//        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
//        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
//    }
//    public boolean isOnline() {
//        Runtime runtime = Runtime.getRuntime();
//        try {
//            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
//            int     exitValue = ipProcess.waitFor();
//            return (exitValue == 0);
//        } catch (IOException e)          { e.printStackTrace(); }
//        catch (InterruptedException e) { e.printStackTrace(); }
//        return false;
//    }


    private class WeatherOperationAsyncTask extends AsyncTask<Void, Void , HashMap>
    {
        Map bagOfTricks = null;

        @Override
        protected HashMap doInBackground(Void... params) {
            final WeatherConfig config = new WeatherConfig();
            config.unitSystem = WeatherConfig.UNIT_SYSTEM.M;
            config.lang = "en";
            config.maxResult = 5;
            config.numDays = 6;
            config.ApiKey = "fc7ebfbabac03248af36d7adb9244b0b";
            WeatherClient.ClientBuilder builder = new WeatherClient.ClientBuilder();
            WeatherClient client = null;
            try {
                client = builder.attach(getActivity())
                        .provider(new OpenweathermapProviderType())
                        .httpClient(WeatherDefaultClient.class)
                        .config(new WeatherConfig())
                        .build();
            } catch (WeatherProviderInstantiationException e) {
                Log.e(WeatherFragmentDetail.class.getSimpleName(),"WeatherProviderInstantiationException", e.fillInStackTrace());
            }
            client.updateWeatherConfig(config);
            client.getCurrentCondition(new WeatherRequest("1566083"), new WeatherClient.WeatherEventListener() {
                @Override
                public void onWeatherRetrieved(final CurrentWeather currentWeather) {
                    Weather weather = currentWeather.weather;
                    bagOfTricks = new HashMap<>();

                    bagOfTricks.put("city", weather.location.getCity() + "," + weather.location.getCountry());
                //    cityText.setText(weather.location.getCity() + "," + weather.location.getCountry());
                    hum.setText(weather.currentCondition.getHumidity() + "%");
                    tempMin.setText(weather.temperature.getMinTemp() + currentWeather.getUnit().tempUnit);
                    tempMax.setText(weather.temperature.getMaxTemp() + currentWeather.getUnit().tempUnit);
                    windSpeed.setText(weather.wind.getSpeed() + currentWeather.getUnit().speedUnit);
                    sunset.setText(WeatherUtil.convertDate(weather.location.getSunset()));
                    sunRise.setText(WeatherUtil.convertDate(weather.location.getSunrise()));
                        }

                @Override
                public void onConnectionError(Throwable t) {
                    Log.d("WL", "Connection Error - parsing data");
                }

                @Override
                public void onWeatherError(WeatherLibException wle) {
                    Log.d("WL", "Weather Error - parsing data");
                    wle.printStackTrace();
                }
            });
            return (HashMap) bagOfTricks;
        }

        @Override
        public void onPostExecute(HashMap result) {
            //TODO - move doInBackground UI stuff here
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        new WeatherOperationAsyncTask().execute();
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(WeatherOperationAsyncTask.class.getSimpleName(), "onCreateView");
        final View v = inflater.inflate(R.layout.weather_fragment_detail, container, false);
       // cityText = (TextView) v.findViewById(R.id.location);
        hum = (TextView) v.findViewById(R.id.humidity);
        windSpeed = (TextView) v.findViewById(R.id.windSpeed);
        tempMin = (TextView) v.findViewById(R.id.tempMin);
        tempMax = (TextView) v.findViewById(R.id.tempMax);
        sunset = (TextView) v.findViewById(R.id.sunset);
        sunRise = (TextView) v.findViewById(R.id.sunrise);
//        if(isNetworkAvailable() )
//        {
//            Toast.makeText(getActivity(),"Connected to Network",Toast.LENGTH_SHORT).show();
//            if(isOnline())
//            {
//                Toast.makeText(getActivity(),"Getting Data from Weather Server",Toast.LENGTH_SHORT).show();
//                new WeatherOperation().execute();
//                Log.d("RUN","RUNDAWEATHER");
//            }
//            else
//            {
//                Toast.makeText(getActivity(),"Internet something wrong with the Internet",Toast.LENGTH_SHORT).show();
//                cityText.setText(getResources().getString(R.string.loading));
//            }
//
//        }
//        else
//        {
//            Toast.makeText(getActivity(),"Please connect to the Wi-Fi",Toast.LENGTH_SHORT).show();
//            cityText.setText(getResources().getString(R.string.loading));
//        }
        return v;
    }

}
