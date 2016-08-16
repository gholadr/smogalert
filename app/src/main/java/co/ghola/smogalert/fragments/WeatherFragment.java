package co.ghola.smogalert.fragments;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.io.IOException;

import co.ghola.smogalert.IconMapper.WeatherIconMapper;
import co.ghola.smogalert.IconMapper.WeatherUtil;
import co.ghola.smogalert.R;
import co.ghola.smogalert.utils.Constants;

/**
 * Created by alecksjohansson on 7/21/16.
 */
public class WeatherFragment extends Fragment {
    // Store instance variables
//    private String title;
//    private int page;
    String cityId= "1851632";
    WeatherRequest req = new WeatherRequest(cityId);
    private TextView noData;
    private TextView condDescr;
    private TextView temp;
    private TextView unitTemp;
    private ImageView imgView;
    private TextView colorTextLine;
    private LinearLayout layout;



    // newInstance constructor for creating fragment with arguments
    public static WeatherFragment newInstance(int page, String title) {
        WeatherFragment mWeatherFragment = new WeatherFragment();
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
        EventBus.getDefault().register(this);
//        page = getArguments().getInt("someInt", 0);
//        title = getArguments().getString("someTitle");

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
         View v = inflater.inflate(R.layout.weather_fragment, container, false);
         noData= (TextView) v.findViewById(R.id.no_data);
         temp = (TextView) v.findViewById(R.id.temp);
         unitTemp = (TextView) v.findViewById(R.id.tempUnit);
         imgView = (ImageView) v.findViewById(R.id.imgWeather);
         colorTextLine = (TextView) v.findViewById(R.id.lineTxt);
         layout = (LinearLayout) v.findViewById(R.id.weather_box);
        return v;
    }
    private class WeatherOperationAsyncTask extends AsyncTask<Void, Void , String>
    {
        @Override
        protected String doInBackground(Void... params) {
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
                    //                  ViewGroup container = (ViewGroup) getView().findViewById(R.id.weather);
                    //                View viewer= LayoutInflater.from(getContext()).inflate(R.layout.weather_fragment,container);
                    Weather weather = currentWeather.weather;
//                    cityText.setText(weather.location.getCity() + "," + weather.location.getCountry());
                    temp.setText("" + ((int) weather.temperature.getTemp()));
                    unitTemp.setText(currentWeather.getUnit().tempUnit);
                    colorTextLine.setBackgroundResource(WeatherUtil.getResource(weather.temperature.getTemp(), config));
                    imgView.setImageResource(WeatherIconMapper.getWeatherResource(weather.currentCondition.getIcon(), weather.currentCondition.getWeatherId()));
                    noData.setVisibility(View.INVISIBLE);
                    layout.setVisibility(View.VISIBLE);
                }


                @Override
                public void onConnectionError(Throwable t) {
                    Log.e("WL", "Connection Error - parsing data");
                    noData.setVisibility(View.VISIBLE);
                    layout.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onWeatherError(WeatherLibException wle) {
                    Log.e("WL", "Weather Error - parsing data");
                    noData.setVisibility(View.VISIBLE);
                    layout.setVisibility(View.INVISIBLE);
                }
            });
            return null;
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void doThis(Boolean online) {
        new WeatherOperationAsyncTask().execute();
    }
}

