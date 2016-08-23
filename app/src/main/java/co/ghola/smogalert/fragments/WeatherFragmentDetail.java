package co.ghola.smogalert.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
    String cityId= "1851632";
    WeatherRequest req = new WeatherRequest(cityId);
    private TextView hum;
    private TextView windSpeed;
    private TextView tempMin;
    private TextView tempMax;
    private TextView sunRise;
    private TextView sunset;
    private TextView noData;
    private RelativeLayout layout;

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
        EventBus.getDefault().register(this);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");

    }

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
                    noData.setVisibility(View.INVISIBLE);
                    layout.setVisibility(View.VISIBLE);
                        }

                @Override
                public void onConnectionError(Throwable t) {
                    Log.e(WeatherFragmentDetail.class.getSimpleName(), "Connection Error - parsing data");
                    noData.setVisibility(View.VISIBLE);
                    layout.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onWeatherError(WeatherLibException wle) {
                    Log.e(WeatherFragment.class.getSimpleName(), "Weather Error - parsing data");
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
        noData= (TextView) v.findViewById(R.id.no_data);
        layout = (RelativeLayout) v.findViewById(R.id.weather_box);
        hum = (TextView) v.findViewById(R.id.humidity);
        windSpeed = (TextView) v.findViewById(R.id.windSpeed);
        tempMin = (TextView) v.findViewById(R.id.tempMin);
        tempMax = (TextView) v.findViewById(R.id.tempMax);
        sunset = (TextView) v.findViewById(R.id.sunset);
        sunRise = (TextView) v.findViewById(R.id.sunrise);
        return v;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void doThis(Boolean online) {
        new WeatherOperationAsyncTask().execute();
    }

}
