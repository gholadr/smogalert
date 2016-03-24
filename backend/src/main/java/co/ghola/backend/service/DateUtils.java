package co.ghola.backend.service;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

/**
 * Created by macbook on 3/23/16.
 */
public class DateUtils {
    private static DateUtils ourInstance = new DateUtils();

    private static final Logger log = Logger.getLogger(DateUtils.class.getName());
    private static DateTimeFormatter format =  DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    public static DateUtils getInstance() {
        return ourInstance;
    }

    private DateUtils() {
    }

    public long dateString2Long(String date)
    {
        long time = 0;

        LocalDateTime d = format.parseLocalDateTime(date);
        DateTime utc = d.toDateTime(DateTimeZone.UTC);
        time = utc.getMillis() / 1000;

        return time;
    }
}
