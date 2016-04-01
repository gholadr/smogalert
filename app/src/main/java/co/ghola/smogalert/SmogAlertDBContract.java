package co.ghola.smogalert;

import android.provider.BaseColumns;

/**
 * Created by gholadr on 4/1/16.
 */
public class SmogAlertDBContract {

    public static final String DATABASE_NAME = "smogalertdb";

    public static final int DATABASE_VERSION = 1;

    private SmogAlertDBContract() {}

    public static abstract class AirQualitySample implements BaseColumns {

        public static final String TABLE_NAME = "air_quality_sample";
        public static final String COLUMN_NAME_AQI = "aqi";
        public static final String COLUMN_NAME_TS = "timestamp";
        public static final String COLUMN_NAME_MESSAGE = "message";
    }

    public static final String SQL_CREATE_AQI = String.format(
            "CREATE TABLE %s ( %s TEXT, %s TEXT, %s LONG)",
            AirQualitySample.TABLE_NAME,
            AirQualitySample.COLUMN_NAME_AQI,
            AirQualitySample.COLUMN_NAME_MESSAGE,
            AirQualitySample.COLUMN_NAME_TS);

    public static final String SQL_DROP_AQI = String.format(
      "DROP TABLE IF EXISTS %s;", AirQualitySample.TABLE_NAME);
}
