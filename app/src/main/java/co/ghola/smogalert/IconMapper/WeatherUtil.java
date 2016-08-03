package co.ghola.smogalert.IconMapper;

import com.survivingwithandroid.weather.lib.WeatherConfig;
import com.survivingwithandroid.weather.lib.util.UnitUtility;
import com.survivingwithandroid.weather.lib.util.WeatherUtility;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import co.ghola.smogalert.R;

/**
 * Created by alecksjohansson on 7/29/16.
 */
public class WeatherUtil {

    public static int getResource(float val, WeatherConfig config) {
        float temp = val;
        if (!WeatherUtility.isMetric(config.unitSystem))
            temp = UnitUtility.toCelcius(val);

        int resId = 0;
        if (temp < 10)
            resId = R.drawable.line_shape_blue;
        else if (temp >= 10 && temp <=24)
            resId = R.drawable.line_shape_green;
        else if (temp > 25)
            resId = R.drawable.line_shape_red;

        return resId;

    }

    public static String getLanguage(String val) {
        if (val == null)
            return "en";

        if (val.equalsIgnoreCase("system"))
            return Locale.getDefault().getLanguage();

        return null;
    }


    public static String convertDate(long unixTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(unixTime * 1000);
        sdf.setTimeZone(cal.getTimeZone());
        return sdf.format(cal.getTime());
    }
}
