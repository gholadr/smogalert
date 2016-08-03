package co.ghola.smogalert.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.survivingwithandroid.weather.lib.util.LogUtils;
import com.survivingwithandroid.weather.lib.util.WindDirection;

import co.ghola.smogalert.IconMapper.WeatherIconMapper;
import co.ghola.smogalert.IconMapper.WeatherUtil;
import co.ghola.smogalert.R;

/**
 * Created by alecksjohansson on 8/1/16.
 */
public class WeatherFragment extends Fragment {
    // Store instance variables
    private String title;
    private int page;
    WeatherClient weatherClient;
    String cityId= "1851632";
    WeatherRequest req = new WeatherRequest(cityId);



    // newInstance constructor for creating fragment with arguments
    public static WeatherFragment newInstance(int page, String title) {
        WeatherFragment mWeatherFragment= new WeatherFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        mWeatherFragment.setArguments(args);
        return mWeatherFragment;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");

    }


    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fouth_fragment, container, false);
        final TextView cityText = (TextView) v.findViewById(R.id.location);
        final TextView hum = (TextView) v.findViewById(R.id.humidity);
        final TextView press = (TextView) v.findViewById(R.id.pressure);
        final TextView windSpeed = (TextView) v.findViewById(R.id.windSpeed);
        final TextView windDeg = (TextView) v.findViewById(R.id.windDeg);
        final TextView tempMin = (TextView) v.findViewById(R.id.tempMin);
        final TextView tempMax = (TextView) v.findViewById(R.id.tempMax);
        final TextView sunset = (TextView) v.findViewById(R.id.sunset);
        final TextView sunRise = (TextView) v.findViewById(R.id.sunrise);
        final Handler handler = new Handler();
        final Runnable thisThread = new Runnable() {
            @Override
            public void run() {
               handler.postDelayed(this,15000);
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
                    e.printStackTrace();
                }
                client.updateWeatherConfig(config);
                client.getCurrentCondition(new WeatherRequest("1566083"), new WeatherClient.WeatherEventListener() {
                    @Override
                    public void onWeatherRetrieved(CurrentWeather currentWeather) {
                        Weather weather = currentWeather.weather;
                        cityText.setText(weather.location.getCity() + "," + weather.location.getCountry());
                        LogUtils.LOGD("SwA", "Val [" + weather.temperature.getTemp() + "]");
                        Log.d("test", "test" + cityText);
                        hum.setText(weather.currentCondition.getHumidity() + "%");
                        tempMin.setText(weather.temperature.getMinTemp() + currentWeather.getUnit().tempUnit);
                        tempMax.setText(weather.temperature.getMaxTemp() + currentWeather.getUnit().tempUnit);
                        windSpeed.setText(weather.wind.getSpeed() + currentWeather.getUnit().speedUnit);
                        windDeg.setText((int) weather.wind.getDeg() + "° (" + WindDirection.getDir((int) weather.wind.getDeg()) + ")");
                        press.setText(weather.currentCondition.getPressure() + currentWeather.getUnit().pressureUnit);
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
            }
        };
        thisThread.run();
        return v;
    }
}
