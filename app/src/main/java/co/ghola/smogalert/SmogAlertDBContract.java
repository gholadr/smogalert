package co.ghola.smogalert;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by gholadr on 4/1/16.
 */
public class SmogAlertDBContract {

    public static final String DATABASE_NAME = "smogalertdb";

    public static final int DATABASE_VERSION = 1;

    public static final String CONTENT_AUTHORITY = "co.ghola.smogalert";

    /**
     * Base URI. (content://com.example.android.network.sync.basicsyncadapter)
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Path component for "entry"-type resources..
     */
    private static final String PATH_AQIS = "air_quality_sample";

    private SmogAlertDBContract() {}

    public static abstract class AirQualitySample implements BaseColumns {

        /**
         * MIME type for lists of aqis.
         */
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.basicsyncadapter.aqis";
        /**
         * MIME type for individual aqis.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.basicsyncadapter.aqi";

        /**
         * Fully qualified URI for "entry" resources.
         */
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_AQIS).build();

        public static final String TABLE_NAME = "air_quality_sample";
        //public static final String COLUMN_NAME_ID ="id";
        public static final String COLUMN_NAME_AQI = "aqi";
        public static final String COLUMN_NAME_MESSAGE = "message";
        public static final String COLUMN_NAME_TS = "ts";

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
