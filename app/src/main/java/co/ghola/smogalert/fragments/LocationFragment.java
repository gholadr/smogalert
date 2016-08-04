package co.ghola.smogalert.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.survivingwithandroid.weather.lib.WeatherClient;
import com.survivingwithandroid.weather.lib.WeatherConfig;
import com.survivingwithandroid.weather.lib.client.okhttp.WeatherDefaultClient;
import com.survivingwithandroid.weather.lib.exception.WeatherLibException;
import com.survivingwithandroid.weather.lib.exception.WeatherProviderInstantiationException;
import com.survivingwithandroid.weather.lib.model.CurrentWeather;
import com.survivingwithandroid.weather.lib.model.Weather;
import com.survivingwithandroid.weather.lib.provider.IWeatherProvider;
import com.survivingwithandroid.weather.lib.provider.WeatherProviderFactory;
import com.survivingwithandroid.weather.lib.provider.forecastio.ForecastIOProviderType;
import com.survivingwithandroid.weather.lib.provider.openweathermap.OpenweathermapProviderType;
import com.survivingwithandroid.weather.lib.provider.yahooweather.YahooProviderType;
import com.survivingwithandroid.weather.lib.request.WeatherRequest;
import com.survivingwithandroid.weather.lib.util.LogUtils;
import com.survivingwithandroid.weather.lib.util.WeatherUtility;
import com.survivingwithandroid.weather.lib.util.WindDirection;

import co.ghola.smogalert.IconMapper.WeatherIconMapper;
import co.ghola.smogalert.IconMapper.WeatherUtil;
import co.ghola.smogalert.R;

/**
 * Created by alecksjohansson on 7/21/16.
 */
public class LocationFragment extends Fragment {
    // Store instance variables
    private String title;
    private int page;
    String cityId= "1851632";
    WeatherRequest req = new WeatherRequest(cityId);



    // newInstance constructor for creating fragment with arguments
    public static LocationFragment newInstance(int page, String title) {
        LocationFragment mLocationFragment= new LocationFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        mLocationFragment.setArguments(args);
        return mLocationFragment;
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
        final View v = inflater.inflate(R.layout.third_fragment, container, false);
        final TextView cityText = (TextView) v.findViewById(R.id.location);
        final TextView temp = (TextView) v.findViewById(R.id.temp);
        final TextView condDescr = (TextView) v.findViewById(R.id.descrWeather);
        final TextView unitTemp = (TextView) v.findViewById(R.id.tempUnit);
        final ImageView imgView = (ImageView) v.findViewById(R.id.imgWeather);
        final TextView colorTextLine = (TextView) v.findViewById(R.id.lineTxt);
        final Typeface mTypeFace  = Typeface.createFromAsset(getActivity().getAssets(),"fonts/RobotoCondensed-Regular.ttf");
        final WeatherConfig config = new WeatherConfig();
        config.unitSystem = WeatherConfig.UNIT_SYSTEM.M;
        config.lang = "en";
        config.maxResult = 5;
        config.numDays = 6;
        config.ApiKey = "fc7ebfbabac03248af36d7adb9244b0b";
        final Handler handler = new Handler();
        final Runnable thisThread = new Runnable() {
            @Override
            public void run() {
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
                handler.postDelayed(this,20000);
                client.updateWeatherConfig(config);
                client.getCurrentCondition(new WeatherRequest("1566083"), new WeatherClient.WeatherEventListener() {
                    @Override
                    public void onWeatherRetrieved(CurrentWeather currentWeather) {
                        Weather weather = currentWeather.weather;
                        cityText.setTypeface(mTypeFace);
                        cityText.setText(weather.location.getCity() + "," + weather.location.getCountry());
                        condDescr.setText(weather.currentCondition.getCondition() + "(" + weather.currentCondition.getDescr() + ")");
                        LogUtils.LOGD("SwA", "Val [" + weather.temperature.getTemp() + "]");
                        temp.setText("" + ((int) weather.temperature.getTemp()));
                        unitTemp.setText(currentWeather.getUnit().tempUnit);
                        colorTextLine.setBackgroundResource(WeatherUtil.getResource(weather.temperature.getTemp(), config));
                        imgView.setImageResource(WeatherIconMapper.getWeatherResource(weather.currentCondition.getIcon(), weather.currentCondition.getWeatherId()));
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

